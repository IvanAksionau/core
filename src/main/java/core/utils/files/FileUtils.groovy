package core.utils.files

import java.nio.file.Files

final class FileUtils {

    private FileUtils() {
    }

    static void writeFileContent(String content, File file) {
        file.withWriter {
            writer ->
                writer.write(content)
        }
    }

    /**
     * Creates a new directory in the default temporary-file directory, using the given prefix to generate its name.
     * And marks it for deletion after JVM shutdown.
     * @param prefix the prefix string to be used in generating the directory's name
     * @return the File object of newly created directory
     * @throws FileUtilsException in case of any IO or security exceptions
     */
    static File makeTempDirectory(String prefix) {
        try {
            Files.createTempDirectory(prefix).toFile().tap { deleteOnExit(it) }
        } catch (IllegalArgumentException | UnsupportedOperationException | IOException | SecurityException e) {
            throw new FileUtilsException("Failed to create temporary directory with prefix [${prefix}].", e)
        }
    }

    /**
     * Creates an empty file in the default temporary-file directory, using the given prefix and suffix to generate its
     * name. And marks it for deletion after JVM shutdown.
     * @param prefix the prefix string to be used in generating the file's name
     * @param suffix the suffix string to be used in generating the file's name
     * @return newly created
     * @throws FileUtilsException in case of any IO or security exceptions
     */
    static File makeTempFile(String prefix, String suffix) {
        try {
            Files.createTempFile(prefix, suffix).toFile().tap { deleteOnExit() }
        } catch (IllegalArgumentException | UnsupportedOperationException | IOException | SecurityException e) {
            throw new FileUtilsException(
                    "Failed to create temporary file with prefix [${prefix}] and suffix [${suffix}].", e)
        }
    }

    private static void deleteOnExit(File directory) {
        Runtime.runtime.addShutdownHook(new Thread(new Runnable() {

            @Override
            void run() {
                FileUtils.deleteDirectory(directory)
            }
        }))
    }
}
