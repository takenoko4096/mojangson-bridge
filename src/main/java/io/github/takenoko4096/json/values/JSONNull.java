package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JSONValueType;
import io.github.takenoko4096.json.JSONValueTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * json構造におけるnull型を表現します。
 */
public final class JSONNull extends JSONPrimitive<Object> {
    private JSONNull() {
        super(null);
    }

    @Override
    public @NonNull String toString() {
        return "null";
    }

    @Override
    public @NonNull JSONValueType<?> getType() {
        return JSONValueTypes.NULL;
    }

    /**
     * 常にnullを返します。
     * @return null
     */
    @Override
    public @Nullable Object getValue() {
        return super.getValue();
    }

    /**
     * nullを表現するシングルトンオブジェクト。
     */
    public static final JSONNull NULL = new JSONNull();
}
