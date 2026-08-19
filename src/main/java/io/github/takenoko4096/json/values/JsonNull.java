package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * json構造におけるnull型を表現します。
 */
public final class JsonNull extends JsonPrimitive<Object> {
    private JsonNull() {
        super(null);
    }

    @Override
    public @NonNull String toString() {
        return "null";
    }

    @Override
    public @NonNull JsonValueType<?> getType() {
        return JsonValueTypes.NULL;
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
    public static final JsonNull NULL = new JsonNull();
}
