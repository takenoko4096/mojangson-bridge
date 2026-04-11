package com.gmail.takenokoii78.nbt;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class NbtReadException extends RuntimeException {
    NbtReadException(String message, Throwable cause) {
        super(message, cause);
    }

    NbtReadException(String message) {
        super(message);
    }

    NbtReadException(Throwable cause) {
        super(cause);
    }
}
