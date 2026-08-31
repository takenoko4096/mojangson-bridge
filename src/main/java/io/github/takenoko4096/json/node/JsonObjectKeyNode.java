package io.github.takenoko4096.json.node;

import io.github.takenoko4096.json.JsonPathUnableToAccessException;
import io.github.takenoko4096.json.JsonValue;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * オブジェクトのキーに対する単純なアクセスを表現するノード。
 */
public final class JsonObjectKeyNode extends JsonPathNode<JsonObject, String> {
    /**
     * {@link JsonObjectKeyNode} を作成します。
     * @param name キー名
     * @param child 子ノード
     */
    public JsonObjectKeyNode(String name, @Nullable JsonPathNode<?, ?> child) {
        super(name, child);
    }

    @Override
    public @Nullable JsonValue<?> getValue(JsonStructure structure) throws JsonPathUnableToAccessException {
        if (!(structure instanceof JsonObject object)) {
            throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        if (!object.has(parameter)) {
            return null;
        }

        return object.getOrNull(parameter, object.getTypeOf(parameter));
    }

    <U> @Nullable U access(JsonStructure structure, BiFunction<JsonObject, String, @Nullable U> function) throws JsonPathUnableToAccessException {
        if (!(structure instanceof JsonObject object)) {
            throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        return function.apply(object, parameter);
    }

    @Override
    public JsonPathNode<JsonObject, String> copy() {
        return new JsonObjectKeyNode(parameter, child == null ? null : child.copy());
    }

    @Override
    public String toString() {
        return "key<" + parameter + ">";
    }
}
