# -------------------------------
# 1. deps stage: 의존성 미리 받아두기
# -------------------------------
FROM eclipse-temurin:21-jdk-jammy as deps
WORKDIR /build

# Gradle 래퍼와 설정 파일만 먼저 복사 (의존성 캐시를 활용하기 위함)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# BuildKit 캐시를 /root/.gradle에 마운트 → Gradle 의존성 다운로드 캐시
# 테스트 제외(-x test). 실패해도 캐시만 채워지도록 || true
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon build -x test || true

# -------------------------------
# 2. build stage: 실제 JAR 빌드
# -------------------------------
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /build

# 나머지 전체 소스 복사
COPY . .
RUN chmod +x gradlew

# Gradle 빌드 수행 (BuildKit 캐시 적용)
# - clean bootJar 실행, test는 제외
# - 산출된 JAR 파일을 app.jar로 복사
#   (스냅샷 버전이면 *-SNAPSHOT.jar, 아니면 *.jar)
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew --no-daemon clean bootJar -x test && \
    cp build/libs/*-SNAPSHOT.jar app.jar || cp build/libs/*.jar app.jar

# -------------------------------
# 3. final stage: 빌드가 끝난 결과물을 실행용 이미지로 패키징
# -------------------------------
FROM eclipse-temurin:17-jre-jammy AS final

# health check를 위해 curl 설치
USER root
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# 런타임 컨테이너는 비루트(non-root) 유저로 실행하기 위해 UID 지정
ARG UID=10001
RUN adduser --disabled-password --gecos "" \
    --home "/nonexistent" --shell "/sbin/nologin" \
    --no-create-home --uid "${UID}" appuser

# 앞으로 실행은 appuser 권한으로 수행
USER appuser
WORKDIR /app

# build stage에서 빌드된 app.jar만 가져오기
COPY --from=build /build/app.jar app.jar

# 컨테이너에서 외부로 노출할 포트
EXPOSE 8080

# 애플리케이션 실행 명령 (java -jar app.jar)
ENTRYPOINT ["java","-jar","app.jar"]