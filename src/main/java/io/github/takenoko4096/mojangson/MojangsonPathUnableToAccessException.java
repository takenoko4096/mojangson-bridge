package io.github.takenoko4096.mojangson;

import org.jspecify.annotations.NullMarked;

/**
 * MojangsonPathによって投げられるアクセス例外。
 * @see MojangsonPath
 */
@NullMarked
public class MojangsonPathUnableToAccessException extends Exception {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     */
    protected MojangsonPathUnableToAccessException(String message) {
        super(message);
    }
}
