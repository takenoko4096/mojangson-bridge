package com.gmail.takenokoii78.nbt;

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
