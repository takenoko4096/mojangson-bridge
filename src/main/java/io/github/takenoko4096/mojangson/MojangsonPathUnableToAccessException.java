package io.github.takenoko4096.mojangson;

/**
 * MojangsonPathによって投げられるアクセス例外。
 * @see MojangsonPath
 */
public class MojangsonPathUnableToAccessException extends Exception {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     */
    protected MojangsonPathUnableToAccessException(String message) {
        super(message);
    }
}
