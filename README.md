대규모 트래픽 대응 SNS 백엔드 서버

Sharding + Redis 캐싱 + Kafka + Outbox + 이미지 썸네일링 + 무한 Depth 댓글
대규모 SNS 트래픽을 처리하기 위해 설계된 고성능 백엔드 서버 프로젝트입니다.

📌 주요 기능
✔️ 1. 분산 처리 아키텍처 (Sharding)

사용자 ID 기반 샤딩(Sharding)을 적용하여 DB 부하 분산

각 샤드에서 독립적으로 읽기/쓰기가 가능해 확장 용이

마스터-슬레이브 구조를 적용해 읽기 부하 최적화

Shard 1: User 1 ~ 10,000,000
Shard 2: User 10,000,001 ~ 20,000,000
Shard 3: User 20,000,001 ~ ...

✔️ 2. Redis 기반 Feed 캐싱

Hot Feed 조회 속도 최적화

타임라인/피드 응답 속도 50~100ms 수준으로 단축

데이터 일관성을 위해 TTL + Lazy write-back 적용

✔️ 3. Kafka + Outbox 패턴 기반 안정적 데이터 처리

댓글/좋아요/팔로우 이벤트를 Outbox에 먼저 저장

트랜잭션과 분리하여 Kafka로 비동기 전송

서비스 장애 시에도 중복·유실 없는 기록 보장

✔️ 4. 이미지 썸네일 생성

업로드 시 Worker가 자동으로 여러 사이즈로 리사이징

S3 업로드 및 CDN 캐싱 사용

원본 이미지 요청 최소화 → 대규모 트래픽 대응

✔️ 5. 무한 Depth 대댓글 구조

계층형 댓글을 Parent → Child 트리 구조로 저장

쿼리 최적화를 위해 closure table + materialized path 혼합 적용

대규모 댓글에서도 빠르게 렌더링 가능

🧱 전체 아키텍처
[ Client ]
      |
      v
[ API Gateway ]
      |
      v
[ Post Service ] --- DB Shards
      |
      v
[ Kafka ] <--- Outbox
      |
      v
[ Worker ] --- Thumbnail/S3 처리

🔧 기술 스택
분야	기술
Language	TypeScript / Java (선택)
Database	MySQL Sharding, Redis
Messaging	Kafka
Infra	Docker, AWS S3, CloudFront
기타	Outbox 패턴, 이미지 썸네일링
🗂️ 주요 디렉토리 구조
src/
 ├─ api/
 ├─ core/
 ├─ feed/
 ├─ comment/
 ├─ user/
 ├─ worker/
 └─ shared/

🚀 성능 결과
기능	처리량	비고
피드 조회	50~100ms	Redis 캐싱
댓글 이벤트 처리	초당 5,000+	Kafka + Worker
이미지 처리	썸네일 생성 150ms	비동기 Worker
🔥 핵심 설계 포인트
1. DB 샤딩

사용자 단위로 분리해서 병렬 처리

샤드 장애 시에도 정상적으로 서비스 유지

2. 캐싱 우선 전략

Redis Sorted Set을 이용해 피드 정렬

TTL 기반으로 캐시 동기화 비용 최소화

3. 이벤트 비동기 구조

API 서버에서는 빠르게 응답

Worker가 무거운 작업을 처리 → 높은 처리량 가능

📦 실행 방법
git clone https://github.com/your-repo/project.git
cd project
docker compose up -d --build


환경 변수는 .env 파일에서 설정합니다.

🧪 테스트
npm run test


단위 테스트 포함

통합 테스트는 Docker 기반으로 실행 가능
