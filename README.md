# FitU Backend

> 학교 단위 운동 커뮤니티 **FitU**의 메인 API 서버입니다.
> 피드 · 운동 기록 · 랭킹 · 인증을 담당하며, 실시간 채팅은 [별도 서비스](https://github.com/jihyun109/fitu-chat-service)로 분리되어 있습니다.

**관련 레포지토리** — [fitu-chat-service](https://github.com/jihyun109/fitu-chat-service) · [monitoring-infra](https://github.com/jihyun109/monitoring-infra)

> 실사용자를 대상으로 운영한 서비스는 아닙니다. 이 문서의 모든 수치는 k6로 가상 사용자를 생성해 개선 전후를 동일 조건에서 측정한 값입니다.

---

## 코드를 먼저 보신다면

| 관심사 | 파일 |
|---|---|
| 트랜잭션 경계 밖으로 외부 I/O 분리 | [`facade/SessionFacade.java`](src/main/java/com/hsp/fitu/facade/SessionFacade.java) |
| 배치 INSERT | [`repository/WorkoutBulkRepository.java`](src/main/java/com/hsp/fitu/repository/WorkoutBulkRepository.java) |
| JWT 인증 필터 | [`jwt/JwtAuthenticationFilter.java`](src/main/java/com/hsp/fitu/jwt/JwtAuthenticationFilter.java) |
| Refresh Token 회전 (Lua 원자적 처리) | [`jwt/RefreshTokenStore.java`](src/main/java/com/hsp/fitu/jwt/RefreshTokenStore.java) |
| 무중단 배포 · probe 설정 | [`k8s/workloads/deployment-app.yaml`](k8s/workloads/deployment-app.yaml) |
| OpenTelemetry 자동 계측 | [`k8s/monitoring/instrumentation-fitu.yaml`](k8s/monitoring/instrumentation-fitu.yaml) |

---

## 기술 스택

| 분야 | 기술 | 선택 근거 |
|---|---|---|
| Language / Framework | Java 17, Spring Boot 3.2.5 | — |
| 인증 | Spring Security, JWT, Redis | Stateless 구성으로 레플리카 확장에 제약이 없도록. Refresh Token 회전 상태는 Redis에서 원자적으로 관리 |
| 데이터 | Spring Data JPA, MySQL 8 (AWS RDS), Redis 7 | 서비스 데이터는 정합성이 중요해 RDB 유지 |
| 파일 | AWS S3 | — |
| 인프라 | Kubernetes (k3d / k3s), GitHub Actions | Operator 기반 자동 계측과 probe 분리를 위해 Swarm에서 전환 |
| 관측 | Prometheus, Grafana, OpenTelemetry, Tempo, Pyroscope | 지표 · 트레이스 · 프로파일을 한 화면에서 교차 분석 |
| 테스트 | JUnit, Mockito, k6 | — |

## 아키텍처

```
                        ┌─────────────┐
        요청 ─────────▶ │   Ingress   │
                        └──────┬──────┘
              /ws, /api/v2/chat│  그 외
                    ┌──────────┴──────────┐
                    ▼                     ▼
          ┌──────────────────┐   ┌──────────────────┐
          │  chat-service    │   │   app (이 레포)   │
          │  레플리카 2       │   │   레플리카 2      │
          └────────┬─────────┘   └─────────┬────────┘
                   ▼                       ▼
              MongoDB                MySQL (RDS) · S3
                   └────────── Redis ──────┘
```

채팅과 REST API는 트래픽 성격이 달라 **스케일 기준이 다릅니다.** 채팅은 동시 접속자 수, API는 요청 수를 기준으로 확장해야 하므로 서비스와 저장소를 분리했습니다. 분리 배경과 측정 근거는 [chat-service README](https://github.com/jihyun109/fitu-chat-service)에 정리했습니다.

---

## 핵심 구현

### 1. 운동 기록 저장 API의 커넥션 풀 고갈 해소

**문제** — 동시 130~150건 구간에서 실패율 **62.8%**, 커넥션 대기 요청이 최대 **175건**까지 누적됐습니다.

**원인 분석** — DB 서버 CPU는 44%로 여유가 있어 애플리케이션 측 병목으로 판단했습니다. 요청당 커넥션 점유 시간이 긴 원인은 두 가지였습니다.

1. S3 업로드가 트랜잭션 내부에 포함되어, 외부 네트워크 I/O가 끝날 때까지 커넥션이 불필요하게 점유됨
2. 운동 세트 25건을 건별 INSERT해 DB 왕복이 25회 발생

**조치**

- 트랜잭션을 갖지 않는 [`SessionFacade`](src/main/java/com/hsp/fitu/facade/SessionFacade.java)를 두어 S3 업로드를 트랜잭션 경계 **밖에서 선행 처리**한 뒤 DB 작업만 위임
- 세트 저장을 [`WorkoutBulkRepository`](src/main/java/com/hsp/fitu/repository/WorkoutBulkRepository.java)의 `JdbcTemplate.batchUpdate()` 기반 배치 INSERT로 전환

> JPA 배치는 적용할 수 없었습니다. 기본 키 생성 전략이 `IDENTITY`라 INSERT를 실행해야 키를 확인할 수 있어 쓰기 지연 저장소에 모아 보낼 수 없기 때문입니다. MySQL은 시퀀스 객체가 없어 `SEQUENCE`도 `TABLE` 전략으로 대체되는데, 이 경우 시퀀스 테이블의 행이 새로운 경합 지점이 되어 채택하지 않았습니다.

**결과** — TPS **101 → 126.6 (+25.3%)**, 대기 커넥션 **175건 → 43건 (−75%)**

### 2. 무중단 배포

기존에는 서버에 접속해 프로세스를 수동 재시작하는 방식이라 **배포마다 약 10분간 서비스가 중단**됐고 롤백 수단도 없었습니다.

- GitHub Actions 기반 자동 빌드·배포, 신규 컨테이너 준비 후 기존 컨테이너를 교체하는 롤링 업데이트
- 이미지 태그를 **커밋 해시로 고정** → 이전 태그로 되돌리는 것이 곧 롤백
- 종료 유예를 **Kubernetes 30초 / 애플리케이션 20초**로 설정해 애플리케이션이 항상 먼저 정리를 완료
- 배포 중 k6로 트래픽을 지속 발생시켜 **실패율 0%** 확인

**probe를 3종으로 분리한 이유** ([deployment-app.yaml](k8s/workloads/deployment-app.yaml))

| Probe | 검사 대상 | 목적 |
|---|---|---|
| `startupProbe` | 프로세스 기동 | 기동에 최대 3분 허용, 느린 시작으로 인한 재시작 루프 방지 |
| `livenessProbe` | 프로세스만 | **RDS 일시 장애 시 불필요한 Pod 재시작 방지** |
| `readinessProbe` | DB · Redis 의존성 포함 | 의존성 장애 시 트래픽만 차단하고 Pod는 유지 |

### 3. 관측 환경

서비스를 분리한 뒤 하나의 요청이 서비스 경계를 넘나드는 경로를 확인할 수 없어, OpenTelemetry Operator 기반 자동 계측을 도입했습니다. Pod에 **어노테이션 한 줄**을 추가하면 agent가 주입됩니다. ([instrumentation-fitu.yaml](k8s/monitoring/instrumentation-fitu.yaml))

```yaml
annotations:
  instrumentation.opentelemetry.io/inject-java: "fitu/fitu-java"
```

설정에서 신경 쓴 부분은 세 가지입니다.

- **샘플링** — `parentbased_traceidratio`로 구성해 하나의 트레이스가 서비스 간에 조각나지 않도록 함
- **PII** — HTTP 헤더 자동 캡처를 비활성화하고 DB statement sanitizer를 활성화해 `Authorization` 헤더·쿼리 파라미터가 트레이스에 실리지 않도록 차단
- **전송** — Collector 일시 장애에 대비한 메모리 큐 구성

### 4. 인증 — Refresh Token 회전과 재사용 탐지

Kakao OAuth 2.0으로 로그인하고 자체 JWT를 발급합니다. Access Token은 응답 헤더로, Refresh Token은 `HttpOnly` 쿠키로 분리 발급하며, 세션은 Stateless로 운영해 레플리카 확장에 제약이 없습니다.

재발급 시 Refresh Token을 **회전**시키는데, 이때 "기존 토큰 검증 → 소비 처리 → 새 토큰 발급"을 개별 명령으로 처리하면 **동일 토큰으로 두 요청이 동시에 들어올 때 둘 다 통과**할 수 있습니다. 검증과 상태 변경 사이에 다른 요청이 끼어들 수 있기 때문입니다.

이를 막기 위해 검증과 소비를 **Lua 스크립트 하나로 묶어 Redis 서버에서 원자적으로 실행**했습니다. 동시에 같은 토큰으로 두 요청이 와도 정확히 하나만 `OK`를 받고 나머지는 `REUSE_DETECTED`를 반환합니다. ([`jwt/RefreshTokenStore.java`](src/main/java/com/hsp/fitu/jwt/RefreshTokenStore.java))

| 반환값 | 상황 | 처리 |
|---|---|---|
| `OK` | 정상 소비 | 새 토큰 발급 |
| `REUSE_DETECTED` | 이미 소비된 토큰 재사용 → **탈취 의심** | 해당 토큰 가족(family) 전체 무효화 |
| `FAMILY_REVOKED` | 무효화된 가족의 토큰 | 거부 |
| `NOT_FOUND` | 만료 · 위조 | 거부 |

토큰에 `familyId`를 부여해 하나의 로그인 세션에서 파생된 토큰들을 묶었습니다. 재사용이 탐지되면 그 세션에서 발급된 토큰을 한 번에 무효화합니다.

인증 흐름을 문서화하는 과정에서 프론트엔드가 `accessToken` 필드를 읽는데 백엔드는 `token` 필드를 반환해 **자동 재발급이 실제로는 동작하지 않던 문제**를 포함해 코드와 의도가 어긋난 8건을 발견하고 정리했습니다.

---

## 실행

```bash
./gradlew clean bootJar          # 빌드
docker build -t fitu-backend .   # 이미지 빌드
kubectl apply -f k8s/            # 배포
```

로컬 실행에는 MySQL · Redis 접속 정보가 필요합니다. `k8s/app-secret.yml`은 저장소에 포함하지 않으며 `kubectl create secret`으로 생성합니다.

## 프로젝트 구조

```
src/main/java/com/hsp/fitu/
├── controller/   REST 엔드포인트 (25)
├── service/      비즈니스 로직
├── facade/       트랜잭션 경계 밖 오케스트레이션 (외부 I/O 분리)
├── repository/   Spring Data JPA + JdbcTemplate 배치
├── entity/       JPA 엔티티
├── jwt/          토큰 생성 · 검증 · 블랙리스트
└── config/       Security, Redis, S3 설정

k8s/
├── workloads/    Deployment, StatefulSet
├── monitoring/   OTel Operator, Collector, ServiceMonitor, Tempo
└── network/      Ingress
```

## 알려진 한계

- 세트 저장이 영속성 컨텍스트 밖에 있어 저장 로직이 이원화되어 있습니다. 스키마 변경 시 엔티티와 SQL 문자열을 함께 수정해야 하며 컴파일 타임에 검증되지 않습니다.
- S3 업로드를 트랜잭션 밖으로 분리한 대가로, 업로드 성공 후 DB 저장이 실패하면 참조되지 않는 객체가 S3에 남습니다. 정리 배치가 필요합니다.
- JDBC URL에 `rewriteBatchedStatements=true`가 설정되어 있지 않아 드라이버가 INSERT를 단일 구문으로 병합하지 않습니다. **현재 +25.3%는 이 옵션 없이 얻은 수치**이며 개선 여지가 남아 있습니다.
- 테스트가 일부 도메인(친구 · 게시글 · 운동 세션)에 한정되어 있습니다.

## 문서

- [채팅 아키텍처](docs/chat-architecture.md) — 분리 이전 설계 기록
- [WebSocket 동작 분석](docs/chat-websocket-architecture.md)
