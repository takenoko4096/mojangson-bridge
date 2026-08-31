package io.github.takenoko4096.json.node;

import io.github.takenoko4096.json.JsonPathUnableToAccessException;
import io.github.takenoko4096.json.JsonValueTypes;
import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * 配列内において条件を満たす最初の要素への探索アクセスを表現するノード。
 */
public final class JsonArrayIndexFinderNode extends JsonPathNode<JsonArray, JsonObject> {
    /**
     * {@link JsonArrayIndexFinderNode} を作成します。
     * @param condition 条件となるコンパウンド
     * @param child 子ノード
     */
    public JsonArrayIndexFinderNode(JsonObject condition, @Nullable JsonPathNode<?, ?> child) {
        super(condition, child);
    }

    @Override
    public @Nullable JsonObject getValue(JsonStructure structure) throws JsonPathUnableToAccessException {
        if (!(structure instanceof JsonArray array)) {
            throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がリストである必要があります");
        }

        for (int i = 0; i < array.length(); i++) {
            if (array.getTypeAt(i) != JsonValueTypes.OBJECT) {
                continue;
            }

            final JsonObject element = array.getOrThrow(i, JsonValueTypes.OBJECT);

            if (element.isSuperOf(parameter)) {
                return element;
            }
        }

        return null;
    }

    @Nullable <U> U access(JsonStructure structure, BiFunction<JsonArray, Integer, @Nullable U> function) throws JsonPathUnableToAccessException {
        if (!(structure instanceof JsonArray array)) {
            throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がリストである必要があります");
        }

        for (int i = 0; i < array.length(); i++) {
            if (array.getTypeAt(i) != JsonValueTypes.OBJECT) {
                continue;
            }

            final JsonObject element = array.getOrThrow(i, JsonValueTypes.OBJECT);

            if (element.isSuperOf(parameter)) {
                return function.apply(array, i);
            }
        }

        throw new JsonPathUnableToAccessException("ノード " + this + " にアクセスできませんでした: 条件 " + parameter + " を満たす要素が見つかりませんでした");
    }

    @Override
    public JsonPathNode<JsonArray, JsonObject> copy() {
        return new JsonArrayIndexFinderNode(parameter, child == null ? null : child.copy());
    }

    @Override
    public String toString() {
        return "index_finder<" + parameter + ">";
    }
}
