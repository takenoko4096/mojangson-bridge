package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

/**
 * mojangsonにおける float を表現します。
 */
public final class MojangsonFloat extends MojangsonNumber<Float> {
    private MojangsonFloat(float value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonFloat> getType() {
        return MojangsonValueTypes.FLOAT;
    }

    /**
     * float を MojangsonFloat に変換します。
     * @param value float
     * @return MojangsonFloat
     */
    public static MojangsonFloat valueOf(float value) {
        return new MojangsonFloat(value);
    }
}
