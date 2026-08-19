package io.github.takenoko4096.mojangson;

/**
 * MojangsonSerializerによって投げられるシリアライゼーション例外。
 * @see MojangsonSerializer
 */
public class MojangsonSerializationException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     */
    protected MojangsonSerializationException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     * @param cause 原因
     */
    protected MojangsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
