package io.github.takenoko4096.json;

import org.jspecify.annotations.NullMarked;

/**
 * JSONSerializerによって投げられるシリアライゼーション例外。
 * @see JSONSerializer
 */
@NullMarked
public class JSONSerializationException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     */
    protected JSONSerializationException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     * @param cause 原因となる例外。
     */
    protected JSONSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
