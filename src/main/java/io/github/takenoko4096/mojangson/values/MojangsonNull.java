package io.github.takenoko4096.mojangson.values;

import io.github.takenoko4096.mojangson.MojangsonValueType;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import org.jspecify.annotations.Nullable;

/**
 * mojangsonにおけるnullを表現します。
 */
public final class MojangsonNull extends MojangsonPrimitive<Void> {
    private MojangsonNull() {
        super(null);
    }

    @Override
    public MojangsonValueType<?> getType() {
        return MojangsonValueTypes.NULL;
    }

    /**
     * 常に null を返します。
     * @return null
     */
    @Override
    public @Nullable Void getValue() {
        return super.getValue();
    }

    @Override
    public String toString() {
        return "null";
    }

    /**
     * シングルトンオブジェクト。
     */
    public static final MojangsonNull NULL = new MojangsonNull();
}
