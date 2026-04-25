package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class MojangsonByte extends MojangsonNumber<Byte> {
    private MojangsonByte(byte value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.BYTE;
    }

    public boolean isBooleanValue() {
        return value == 0 || value == 1;
    }

    public boolean getAsBooleanValue() throws MojangsonNonBooleanValueException {
        if (isBooleanValue()) return value != 0;
        else throw new MojangsonNonBooleanValueException(value);
    }

    public @Nullable Boolean getAsBooleanValueOrNull() {
        if (isBooleanValue()) return value != 0;
        else return null;
    }

    public static MojangsonByte valueOf(byte value) {
        return new MojangsonByte(value);
    }

    public static final class MojangsonNonBooleanValueException extends RuntimeException {
        private MojangsonNonBooleanValueException(byte value) {
            super("MojangsonByteから真偽値への変換に失敗しました: ラップされた値は 0 または 1 でなければなりませんが、実際の値は " + value + " です");
        }
    }
}
