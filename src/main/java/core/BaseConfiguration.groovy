package core

import core.config.env.BaseEnvironment
import core.config.env.EnvironmentFactory
import core.config.env.IEnvironmentFactory
import core.config.env.SecretsAwareEnvironmentFactory
import core.config.props.IPropertiesMapper
import core.config.props.PropertySourcesConfigurer
import core.context.IContext
import core.context.ThreadedContext
import core.spring.StringToAwsRegionConverter
import core.spring.StringToDurationConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.convert.converter.Converter
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.format.support.FormattingConversionServiceFactoryBean
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient

import javax.annotation.PostConstruct
import java.util.logging.Logger

@Configuration
@ComponentScan
@Lazy
class BaseConfiguration {

    @Autowired
    private ConfigurableEnvironment environment

    @PostConstruct
    void configurePropertySources() {
        PropertySourcesConfigurer.configure(environment)
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        new PropertySourcesPlaceholderConfigurer()
    }

    @Bean
    @Lazy(false)
    FormattingConversionServiceFactoryBean conversionService() {
        FormattingConversionServiceFactoryBean bean = new FormattingConversionServiceFactoryBean()
        Set<Converter> converters = [] as Set
        converters.add(new StringToDurationConverter())
        converters.add(new StringToAwsRegionConverter())
        bean.setConverters(converters)
        bean
    }

    @Bean
    @Qualifier('defaultEnvFactory')
    IEnvironmentFactory environmentFactory(IPropertiesMapper propertiesMapper, SecretsManagerClient awsSecretsManager) {
        String defaultEnvType = environment.getProperty(EnvironmentFactory.DEFAULT_ENV_PROPERTY_KEY)
        IEnvironmentFactory prev = new EnvironmentFactory(defaultEnvType, propertiesMapper)
        prev.setOverridingUrl(environment.getProperty(RuntimePropsKeys.ARGS_URL))
        prev.setOverridingEnvType(environment.getProperty(RuntimePropsKeys.ARGS_ENVIRONMENT))
        prev.setOverridingUser(environment.getProperty(RuntimePropsKeys.ARGS_USER))
        new SecretsAwareEnvironmentFactory(prev, awsSecretsManager)
    }

    @Bean
    BaseEnvironment baseEnvironment(@Qualifier('defaultEnvFactory') IEnvironmentFactory environmentFactory)
            throws IOException {
        environmentFactory.create(BaseEnvironment)
    }

    @Bean
    IContext testContext() {
        new ThreadedContext()
    }

    @Bean
    SecretsManagerClient secretsManager(@Value('${com.ia.core.BaseConfiguration/awsSecretsRegion}') Region region) {
        SecretsManagerClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build()
    }

    @Bean
    Logger logger() {
        new Logger()
    }
}
