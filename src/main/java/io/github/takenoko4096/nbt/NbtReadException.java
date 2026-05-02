package io.github.takenoko4096.nbt;

import org.jspecify.annotations.NullMarked;

/**
 * NbtDecoderが投げるバイナリ解析例外。
 * @see NbtDecoder
 */
@NullMarked
public class NbtReadException extends RuntimeException {
    protected NbtReadException(String message, Throwable cause) {
        super(message, cause);
    }

    protected NbtReadException(String message) {
        super(message);
    }

    protected NbtReadException(Throwable cause) {
        super(cause);
    }
}
