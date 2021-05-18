package core.utils.resources.properties

final class PropsUtil {

    private PropsUtil() {
        //Utility class
    }

    static Properties readProperties(String propertiesLocation) {
        Properties properties = new Properties()
        InputStream propsAsStream = PropsUtil.getResourceAsStream(propertiesLocation)
        if (propsAsStream != null) {
            properties.load(propsAsStream)
        }
        properties
    }
}
