package com.gmail.takenokoii78.nbt;

import org.jspecify.annotations.NullMarked;

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
