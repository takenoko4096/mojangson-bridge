package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
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
