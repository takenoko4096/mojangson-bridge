package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class MojangsonString extends MojangsonPrimitive<String> {
    private MojangsonString(String value) {
        super(value);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.STRING;
    }

    public static MojangsonString valueOf(String value) {
        return new MojangsonString(value);
    }

    public static MojangsonString valueOf(char value) {
        return valueOf(String.valueOf(value));
    }
}
