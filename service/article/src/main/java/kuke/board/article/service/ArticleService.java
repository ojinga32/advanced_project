package kuke.board.article.service;

import kuke.board.article.entity.Article;
import kuke.board.article.entity.BoardArticleCount;
import kuke.board.article.repository.ArticleRepository;
import kuke.board.article.repository.BoardArticleCountRepository;
import kuke.board.article.service.request.ArticleCreateRequest;
import kuke.board.article.service.request.ArticleUpdateRequest;
import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import kuke.board.common.event.EventType;
import kuke.board.common.event.payload.ArticleCreatedEventPayload;
import kuke.board.common.event.payload.ArticleDeletedEventPayload;
import kuke.board.common.event.payload.ArticleUpdatedEventPayload;
import kuke.board.common.outboxmessagerelay.OutboxEventPublisher;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final Snowflake snowflake = new Snowflake();
    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleRepository.save(
                Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId())
        );
        int result = boardArticleCountRepository.increase(request.getBoardId());
        if (result == 0) {
            boardArticleCountRepository.save(
                    BoardArticleCount.init(request.getBoardId(), 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_CREATED,
                ArticleCreatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.getTitle(), request.getContent());

        outboxEventPublisher.publish(
                EventType.ARTICLE_UPDATED,
                ArticleUpdatedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .build(),
                article.getBoardId()
        );

        return ArticleResponse.from(article);
    }

    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.deleteById(articleId);
        boardArticleCountRepository.decrease(article.getBoardId());

        outboxEventPublisher.publish(
                EventType.ARTICLE_DELETED,
                ArticleDeletedEventPayload.builder()
                        .articleId(article.getArticleId())
                        .title(article.getTitle())
                        .content(article.getContent())
                        .boardId(article.getBoardId())
                        .writerId(article.getWriterId())
                        .createdAt(article.getCreatedAt())
                        .modifiedAt(article.getModifiedAt())
                        .boardArticleCount(count(article.getBoardId()))
                        .build(),
                article.getBoardId()
        );
    }

    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return ArticlePageResponse.of(
                articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize).stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.calculatePageLimit(page, pageSize, 10L)
                )
        );
    }

    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);
        return articles.stream().map(ArticleResponse::from).toList();
    }

    public Long count(Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
