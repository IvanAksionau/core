package core.utils

import java.nio.charset.StandardCharsets

final class Base64Utils {

    private Base64Utils() {
    }

    static String decode(String str) {
        new String(str.decodeBase64(), StandardCharsets.UTF_8)
    }

    static String encode(byte[] bytes) {
        bytes.encodeBase64().toString()
    }

    static String encode(String str) {
        encode(str.bytes)
    }

    static String encode(File file) {
        encode(file.bytes)
    }
}
