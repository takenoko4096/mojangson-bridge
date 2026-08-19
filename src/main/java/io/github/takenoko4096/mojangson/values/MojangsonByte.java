package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

/**
 * mojangsonにおける byte を表現します。
 */
public final class MojangsonByte extends MojangsonNumber<Byte> {
    private MojangsonByte(byte value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonByte> getType() {
        return MojangsonValueTypes.BYTE;
    }

    /**
     * boolean として取得します。
     * @return boolean
     */
    public boolean booleanValue() {
        return value != 0;
    }

    /**
     * byte を MojangsonByte に変換します。
     * @param value byte
     * @return MojangsonByte
     */
    public static MojangsonByte valueOf(byte value) {
        return new MojangsonByte(value);
    }
}
