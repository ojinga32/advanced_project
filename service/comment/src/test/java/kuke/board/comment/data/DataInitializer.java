package kuke.board.comment.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kuke.board.comment.entity.Comment;
import kuke.board.common.snowflake.Snowflake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// select * from (select article_id from article where board_id = 1 order by article_id desc limit 30 offset 100000) t left join article on t.article_id = article.article_id;

//select * from article where board_id = 1 order by article_id desc limit 30 offset 100000

@SpringBootTest
public class DataInitializer {

    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    TransactionTemplate transactionTemplate;
    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    static final int BULK_INSERT_COUNT = 6000;
    static int EXECUTE_COUNT = 2000;


    @Test
    void inittialize() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 0; i < EXECUTE_COUNT; i++) {
            executorService.submit(() -> {
                insert();
                latch.countDown();
                System.out.println("latch.getCount() = " + latch.getCount());
            });
        }
        latch.await();
        executorService.shutdown();
    }

    void insert() {
        transactionTemplate.executeWithoutResult(status -> {
            Comment prev = null;

            for(int i = 0; i < BULK_INSERT_COUNT; i++) {
                Comment comment = Comment.create(
                        snowflake.nextId(),
                        "conent",
                        i % 2 == 0 ? null : prev.getCommentId(),
                        1L,
                        1L
                );

                prev = comment;

                entityManager.persist(comment);
            }
        });
    }








}
