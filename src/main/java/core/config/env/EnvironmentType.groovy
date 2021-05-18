package core.config.env

enum EnvironmentType {
    LOCAL(''),
    DEV('Development.json'),
    QA('QA.json'),
    PREPROD('PreProduction.json'),
    PROD('Production.json'),
    PERFORMANCE('')

    final String value

    EnvironmentType(String value) {
        this.value = value
    }
}
