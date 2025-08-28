package kuke.board.articleread.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentClient {
    private RestClient restClient;
    @Value("{enendpoints.kuke-board-comment-service.url}")
    private String commentServiceUrl;

    @PostConstruct
    public void initRestClient() {
        restClient = RestClient.create(commentServiceUrl);
    }

    public long count(Long articleId) {
        try {
            return restClient.get()
                    .uri("/v2/comments/articles/{articleId}/count", articleId)
                    .retrieve()
                    .body(Long.class);
        } catch (Exception e) {
            log.error("[CommentClient.read] articleId={}", articleId, e);
            return 0;
        }
    }



}
