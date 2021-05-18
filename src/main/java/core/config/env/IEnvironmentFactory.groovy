package core.config.env

@SuppressWarnings('FactoryMethodName')
interface IEnvironmentFactory {

    public <T extends BaseEnvironment> T create(Class<T> clazz) throws IOException
}
