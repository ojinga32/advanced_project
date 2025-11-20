인기글 집계 (Kafka + Outbox)

DB 트랜잭션 내 Outbox 테이블에 이벤트 저장

Message Relay가 Outbox 읽어 Kafka로 전송 → Consumer가 인기글 집계

장점: DB 상태와 이벤트 일관성 보장, 메시지 유실 제거
