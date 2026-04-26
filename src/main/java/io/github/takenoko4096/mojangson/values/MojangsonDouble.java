package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MojangsonDouble extends MojangsonNumber<Double> {
    private MojangsonDouble(double value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.DOUBLE;
    }

    public static MojangsonDouble valueOf(double value) {
        return new MojangsonDouble(value);
    }
}
