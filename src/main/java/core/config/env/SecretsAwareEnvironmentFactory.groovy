package core.config.env

import core.config.ConfigurationException
import groovy.json.JsonSlurper
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.DecryptionFailureException
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse
import software.amazon.awssdk.services.secretsmanager.model.InternalServiceErrorException
import software.amazon.awssdk.services.secretsmanager.model.InvalidParameterException
import software.amazon.awssdk.services.secretsmanager.model.InvalidRequestException
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException

class SecretsAwareEnvironmentFactory implements IEnvironmentFactory {

    private final JsonSlurper jsonSlurper = new JsonSlurper()

    protected final IEnvironmentFactory prev
    private final SecretsManagerClient awsSecretsManager

    SecretsAwareEnvironmentFactory(IEnvironmentFactory prev, SecretsManagerClient awsSecretsManager) {
        this.prev = prev
        this.awsSecretsManager = awsSecretsManager
    }

    @Override
    <T extends BaseEnvironment> T create(Class<T> clazz) throws IOException {
        T environment = prev.create(clazz)
        String secretId = environment.secret
        if (environment.password.empty && !secretId.empty) {
            String user = environment.user
            checkUserValue(user)
            environment.password = getPasswordForUser(secretId, user)
        }
        environment
    }

    String getPasswordForUser(String secretId, String user) {
        GetSecretValueResponse result
        try {
            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretId)
                    .build()
            result = awsSecretsManager.getSecretValue(getSecretValueRequest)
        }
        catch (ResourceNotFoundException | InvalidParameterException | InvalidRequestException
        | DecryptionFailureException | InternalServiceErrorException | SdkClientException e) {
            throw new ConfigurationException("Failed to retrieve secret [${secretId}] for user [${user}].", e)
        }

        String password = jsonSlurper.parseText(result.secretString())[user]
        if (password == null) {
            throw new ConfigurationException(
                    "Failed to retrieve password for user [${user}]. Check key-value pair is present.")
        }
        password
    }

    protected static void checkUserValue(String user) {
        if (user.empty) {
            throw new ConfigurationException(
                    'User should be specified in order to retrieve password from secret.')
        }
    }
}
