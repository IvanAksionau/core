package core.config.env

import core.config.props.IPropertiesMapper
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Lazy
@Component
class EnvironmentFactory implements IEnvironmentFactory {

    public static final String DEFAULT_ENV_PROPERTY_KEY = 'com.tr.eds.core.config.env.EnvironmentFactory/defaultEnv'
    public static final String SECRET_KEY = 'com.tr.eds.qa.args.secret'
    public static final String USER_PASSWORD = 'com.tr.eds.qa.args.password'
    public static final String ADMIN_PASSWORD = 'com.tr.eds.qa.args.adminPassword'

    private String overridingUrl
    private String overridingEnvType
    private String overridingUser

    private final String defaultEnvType
    private final IPropertiesMapper propertiesMapper

    EnvironmentFactory(String defaultEnvType, IPropertiesMapper propertiesMapper) {
        this.defaultEnvType = defaultEnvType
        this.propertiesMapper = propertiesMapper
    }

    @Override
    <T extends BaseEnvironment> T create(Class<T> clazz) throws IOException {
        String envType =
                (overridingEnvType == null || overridingEnvType.empty) ? defaultEnvType : overridingEnvType

        T environment = propertiesMapper.getEnvironment(clazz)

        if (overridingUrl != null && !overridingUrl.empty) {
            environment.setUrl(overridingUrl)
        }
        if (overridingUser) {
            environment.user = overridingUser
        }
        environment.setEnvironmentType(EnvironmentType.valueOf(envType))
        environment
    }

    void setOverridingUrl(String overridingUrl) {
        this.overridingUrl = overridingUrl
    }

    void setOverridingEnvType(String overridingEnvType) {
        this.overridingEnvType = overridingEnvType
    }

    void setOverridingUser(String overridingUser) {
        this.overridingUser = overridingUser
    }
}
