📌 MSA 대규모 트래픽 게시판 시스템

Redis 기반 캐싱, Kafka + Outbox 패턴, 샤딩 구조까지 포함한 고성능·확장형 게시판 시스템

📎 Repository

👉 https://github.com/ojinga32/advanced_project

🚀 프로젝트 개요

프로젝트명: MSA 기반 대규모 트래픽 게시판 시스템

목적: 초대규모 트래픽에서도 안정적인 게시글·댓글·조회수·좋아요 서비스를 제공하기 위한 확장형 아키텍처 구축

성과

Redis 기반 조회수 처리 성능 3배 향상

Kafka + Outbox 패턴으로 데이터 유실 0%

인기글 비동기 집계 → DB 부하 감소

MSA 구조 + 서비스별 독립 DB 구성

샤딩 설계로 수평 확장 가능 구조 구현

🏗 전체 아키텍처
Client
  ↓
API Gateway
  ↓
[Board Service] — MySQL(sharded)
[Comment Service] — MySQL(sharded)
[Like Service]
[ViewCount Service] — Redis
[Popular Service]
  ↑
Kafka (Event Stream)
  ↑
Outbox Processor

🛠 기술 스택
분야	기술
Backend	Java 17, Spring Boot 3, JPA, QueryDSL
Messaging	Apache Kafka
Database	MySQL, Redis
Infra	Docker
Docs	Swagger(OpenAPI 3.0)
Test	JUnit5, Mockito
📌 주요 기능 (서비스 관점)
📝 게시글

CRUD

페이지네이션

무한스크롤

게시글 카운트

💬 댓글

댓글 작성/삭제

2 Depth 트리 구조

무한 Depth 구조 (Path 기반)

무한스크롤

댓글 개수

👁 조회수

Redis INCR 기반 Lock-free 처리

TTL 기반 어뷰징 방지

MySQL 백업 전략 적용

👍 좋아요

좋아요/취소

낙관적 락

비관적 락 (2종)

좋아요 개수 조회

🔥 인기글

게시글/댓글/좋아요/조회수 이벤트 기반 집계

Kafka + Outbox 패턴으로 일관성과 무손실 전송 보장

⚡ 핵심 기술 설계
1️⃣ DB 샤딩 (Sharding)
✅ 왜 샤딩을 도입했는가?

단일 DB로는 수십만~수백만 요청 처리 불가

CPU, IO, 스토리지 병목 발생

데이터 증가에 따라 수평 확장 가능하도록 설계

✅ 효과

DB 부하 분산

고동시성 요청에서도 안정적 처리

캐싱 + 샤딩 조합으로 조회 성능 극대화

2️⃣ Snowflake ID 생성기
📌 왜 필요한가?

Auto Increment는 분산 환경에서 충돌 발생

DB 락 문제 → 고성능 시스템에 부적합

📌 특징

64bit 고유 ID 생성

Node ID + Timestamp + Sequence

정렬 가능 → 페이징/검색 최적화

초당 수십만 건 생성

구조
| 1bit | 41bit Timestamp | 10bit NodeID | 12bit Sequence |

3️⃣ 댓글 시스템
🔹 2 Depth 댓글 설계
정렬 규칙

parent_comment_id ASC, comment_id ASC

인덱스 설계
CREATE INDEX idx_comment_list
ON comment (article_id, parent_comment_id, comment_id);

페이징

Offset 방식

Keyset 기반 무한스크롤 방식

🔹 무한 Depth 댓글 설계 — Path Enumeration
핵심 아이디어

각 댓글의 전체 계층 경로를 문자열 Path로 저장
예:

00000  
00000 00001  
00000 00001 00003

Path 특징

5자리 62진법 문자열 (0-9A-Za-z)

정렬 시 계층 구조 그대로 노출

utf8mb4_bin Colation 사용 (정확한 정렬)

Path 기반 조회
ORDER BY path ASC LIMIT ?

4️⃣ 조회수 시스템 – Redis 기반 고성능 구조
문제점

DB UPDATE 폭주 → Row Lock 경합

어뷰징 방지 어려움

해결 방식

✔ Redis INCR → Lock-free
✔ TTL + SETNX → 어뷰징 방지
✔ AOF + MySQL 백업 → 영속성 확보

5️⃣ Kafka 기반 인기글 시스템
필요한 이유

게시글/댓글/좋아요/조회수 모두 이벤트 발생

동기 호출 시 장애 전파 → 위험

Kafka는 고성능·확장성·복원력 확보

⚠ 문제: 데이터 유실 가능성

DB는 저장됐는데 Kafka 전송 실패 가능함

해결책: Transactional Outbox 패턴

DB 트랜잭션 내부에서 Outbox 테이블 저장

커밋 후 별도 프로세스가 Kafka 전송

Kafka 전송 성공 시 Outbox 상태 업데이트

장점

DB와 이벤트 원자성 보장

메시지 유실 0%

장애 시 Outbox 재처리 가능

📄 API 명세

👉 https://www.notion.so/API-25bcdb9aab70806eb169d249df9011f9?pvs=21

(Swagger로 자동 문서화)

🧪 테스트 코드 (JUnit + Mockito)
테스트 범위

게시글 CRUD

댓글 생성/트리 구조 테스트

좋아요 테스트

조회수 테스트(Redis Mock)

인기글 이벤트 테스트

Outbox 저장 및 Kafka 발행 검증

대용량 테스트(30,000,000 건)

예시 코드
@Test
void create() {
    CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
    CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));

    System.out.println(response1);
    System.out.println(response2);
}

📂 프로젝트 구조
src
 ├─ api
 ├─ domain
 ├─ application
 ├─ infrastructure
 ├─ global
 ├─ test

🔥 핵심 요약
✔ 초대규모 트래픽 대응을 위한 구조

DB 샤딩

Redis 캐싱

Kafka 기반 비동기 이벤트 처리

✔ 데이터 정합성 보장

Outbox 패턴

Eventually Consistency 적용

✔ 고성능 댓글 구조

2 Depth / 무한 Depth 모두 지원

Path 기반 정렬로 조회 최적화
