package io.github.takenoko4096.nbt;

/**
 * NbtDecoderが投げるバイナリ解析例外。
 * @see NbtDecoder
 */
public class NbtReadException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     * @param cause 原因
     */
    protected NbtReadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ
     */
    protected NbtReadException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param cause 原因
     */
    protected NbtReadException(Throwable cause) {
        super(cause);
    }
}
