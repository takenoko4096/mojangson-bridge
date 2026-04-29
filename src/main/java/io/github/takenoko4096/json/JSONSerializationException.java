package io.github.takenoko4096.json;

import org.jspecify.annotations.NullMarked;

/**
 * JSONSerializerによって投げられるシリアライゼーション例外。
 * @see JSONSerializer
 */
@NullMarked
public class JSONSerializationException extends RuntimeException {
    protected JSONSerializationException(String message) {
        super(message);
    }

    protected JSONSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
