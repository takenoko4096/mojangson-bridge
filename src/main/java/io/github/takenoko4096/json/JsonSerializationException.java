package io.github.takenoko4096.json;

/**
 * JsonSerializerによって投げられるシリアライゼーション例外。
 * @see JsonSerializer
 */
public class JsonSerializationException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     */
    protected JsonSerializationException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     * @param cause 原因
     */
    protected JsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
