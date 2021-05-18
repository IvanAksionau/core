package core;

import core.reportportal.ReportPortalPropertiesResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseTestRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTestRunner.class);

    @Option(names = {"--suite"}, required = true, description = "A test suite to run.")
    private String suite;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Option(names = {"--R"}, description = "Additional runtime options.")
    private final Map<String, String> options = new HashMap<>();

    public static void main(String[] args) {
        try {
            CommandLine.run(new BaseTestRunner(), args);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        System.exit(0); //required because of reportportal listener issue
    }

    @Override
    public void run() {
        System.setProperty(RuntimePropsKeys.ARGS_SUITE, suite);
        options.forEach((key, value) -> System
            .setProperty(StringUtils.prependIfMissing(key, RuntimePropsKeys.PROPS_PREFIX), value));
        new ReportPortalPropertiesResolver().resolve();

        testNgExecutor(suite);
    }

    private void testNgExecutor(String suiteName) {
        File testSuite = readTestSuite(suiteName);
        TestNG testNg = new TestNG();
        testNg.setTestSuites(Collections.singletonList(testSuite.getAbsolutePath()));
        testNg.run();
    }

    private File readTestSuite(String suiteName) {
        try {
            String xmlSuffix = ".xml";
            File testSuite = Files.createTempFile(UUID.randomUUID().toString(), xmlSuffix).toFile();
            testSuite.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(testSuite);
                InputStream in = this.getClass().getResourceAsStream("/testsuites/" + suiteName + xmlSuffix)) {
                if (in != null) {
                    IOUtils.copy(in, out);
                } else {
                    LOGGER.error("Test suite with name " + suiteName + " was not found");
                }
            }
            return testSuite;
        } catch (IOException e) {
            LOGGER.error("Unable to create temporary file for test suite", e);
            throw new RuntimeException();
        }
    }
}
