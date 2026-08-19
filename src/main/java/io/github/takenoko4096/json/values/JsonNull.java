package io.github.takenoko4096.json.values;

import io.github.takenoko4096.json.JsonValueType;
import io.github.takenoko4096.json.JsonValueTypes;
import org.jspecify.annotations.Nullable;

/**
 * json構造における null 型を表現します。
 */
public final class JsonNull extends JsonPrimitive<Void> {
    private JsonNull() {
        super(null);
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public JsonValueType<JsonNull> getType() {
        return JsonValueTypes.NULL;
    }

    /**
     * 常にnullを返します。
     * @return null
     */
    @Override
    public @Nullable Void getValue() {
        return super.getValue();
    }

    /**
     * null を表現するシングルトンオブジェクト。
     */
    public static final JsonNull NULL = new JsonNull();
}
