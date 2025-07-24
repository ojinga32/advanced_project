package kuke.board.hotarticle.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculatorUtils {

    // 현재 시간에서 자정까지 남은 시간 구해줌
    public static Duration calculateDurationToMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        return Duration.between(now, midnight);
    }
}
