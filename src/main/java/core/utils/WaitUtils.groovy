package core.utils

import org.apache.commons.lang3.time.DurationFormatUtils

import java.time.Duration

final class WaitUtils {

    private WaitUtils() {
    }

    @SuppressWarnings('CatchThrowable')
    static <T> T waitUntil(Closure<T> isTrue, Duration timeout, Duration pollingPeriod) {
        long end = System.currentTimeMillis() + timeout.toMillis()
        Throwable lastException
        while (true) {
            try {
                T value = isTrue()
                if (value != null && (Boolean != value.class || Boolean.TRUE == value)) {
                    return value
                }
                lastException = null
            }
            catch (Throwable e) {
                lastException = e
            }

            if (System.currentTimeMillis() > end) {
                throw new TimeoutException("Expected condition failed. Tried for ${formatTime(timeout)} with " +
                        "${formatTime(pollingPeriod)} interval", lastException)
            }
            sleep(pollingPeriod.toMillis())
        }
    }

    private static String formatTime(Duration timeEntity) {
        DurationFormatUtils.formatDurationWords(timeEntity.toMillis(),
                true, false)
    }
}
