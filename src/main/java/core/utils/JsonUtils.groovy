package core.utils

import groovy.json.JsonOutput

final class JsonUtils {

    private JsonUtils() {
    }

    static String toJson(Object obj) {
        JsonOutput.prettyPrint(JsonOutput.toJson(obj))
    }

}
