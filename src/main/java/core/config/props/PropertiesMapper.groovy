package core.config.props

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import core.RuntimePropsKeys
import core.config.env.EnvironmentFactory
import core.config.env.EnvironmentType
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region

import javax.annotation.PostConstruct

@Component
class PropertiesMapper implements IPropertiesMapper {

    private static final JavaPropsMapper PROPS_MAPPER = new JavaPropsMapper()
    private static final JsonSlurper JSON_PARSER = new JsonSlurper()
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesMapper)

    private static final String IAC_URL_KEY = 'Endpoint'
    private static final String URL_PORT_VALUE = ':5001'

    private static String rootPath
    private static File projectPathFile

    private final ConfigurableEnvironment environment

    private EnvironmentType defaultEnv

    private Properties properties

    @Value('${com.ia.core.BaseConfiguration/awsSecretsRegion}')
    private Region region

    @Value('${com.ia.core.local.url:#{null}}')
    private String localUrl

    @Value('${com.ia.core.local.run:#{false}}')
    private boolean isLocalRun

    @Value('${com.ia.core.local.settings:#{false}}')
    private boolean isLocalSettings

    @Value('${com.ia.qa.args.scheme:#{null}}')
    private String scheme

    @Autowired
    PropertiesMapper(ConfigurableEnvironment environment) {
        this.environment = environment
    }

    @Override
    <T> T getEnvironment(Class<T> valueType) {
        String secretKey = environment.getProperty(EnvironmentFactory.SECRET_KEY)
        String password = environment.getProperty(EnvironmentFactory.USER_PASSWORD)
        String adminPassword = environment.getProperty(EnvironmentFactory.ADMIN_PASSWORD)

        Map<String, String> settings = environmentSettings
        settings.put('secret', secretKey)
        settings.put('password', password)
        adminPassword ? settings.put('adminPassword', adminPassword) : void

        setSystemProperties(settings)

        Properties objectProps = settings.entrySet()
                .collectEntries(new Properties()) {
                    [(it.key.contains(IAC_URL_KEY) ? 'url'
                            : it.key.contains('InternalUser') ? 'internalUser' : it.key.toLowerCase()) : it.value]
                } as Properties

        PROPS_MAPPER.readPropertiesAs(objectProps, valueType)
    }

    private Map<String, String> getEnvironmentSettings() {
        Map object
        File settingsFile
        //check if config for account 3.0 exists
        settingsFile = new File(
                "${rootPath}/iac3.0/tests-configs/${region.id()}/${defaultEnv.value}")

        //for Partner Schemas tests
        if (!isLocalRun && !settingsFile.exists()) {
            LOGGER.info("Checking of 'iac3.0' settings for Partner Schemas tests for '${scheme}' schema")
            settingsFile = new File(
                    "${rootPath}/iac3.0/tests-configs/${scheme}/${region.id()}/${defaultEnv.value}")
        }
        if (isLocalSettings || !settingsFile.exists()) {
            LOGGER.info("Checking of 'iac' settings from 'qa-autotests' directory")
            settingsFile = new File(
                    "${projectPathFile}/iac/ApplicationValidation/${region.id()}/${defaultEnv.value}")
        }

        object = JSON_PARSER.parse(settingsFile) as Map

        //get environment data from file
        LOGGER.info("Apply settings from path: ${settingsFile.path}")
        Map taskDefinitions = object.get('ecs_task_definition') as Map
        List<Map> containerDefinitions = taskDefinitions.get('containerDefinitions') as List<Map>
        List<Map> environmentDefinitions = containerDefinitions.first().get('environment') as List<Map>

        //collect environment data to map
        Map<String, String> settings = [:]
        environmentDefinitions.each { map ->
            String key = map.get('name')
            String value = map.get('value')
            settings.put(key, value)
        }

        //if 'url' value exists in environment data from file -> put it in settings map
        String url = settings.get(IAC_URL_KEY)
        if (localUrl) {
            settings.put(IAC_URL_KEY, localUrl)
            settings
        } else if (url) {
            //test port in Loadbalancer (5001 in our current config) -> we delete its value from URL for local run only
            isLocalRun && url.contains(URL_PORT_VALUE) ? resolveUrlForNewAccount(settings) : settings
        }
        settings
    }

    private Map<String, String> resolveUrlForNewAccount(Map<String, String> settings) {
        String url = settings.get(IAC_URL_KEY).replace(URL_PORT_VALUE, '')
        settings.put(IAC_URL_KEY, url)
        settings
    }

    private void setSystemProperties(Map<String, String> settings) {
        System.setProperty("${RuntimePropsKeys.PROPS_PREFIX}authUrl", settings.get('AuthUrl') ?: '')
        System.setProperty("${RuntimePropsKeys.PROPS_PREFIX}gwUrl", settings.get('GwUrl') ?: '')
        System.setProperty("${RuntimePropsKeys.PROPS_PREFIX}gwUrlStg", settings.get('GwUrlStg') ?: '')
    }

    @SuppressWarnings('Instanceof')
    @PostConstruct
    void init() {
        Properties properties = new Properties()
        environment.propertySources.findAll { it instanceof EnumerablePropertySource }
                .stream()
                .map { (it as EnumerablePropertySource).propertyNames }
                .flatMap { Arrays.stream(it) }
                .forEach { properties.setProperty(it, environment.getProperty(it)) }
        this.properties = properties

        projectPathFile = new File('./').canonicalFile
        LOGGER.info("'projectPathFile' was defined as: ${projectPathFile}")

        String qaPath = projectPathFile.parent
        rootPath = new File(qaPath).canonicalFile.parent
        LOGGER.info("'rootPath' was defined as: ${rootPath}")

        String env = environment.getProperty(RuntimePropsKeys.ARGS_ENVIRONMENT)
        defaultEnv = (env ?: environment.getProperty(EnvironmentFactory.DEFAULT_ENV_PROPERTY_KEY)) as EnvironmentType
    }
}
