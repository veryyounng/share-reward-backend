# 📌 Share & Reward (Backend)

**실시간 이벤트 기반 리워드 시스템**  
사용자가 상품 링크를 공유하면, 친구가 해당 링크를 클릭했을 때 포인트가 적립됩니다.  
Kafka를 중심으로 서비스 간 이벤트를 주고받으며, Spring Boot 기반 백엔드로 구현했습니다.

---

## 🏗️ 아키텍처

- **Frontend**: React (Vite, Tailwind)  
- **Backend**: Spring Boot (Java 21, Gradle, JPA, JWT)  
- **Event Streaming**: Apache Kafka (멀티 브로커 클러스터)  
- **Infra**: Docker Compose, AWS EC2  
- **Database/Cache (추가 예정)**: Redis  

**이벤트 흐름**
1. `share-service`: 상품 공유 시 `share.created.v1` 이벤트 발행  
2. `click-tracker`: 공유된 링크 클릭 시 `click.tracked.v1` 이벤트 발행  
3. `reward-service`: 클릭 이벤트를 수신해 `reward.granted.v1` 이벤트 발행  

---

## ✨ 주요 기능

- **상품 공유**: API 호출 시 Kafka 토픽에 `share.created` 이벤트 발행  
- **클릭 추적**: 공유된 링크 클릭 이벤트를 Kafka로 수집  
- **리워드 지급**: 클릭 이벤트를 구독하여 보상 지급 처리  
- **Kafka UI**를 통한 토픽/메시지 흐름 모니터링  

---

## 🚀 실행 방법

```bash
# 1. 저장소 클론
git clone https://github.com/veryyounng/share-reward-backend.git
cd share-reward-backend

# 2. Docker Compose로 Kafka 실행
docker-compose up -d

# 3. Spring Boot 실행
./gradlew bootRun
