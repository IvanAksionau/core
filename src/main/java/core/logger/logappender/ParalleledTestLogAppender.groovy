package core.logger.logappender

import core.config.ConfigurationException
import groovy.util.logging.Slf4j

import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.LogEvent

import org.apache.logging.log4j.core.Core
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.config.plugins.PluginAttribute
import org.apache.logging.log4j.core.config.plugins.PluginElement
import org.apache.logging.log4j.core.config.plugins.PluginFactory

import java.time.LocalDateTime

@Slf4j
@Plugin(name = 'ParalleledTestLogAppender',
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE,
        printObject = false)
final class ParalleledTestLogAppender extends AbstractAppender {
    private final static String REPORT_PORTAL_KEY = 'com.tr.eds.qaframework.utils.ReportPortalUtils'
    private final static String LINE_SEPARATOR = System.lineSeparator()

    ParalleledTestLogAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                              boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions)
    }

    @Override
    void append(LogEvent event) {
        LogEvent immutableEvent = event.toImmutable()
        if (!immutableEvent.message || immutableEvent.loggerName == REPORT_PORTAL_KEY) {
            return
        }

        try {
            long threadId = event.threadId
            StringBuilder builder = ThreadLogsMapProvider.LOGS_MAP.get(threadId)
            //check if 'threadsLogMap' already contains instance of 'StringBuilder' with 'threadId'
            //if no, then create a new instance of 'StringBuilder' and put it into map 'threadsLogMap'
            if (!builder) {
                builder = new StringBuilder()
                ThreadLogsMapProvider.LOGS_MAP.put(threadId, builder)
            }

            //add message to builder
            builder.append("[${LocalDateTime.now()}] ")
            builder.append(immutableEvent.message.formattedMessage)
            builder.append(LINE_SEPARATOR)
        } catch (IOException e) {
            log.warn("ParalleledTestLogAppender returns error :${LINE_SEPARATOR} ${e.message}")
            throw new ConfigurationException(e)
        }
    }

    @PluginFactory
    static ParalleledTestLogAppender defineAppender(
            @PluginAttribute('name') String name,
            @PluginElement('layout') Layout<? extends Serializable> layout,
            @PluginElement('filter') Filter filter) {
        if (!name) {
            LOGGER.error('No name provided for ParalleledTestLogAppender')
            return null
        }

        new ParalleledTestLogAppender(name, filter, layout, true)
    }
}
