package io.github.takenoko4096.json;

/**
 * JSONPathによって投げられるアクセス例外。
 * @see JSONPath
 */
public class JSONPathUnableToAccessException extends Exception {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     */
    protected JSONPathUnableToAccessException(String message) {
        super(message);
    }
}
