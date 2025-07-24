package kuke.board.hotarticle.service.eventHandler;

import kuke.board.common.event.Event;
import kuke.board.common.event.EventType;
import kuke.board.common.event.payload.CommentCreatedEventPayload;
import kuke.board.hotarticle.repository.ArticleCommentCountRepository;
import kuke.board.hotarticle.repository.ArticleLikeCountRepository;
import kuke.board.hotarticle.utils.TimeCalculatorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload>{
    private final ArticleCommentCountRepository articleCommentCountRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;

    @Override
    public void handle(Event<CommentCreatedEventPayload> event) {
        CommentCreatedEventPayload payload = event.getPayload();
        articleLikeCountRepository.createOrUpdate(
                payload.getArticleId(),
                payload.getArticleCommentCount(),
                TimeCalculatorUtils.calculateDurationToMidnight()
        );
    }

    @Override
    public boolean supports(Event<CommentCreatedEventPayload> event) {
        return EventType.COMMENT_CREATED == event.getType();
    }

    @Override
    public Long findArticleId(Event<CommentCreatedEventPayload> event) {
        return event.getPayload().getArticleId();
    }
}
