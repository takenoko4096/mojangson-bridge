package io.github.takenoko4096.nbt;

/**
 * NbtEncoderが投げるバイナリ変換例外。
 * @see NbtEncoder
 */
public class NbtWriteException extends RuntimeException {
    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     * @param cause 原因となる例外。
     */
    protected NbtWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 例外を作成します。
     * @param message エラーメッセージ。
     */
    protected NbtWriteException(String message) {
        super(message);
    }

    /**
     * 例外を作成します。
     * @param cause 原因となる例外。
     */
    protected NbtWriteException(Throwable cause) {
        super(cause);
    }
}
