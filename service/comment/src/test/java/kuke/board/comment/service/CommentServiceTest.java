package kuke.board.comment.service;

import kuke.board.comment.entity.Comment;
import kuke.board.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    CommentService commentService;
    @Mock
    CommentRepository commentRepository;

    @Test
    @DisplayName("삭제할 댓글이 자식 있으면, 삭제 표시만 한다.")
    void deleteShouldMarkDeletedIfHashChildren() {
        // given
        Long articleId = 1L;
        Long commentId = 2L;
        Comment comment = createComment(articleId, commentId);

        System.out.println("comment = " + comment.toString());

        System.out.println("commentRepository.findById(commentId) = " + commentRepository.findById(commentId));

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));

        System.out.println("commentId1 = " + commentId);

        given(commentRepository.countBy(articleId, commentId, 2L)).willReturn(2L);

        System.out.println("commentId2 = " + commentId);

        // when
        commentService.delete(commentId);

        // then
        verify(comment).delete();
    }

    private Comment createComment(Long articleId, Long commentId) {
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);
        return comment;
    }

    private Comment createComment(Long articleId, Long commentId, Long parentCommentId) {
        Comment comment = createComment(articleId, commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);
        return comment;
    }

}