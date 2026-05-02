package io.github.takenoko4096.nbt;

/**
 * NbtEncoderが投げるバイナリ変換例外。
 * @see NbtEncoder
 */
public class NbtWriteException extends RuntimeException {
    protected NbtWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    protected NbtWriteException(String message) {
        super(message);
    }

    protected NbtWriteException(Throwable cause) {
        super(cause);
    }
}
