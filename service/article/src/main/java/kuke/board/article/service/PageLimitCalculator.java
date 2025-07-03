package kuke.board.article.service;

import kuke.board.article.entity.Article;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageLimitCalculator {

    // n -> 현재 페이지
    // m -> 이동 가능한 페지이 개수
    // k -> 이동 간으한 페이지 개수
    
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return (((page - 1) / movablePageCount) + 1) * pageSize * movablePageCount + 1;
    }

}
