package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

/**
 * mojangsonにおける int を表現します
 */
public final class MojangsonInt extends MojangsonNumber<Integer> {
    private MojangsonInt(int value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonInt> getType() {
        return MojangsonValueTypes.INT;
    }

    /**
     * int を MojangsonInt に変換します。
     * @param value int
     * @return MojangsonInt
     */
    public static MojangsonInt valueOf(int value) {
        return new MojangsonInt(value);
    }
}
