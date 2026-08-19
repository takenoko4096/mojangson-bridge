package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

/**
 * mojangsonにおけるshortを表現します。
 */
public final class MojangsonShort extends MojangsonNumber<Short> {
    private MojangsonShort(short value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonShort> getType() {
        return MojangsonValueTypes.SHORT;
    }

    /**
     * short を MojangsonShort に変換します。
     * @param value short
     * @return MojangsonShort
     */
    public static MojangsonShort valueOf(short value) {
        return new MojangsonShort(value);
    }
}
