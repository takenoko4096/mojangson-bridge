package io.github.takenoko4096.json.node;

import io.github.takenoko4096.json.JsonPathUnableToAccessException;
import io.github.takenoko4096.json.JsonValue;
import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * 配列の添え字に対する単純なアクセスを表現するノード。
 */
public final class JsonArrayIndexNode extends JsonPathNode<JsonArray, Integer> {
    /**
     * {@link JsonArrayIndexNode} を作成します。
     * @param index インデックス
     * @param child 子ノード
     */
    public JsonArrayIndexNode(Integer index, @Nullable JsonPathNode<?, ?> child) {
        super(index, child);
    }

    @Override
    public @Nullable JsonValue<?> getValue(JsonStructure structure) throws JsonPathUnableToAccessException {
        return switch (structure) {
            case JsonArray array -> {
                if (!array.has(parameter)) yield null;
                yield array.getOrNull(parameter, array.getTypeAt(parameter));
            }
            case null, default -> throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Nullable <U> U access(JsonStructure structure, BiFunction<JsonArray, Integer, @Nullable U> function) throws JsonPathUnableToAccessException {
        return switch (structure) {
            case JsonArray array -> function.apply(array, parameter);
            case null, default -> throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Override
    public JsonPathNode<JsonArray, Integer> copy() {
        return new JsonArrayIndexNode(parameter, child == null ? null : child.copy());
    }

    @Override
    public String toString() {
        return "index<" + parameter + ">";
    }
}
