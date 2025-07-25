package kuke.board.article.service.response;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ArticlePageResponse {

    private List<ArticleResponse> articles;
    private Long articleCount;

    public static ArticlePageResponse of(List<ArticleResponse> articles, Long articleCount) {
        ArticlePageResponse response = new ArticlePageResponse();
        response.articles = articles;
        response.articleCount = articleCount;
        return response;
    }

}
