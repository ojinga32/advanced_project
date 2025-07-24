package kuke.board.hotarticle.service.response;

import kuke.board.hotarticle.client.ArticleClient;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class HotArticleResponse {

    private Long articleId;
    private String title;
    private LocalDateTime createAt;

    public static HotArticleResponse from(ArticleClient.ArticleResponse articleResponse) {
        HotArticleResponse response = new HotArticleResponse();
        response.articleId = articleResponse.getArticleId();
        response.title = articleResponse.getTitle();
        response.createAt = articleResponse.getCreatedAt();
        return response;
    }

}
