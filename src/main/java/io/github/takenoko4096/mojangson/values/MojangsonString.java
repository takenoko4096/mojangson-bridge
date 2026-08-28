package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;

/**
 * mojangsonにおける String を表現します。
 */
public class MojangsonString extends MojangsonPrimitive<String> {
    private MojangsonString(String value) {
        super(value);
    }

    @Override
    public MojangsonValueType<MojangsonString> getType() {
        return MojangsonValueTypes.STRING;
    }

    /**
     * String を MojangsonString に変換します。
     * @param value String
     * @return MojangsonString
     */
    public static MojangsonString valueOf(String value) {
        return new MojangsonString(value);
    }

    /**
     * char を MojangsonString に変換します。
     * @param value char
     * @return MojangsonString
     */
    public static MojangsonString valueOf(char value) {
        return valueOf(String.valueOf(value));
    }
}
