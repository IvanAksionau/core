package core.reportportal

import com.epam.reportportal.utils.properties.ListenerProperty
import core.config.env.EnvironmentFactory
import core.config.props.PropertyFiles
import core.utils.resources.properties.PropsUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import java.util.regex.Matcher
import java.util.regex.Pattern

import static core.RuntimePropsKeys.ARGS_SUITE
import static core.RuntimePropsKeys.ARGS_URL
import static core.RuntimePropsKeys.PROPS_PREFIX
import static core.RuntimePropsKeys.ARGS_USER
import static core.RuntimePropsKeys.ARGS_ENVIRONMENT

@Slf4j
class ReportPortalPropertiesResolver {

    private static final String TAGS_SEPARATOR = ';'
    private static final String VERSION_PATTERN_PROPERTY =
            'com.tr.eds.core.reportportal.ReportPortalPropertiesResolver/versionPattern'
    private static final String ADDITIONAL_TAGS_PROPERTY =
            'com.tr.eds.core.reportportal.ReportPortalPropertiesResolver/additionalTagsProperty'

    private static final List<String> REPORT_PORTAL_PROPERTY_KEYS = ListenerProperty.values()*.propertyName

    private static final Pattern PROPERTY_PLACEHOLDER_PATTERN = ~/.*\$\{(.+)}.*/

    private final Properties props = new Properties()

    void resolve() {
        collectProperties()

        resolveLaunch()
        resolveTags()
        resolveOthers()
    }

    private void collectProperties() {
        mergeProperties(props, System.properties)
        mergeProperties(props, applicationProperties)
        defaultProperties.each { mergeProperties(props, it) }
    }

    private void resolveLaunch() {
        String suite = props.getProperty(ARGS_SUITE)
        log.info("Launch name: ${suite}")
        System.setProperty('rp.launch', suite)
    }

    private void resolveTags() {
        String environment = resolveEnvironment()
        StringBuilder rpTags = new StringBuilder(environment)

        String user = resolveUser()
        rpTags.append(TAGS_SEPARATOR).append(user)

        resolveVersion()
                .ifPresent { rpTags.append(TAGS_SEPARATOR).append(it) }

        resolvePlaceWhereTestsExecuted()
                .ifPresent { rpTags.append(TAGS_SEPARATOR).append(it) }

        resolveAdditionalTags(rpTags)

        System.setProperty('rp.tags', rpTags.toString())
    }

    private void resolveOthers() {
        Properties systemProperties = System.properties
        props.findAll { k, v -> REPORT_PORTAL_PROPERTY_KEYS.contains(k) }
                .each { k, v -> systemProperties.putIfAbsent(k, v) }
    }

    private Optional<String> resolveVersion() {
        String url = props.getProperty(ARGS_URL)
        if (url == null) {
            url = props
                    .getProperty("${PROPS_PREFIX}url",
                            StringUtils.EMPTY)
        }
        log.info("Url: ${url}")
        getVersionRpTag(props.getProperty(VERSION_PATTERN_PROPERTY, StringUtils.EMPTY), url)
    }

    private static Optional<String> getVersionRpTag(String versionPattern, String url) {
        if (url.isEmpty() || versionPattern.isEmpty()) {
            return Optional.empty()
        }
        Pattern p = Pattern.compile(versionPattern)
        Matcher m = p.matcher(url)
        m.find() ? Optional.of(m.group(1)) : Optional.empty() as Optional<String>
    }

    private String resolveUser() {
        String user = props.getProperty(ARGS_USER) ?: props
                .getProperty("${PROPS_PREFIX}user", StringUtils.EMPTY)
        log.info("User: ${user}")
        user
    }

    private String resolveEnvironment() {
        String environment = props.getProperty(ARGS_ENVIRONMENT)
        if (environment == null) {
            environment = props.getProperty(EnvironmentFactory.DEFAULT_ENV_PROPERTY_KEY, StringUtils.EMPTY)
        }
        log.info("Environment: ${environment}")
        environment
    }

    private Optional<String> resolvePlaceWhereTestsExecuted() {
        String rpDescription = props.getProperty('rp.description')
        if (rpDescription == null) {
            return Optional.empty()
        }
        log.info("Launch description: ${rpDescription}")
        String aws = 'AWS'
        rpDescription.contains(aws) ? Optional.of(aws) : Optional.empty() as Optional<String>
    }

    private void resolveAdditionalTags(StringBuilder rpTags) {
        List<String> additionalTags = props.getProperty(ADDITIONAL_TAGS_PROPERTY)?.split(',')
        if (additionalTags) {
            additionalTags.collect { resolvePropertyPlaceholders(it) }
                    .each { rpTags.append(TAGS_SEPARATOR).append(it) }
        }
    }

    private String resolvePropertyPlaceholders(String propertyValue) {
        Matcher matcher = PROPERTY_PLACEHOLDER_PATTERN.matcher(propertyValue)
        if (matcher.matches()) {
            String resolvedPropertyValue = props.getProperty(matcher.group(1))
            if (resolvedPropertyValue == null) {
                throw new IllegalArgumentException("Failed to resolve placeholder ${propertyValue}")
            }
            return resolvePropertyPlaceholders(resolvedPropertyValue)
        }
        propertyValue
    }

    private static void mergeProperties(Properties originalProperties, Properties newProperties) {
        newProperties.each { k, v -> originalProperties.putIfAbsent(k, v) }
    }

    private static Properties convertResourceToProperties(Resource resource) {
        Properties properties = new Properties()
        properties.load(resource.inputStream)
        properties
    }

    private static Properties getApplicationProperties() {
        PropsUtil.readProperties("/${PropertyFiles.APPLICATION_PROPERTIES}")
    }

    private static List<Properties> getDefaultProperties() {
        new PathMatchingResourcePatternResolver().getResources("classpath*:${PropertyFiles.DEFAULT_PROPERTIES}")
                .collect { convertResourceToProperties(it as Resource) }
    }
}
