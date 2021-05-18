package core.utils

import groovy.util.logging.Slf4j
import org.slf4j.event.Level

@Slf4j
final class ReportPortalUtils {

    private static final String FORMAT = 'RP_MESSAGE#BASE64#{}#{}'

    private ReportPortalUtils() {
        //Utility class
    }

    /**
     * Attaches file to a report for the current test in ReportPortal.
     * @param attachment file's content as base64 string
     * @param message a message describing attachment
     * @param level indicates a log level of attachment message
     */
    static void attachFile(String attachment, String message, Level level) {
        switch (level) {
            case Level.ERROR:
                log.error(FORMAT, attachment, message)
                break
            case Level.WARN:
                log.warn(FORMAT, attachment, message)
                break
            case Level.INFO:
                log.info(FORMAT, attachment, message)
                break
            case Level.DEBUG:
                log.debug(FORMAT, attachment, message)
                break
            case Level.TRACE:
                log.trace(FORMAT, attachment, message)
                break
        }
    }
}
