package core.utils.resources

import org.apache.logging.log4j.core.util.IOUtils

import java.nio.charset.StandardCharsets

final class ResourceUtils {

    private ResourceUtils() {
        //Utility class
    }

    /**
     * Loads resource as UTF-8 string
     * @param clazz Class to search resource relatively
     * @param resourceName Resource name
     * @return Resource as string
     */
    static String loadResource(Class<?> clazz, String resourceName) {
        URL resource = findResource(clazz, resourceName)
        try {
            return IOUtils.toString(resource, StandardCharsets.UTF_8)
        }
        catch (IOException e) {
            throw new IllegalStateException(e)
        }
    }

    private static URL findResource(Class<?> clazz, String resourceName) {
        URL resource = clazz.getResource(resourceName)
        if (resource != null) {
            return resource
        }
        throw new IllegalArgumentException("Resource with name ${resourceName} for ${clazz} is not found")
    }
}
