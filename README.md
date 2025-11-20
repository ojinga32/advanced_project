📘 대규모 트래픽 대응 게시판 시스템

Sharding · Redis · Kafka · Outbox · 무한 Depth 댓글 구조 · Snowflake ID

대규모 트래픽을 견딜 수 있는 게시판 시스템을 직접 설계·구현한 프로젝트입니다.  
실제 SNS 서비스들이 사용하는 아키텍처 요소들을 적용하여 확장성과 안정성을 확보했습니다.

🚀 핵심 기능 요약

🔥 트래픽 대응 핵심 요소

- DB Sharding으로 부하 분산 및 수평 확장  
- Redis Atomic INCR + TTL → 조회수 3배 성능 향상 & 어뷰징 방지  
- Kafka + Outbox 패턴 → 이벤트 유실 0%  
- Snowflake ID(64bit) → 분산 환경에서도 유일 ID 생성  
- 무한 Depth 댓글 구조 → 문자열 기반 Path(62진법) 정렬  
- 인기글 집계 → Kafka Consumer로 비동기 집계  

🧱 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3, JPA, QueryDSL |
| DB | MySQL, Redis |
| Message | Kafka |
| Infra | Docker |
| ETC | Swagger, JUnit, Mockito |

🗂 주요 기능 상세

📝 **게시판**
- 게시글 CRUD  
- 페이지네이션 & 무한스크롤  
- 게시글 개수 조회  

💬 **댓글**
- 2 Depth 댓글  
- parent_id, comment_id 기반 정렬  
- 무한 Depth 구조  
- 문자열 Path + 62진법 정렬  
- 트리 전체 조회 성능 최적화  
- 무한스크롤 기반 페이징  

❤️ **좋아요**
- 낙관적 락 버전  
- 비관적 락 버전  
→ 동시성 처리 비교/테스트 가능  

👁 **조회수(핵심)**
- Redis INCR 기반 초고속 증가  
- TTL + SETNX로 중복 방지 / 어뷰징 방지  
- Redis → MySQL 백업(동기 스냅샷)

🔥 **인기글**
- Kafka Producer  
- 게시글 조회/좋아요 발생 시 이벤트 push  
- Outbox 패턴 적용  
- DB 트랜잭션과 Kafka 송신 분리  

- Kafka Consumer  
- 비동기 집계 → MySQL 저장  

❄️ **Snowflake ID**
- 64bit 기반 분산 ID 생성 방식  
- 시간순 정렬 가능  
- 충돌 없이 대량 생성 가능  
- Kafka·Redis·클러스터 환경에서 안전  

🔧 **아키텍처 개요**

[Client]  
   |  
   v  
[Spring API Server]  
   |-- Redis (조회수/캐싱)  
   |-- MySQL Shards  
   |-- Outbox Table  
   |  
   v  
[Kafka Producer] ----> [Kafka Broker] ----> [Kafka Consumer]  
                                                |  
                                                v  
                                      [Popular Post DB]  

🧪 테스트
- JUnit 단위 테스트  
- Mockito 기반 Mock 테스트  
- Kafka / Redis 단위 테스트  
- 3천만 건 대용량 성능 검증  

🛠 실행 방법
git clone https://github.com/your-repo/project.git

cd project
docker compose up -d --build


환경 변수는 `.env`에서 관리합니다.

📌 **한 줄 요약**
> “대규모 트래픽 환경을 가정해  
> 샤딩, 캐싱, 비동기 메시징, 무한 Depth 구조 등  
> 실제 대형 서비스 아키텍처를 직접 설계하여 구현한 프로젝트입니다.”
```

##노션주소
https://www.notion.so/MSA-25bcdb9aab7080159de3d0c1013eaeab
