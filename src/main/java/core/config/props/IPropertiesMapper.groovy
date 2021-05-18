package core.config.props

interface IPropertiesMapper {

    public <T> T getEnvironment(Class<T> valueType)
}
