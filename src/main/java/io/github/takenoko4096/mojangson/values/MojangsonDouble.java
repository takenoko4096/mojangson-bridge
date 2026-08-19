package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

/**
 * mojangsonにおける double を表現します。
 */
@NullMarked
public final class MojangsonDouble extends MojangsonNumber<Double> {
    private MojangsonDouble(double value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonDouble> getType() {
        return MojangsonValueTypes.DOUBLE;
    }

    /**
     * double を MojangsonDouble に変換します。
     * @param value double
     * @return MojangsonDouble
     */
    public static MojangsonDouble valueOf(double value) {
        return new MojangsonDouble(value);
    }
}
