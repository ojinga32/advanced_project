package kuke.board.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleViewCountRepository {
    private final StringRedisTemplate redisTemplate;

    // view::article::{article_id}::view_count
    private static final String KEY_FORMA = "view::article::%s::view_count";

    public Long read(Long articleId) {
        String result = redisTemplate.opsForValue().get(generateKey(articleId));
        return result== null ? 0L : Long.valueOf(result);
    }

    public Long increase(Long articleId) {
        return redisTemplate.opsForValue().increment(generateKey(articleId));
    }

    private String generateKey(Long articleId) {
        return KEY_FORMA.formatted(articleId);
    }

}
