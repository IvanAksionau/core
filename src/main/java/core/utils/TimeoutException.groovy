package core.utils

class TimeoutException extends RuntimeException {

    TimeoutException(String message, Throwable cause) {
        super(message, cause)
    }
}
