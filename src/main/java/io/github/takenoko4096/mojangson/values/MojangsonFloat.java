package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MojangsonFloat extends MojangsonNumber<Float> {
    private MojangsonFloat(float value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.FLOAT;
    }

    public static MojangsonFloat valueOf(float value) {
        return new MojangsonFloat(value);
    }
}
