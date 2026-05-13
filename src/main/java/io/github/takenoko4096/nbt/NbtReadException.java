package io.github.takenoko4096.nbt;

import org.jspecify.annotations.NullMarked;

/**
 * NbtDecoderが投げるバイナリ解析例外。
 * @see NbtDecoder
 */
@NullMarked
public class NbtReadException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     * @param cause 原因となる例外。
     */
    protected NbtReadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     */
    protected NbtReadException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param cause 原因となる例外。
     */
    protected NbtReadException(Throwable cause) {
        super(cause);
    }
}
