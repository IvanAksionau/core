package core.config.props

import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MutablePropertySources
import org.springframework.core.env.StandardEnvironment
import org.springframework.core.io.Resource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.core.io.support.ResourcePropertySource

class PropertySourcesConfigurer {

    static void configure(ConfigurableEnvironment environment) {
        MutablePropertySources mutablePropertySources = environment.propertySources
        mutablePropertySources.remove(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME)

        ResourcePropertySource resourcePropertySource =
                new ResourcePropertySource("classpath:${PropertyFiles.APPLICATION_PROPERTIES}")
        mutablePropertySources.addLast(resourcePropertySource)

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver()
        Resource[] defaultPropertyResources =
                resourcePatternResolver.getResources("classpath*:**/${PropertyFiles.DEFAULT_PROPERTIES}")
        defaultPropertyResources.each { mutablePropertySources.addLast(new ResourcePropertySource(it)) }
    }
}
