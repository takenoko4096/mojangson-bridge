package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

/**
 * mojangsonにおけるlongを表現します。
 */
public final class MojangsonLong extends MojangsonNumber<Long> {
    private MojangsonLong(long value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonLong> getType() {
        return MojangsonValueTypes.LONG;
    }

    /**
     * long を MojangsonLong に変換します。
     * @param value long
     * @return MojangsonLong
     */
    public static MojangsonLong valueOf(long value) {
        return new MojangsonLong(value);
    }
}
