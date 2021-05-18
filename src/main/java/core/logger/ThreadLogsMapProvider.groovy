package core.logger

import core.config.ConfigurationException
import groovy.util.logging.Slf4j

import java.util.concurrent.ConcurrentHashMap

@Slf4j
class ThreadLogsMapProvider {
    final static Map<Long, StringBuilder> LOGS_MAP = new ConcurrentHashMap<Long, StringBuilder>()
    private static File file

    private ThreadLogsMapProvider() {
    }

    static void writeLogsToFile() {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file))
        LOGS_MAP.isEmpty() ? log.info('LOGS_MAP is empty') : LOGS_MAP.each {
            testLog ->
                writer.write(System.lineSeparator())
                writer.write(testLog.value.toString())
        }
        writer.close()
    }

    static void initializeLogFile() {
        log.info("Initialization of a log file with name 'log4j2.log'")
        File newDirectory = new File('./test-output/threadslog')
        //Create new directory if not exists
        newDirectory.exists() ?: newDirectory.mkdirs()

        if (newDirectory.exists()) {
            try {
                file = new File(newDirectory.path + File.separator + 'log4j2.log')
                //Create new file under specified directory if not exists
                file.exists() ?: file.createNewFile()
            } catch (IOException e) {
                log.error("ThreadLogsMapProvider returns error :\n ${e.message}")
                throw new ConfigurationException(e)
            }
        }
    }
}
