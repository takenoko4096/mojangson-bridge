package io.github.takenoko4096.json;

/**
 * JsonPathによって投げられるアクセス例外。
 * @see JsonPath
 */
public class JsonPathUnableToAccessException extends Exception {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     */
    public JsonPathUnableToAccessException(String message) {
        super(message);
    }
}
