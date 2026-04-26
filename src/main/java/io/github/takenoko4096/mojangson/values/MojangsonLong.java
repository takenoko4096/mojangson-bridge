package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class MojangsonLong extends MojangsonNumber<Long> {
    private MojangsonLong(long value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.LONG;
    }

    public static MojangsonLong valueOf(long value) {
        return new MojangsonLong(value);
    }
}
