package io.github.takenoko4096.mojangson;

import org.jspecify.annotations.NullMarked;

/**
 * MojangsonSerializerによって投げられるシリアライゼーション例外。
 * @see MojangsonSerializer
 */
@NullMarked
public class MojangsonSerializationException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     */
    protected MojangsonSerializationException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     * @param cause 原因となる例外。
     */
    protected MojangsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
