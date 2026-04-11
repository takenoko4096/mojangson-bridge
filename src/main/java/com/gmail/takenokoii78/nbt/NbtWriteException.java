package com.gmail.takenokoii78.nbt;

public class NbtWriteException extends RuntimeException {
    NbtWriteException(String message, Throwable cause) {
        super(message, cause);
    }

    NbtWriteException(String message) {
        super(message);
    }

    NbtWriteException(Throwable cause) {
        super(cause);
    }
}
