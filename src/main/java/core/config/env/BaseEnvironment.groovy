package core.config.env

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class BaseEnvironment {

    String url
    String user = ''
    String password = ''
    String secret = ''
    EnvironmentType environmentType
}
