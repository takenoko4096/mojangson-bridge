package io.github.takenoko4096.json.node;

import io.github.takenoko4096.json.JsonPathUnableToAccessException;
import io.github.takenoko4096.json.JsonValue;
import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * 配列の添字を明示しないアクセスを表現するノード。
 */
public final class JsonArrayIndexUnspecifiedNode extends JsonPathNode<JsonArray, Integer> {
    /**
     * {@link JsonArrayIndexUnspecifiedNode} を作成します。
     * @param child 子ノード
     */
    public JsonArrayIndexUnspecifiedNode(@Nullable JsonPathNode<?, ?> child) {
        super(0, child);
    }

    @Override
    public @Nullable JsonValue<?> getValue(JsonStructure structure) throws JsonPathUnableToAccessException {
        return switch (structure) {
            case JsonArray array -> {
                if (array.length() != 1) yield null;
                yield array.getOrNull(parameter, array.getTypeAt(parameter));
            }
            case null, default -> throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Nullable <U> U access(boolean requirePreciseLocation, JsonStructure structure, BiFunction<JsonArray, Integer, @Nullable U> function) throws JsonPathUnableToAccessException {
        return switch (structure) {
            case JsonArray array -> {
                if (!requirePreciseLocation || array.length() == 1) yield function.apply(array, parameter);
                else throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " のサイズが 1 である必要があります");
            }
            case null, default -> throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Override
    public JsonArrayIndexUnspecifiedNode copy() {
        return new JsonArrayIndexUnspecifiedNode(child == null ? null : child.copy());
    }

    @Override
    public String toString() {
        return "index_unspecified";
    }
}
