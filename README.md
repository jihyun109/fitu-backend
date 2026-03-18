# FitU Backend

> **핏 유(FitU)** — 피트니스 소셜 플랫폼의 백엔드 서버입니다.
> 실시간 채팅, 운동 기록, 소셜 피드 기능을 제공하며, Docker Swarm 기반 무중단 배포 환경에서 운영됩니다.

**연관 레포지토리:** [fitu-frontend](https://github.com/jihyun109/fitu-frontend) | [monitoring-infra](https://github.com/jihyun109/monitering-infra)

---

## 기술 스택

| 분야 | 기술 |
|------|------|
| Language / Framework | Java 17, Spring Boot 3.2.5 |
| 인증 | Spring Security, JWT (Access Token 1h + Refresh Token 24h) |
| 데이터 | Spring Data JPA, MySQL 8 (AWS RDS), Redis 7 |
| 실시간 통신 | WebSocket, STOMP, Redis Pub/Sub, Redis Stream |
| 파일 저장 | AWS S3 |
| 인프라 | Docker Swarm (2 replicas), Nginx, GitHub Actions CI/CD |
| 모니터링 | Spring Actuator, Prometheus, Grafana |
| 테스트 | k6 (부하 테스트), JUnit, Jest + axios-mock-adapter |

---

## 아키텍처

### 실시간 채팅 플로우

```
클라이언트 → STOMP SEND → Spring WebSocket → DB 저장
                                             → Redis PUBLISH (chat:messages)
Redis → 모든 인스턴스 SUBSCRIBE → SimpleBroker → 연결된 클라이언트 브로드캐스트
```

멀티 레플리카 환경에서 어느 서버에 연결된 사용자에게도 메시지가 도달할 수 있도록 **Redis Pub/Sub 기반 수평 확장 구조**를 직접 설계했습니다.

### 인증 플로우

```
Kakao OAuth 2.0 → Backend JWT 발급 (Access 1h + Refresh 24h HttpOnly Cookie)
→ Frontend Axios 인터셉터 401 감지 → 자동 토큰 갱신
```

### 프로젝트 구조

```
src/main/java/com/hsp/fitu/
├── controller/      # REST + WebSocket 엔드포인트 (~28)
├── service/         # 비즈니스 로직 (~50+ 서비스)
├── entity/          # JPA 엔티티 (~30)
├── repository/      # Spring Data JPA 리포지토리 (~29)
├── dto/             # 요청/응답 DTO (~83)
├── jwt/             # JWT 생성 및 검증
├── messaging/       # 실시간 채팅 인프라 (Redis Pub/Sub, Stream)
├── facade/          # S3 + DB 트랜잭션 분리 레이어
└── config/          # WebSocket, Security, Redis, S3 설정
```

---

## 주요 구현 및 성과

### 실시간 채팅 시스템

- `WebSocketAuthChannelInterceptor` — STOMP CONNECT 프레임에서 JWT 검증
- `MessageBrokerPort` 인터페이스 — Redis ↔ Kafka 교체 가능한 어댑터 구조 설계
- Redis 메시지 payload에 `roomMemberIds` 포함 → 구독자 측 DB 조회 없이 라우팅
- `/sub/chat/room/list/{userId}` 구독 → 새 메시지 수신 시 채팅방 목록 실시간 갱신

### 채팅 서비스 부하 테스트 및 최적화 (VU 300명)

| 병목 | 해결 방법 | 결과 |
|------|----------|------|
| Redis 리스너 팬아웃 루프 동기 처리 | `ThreadPoolExecutor` 비동기 오프로드 + CallerRunsPolicy 배압 | 인바운드 스레드 점유 ~12ms → ~2ms |
| clientInboundChannel DB 블로킹 | Redis Stream + Worker 패턴으로 DB 접근 분리 | HikariCP 고갈 해소 |
| SimpleBroker 단일 스레드 | heartbeat 비활성화 + Virtual Threads 적용 | outbound 큐 포화 해소 |
| SerialGC + Xmx200m | G1GC + Xmx512m, MaxGCPauseMillis=100 | STW 수백ms → 정상 |

**결과: p95 레이턴시 >3,000ms → <300ms (VU 150명 기준)**

### 운동 세션 API 최적화 (3단계)

| 단계 | 작업 | 효과 |
|------|------|------|
| 1차 | HikariCP 풀 사이즈 10 → 40 | Error Rate 62.8% → 0.14% |
| 2차 | Facade 패턴으로 S3(비트랜잭션) + DB(트랜잭션) 분리 | DB Connection 점유 시간 단축 |
| 3차 | `JdbcTemplate.batchUpdate()` Bulk Insert 적용 | TPS 101 → 126.6 (+25.3%), Pending 175 → 43 |

### N+1 쿼리 제거

채팅방 목록 API에서 상대방 정보를 JPQL self-join으로 단일 쿼리로 조회하도록 변경 → **쿼리 N+1 → 1**

### 배포 / 인프라

- **SockJS 제거 → 네이티브 WebSocket 전환** — Docker Swarm sticky session 의존성 문제 해결, 번들 크기 감소
- **Docker Swarm Immutable Config** — `nginx.conf` MD5 해시를 config 이름에 붙여 rolling update 대응
- **GitHub Actions CI/CD** — 커밋 해시 이미지 태그, 멀티 아키텍처 빌드(M1/Intel), AWS SG 동적 IP 관리
- **무중단 배포 검증** — 배포 중 k6 부하 지속 → 5xx 응답 0건 확인
- **Docker Secret** — `application.yml` 민감 정보 런타임 주입

---

## 빌드 및 실행

```bash
# 환경 변수 설정 (.env)
cp .env.example .env

# 빌드
./gradlew clean bootJar

# Docker 이미지 빌드 (멀티 아키텍처)
docker buildx build --platform linux/amd64,linux/arm64 -t fitu-backend:latest .

# Docker Swarm 배포
docker stack deploy -c compose.yaml fitu
```

---

## 문서

- [채팅 아키텍처](docs/chat-architecture.md)
- [채팅 WebSocket 동작 분석](docs/chat-websocket-architecture.md)
- [채팅 부하 테스트 보고서](../docs/chat-load-test-report.md)
- [채팅 병목 해결 방안](../docs/chat-load-test-bottleneck-solutions.md)
- [Spring Security 학습 노트](docs/Spring_Security_Architecture_공부기록.md)
- [Spring Security JWT 개선 계획서](docs/Spring_Security_JWT_개선_계획서.md)