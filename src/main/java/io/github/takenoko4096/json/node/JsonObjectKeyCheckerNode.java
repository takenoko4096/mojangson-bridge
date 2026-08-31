package io.github.takenoko4096.json.node;

import io.github.takenoko4096.json.JsonPathUnableToAccessException;
import io.github.takenoko4096.json.JsonValueTypes;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * オブジェクトが紐づけられたキーに対する条件付きアクセスを表現するノード。
 */
public final class JsonObjectKeyCheckerNode extends JsonPathNode<JsonObject, JsonObjectKeyCheckerNode.JsonConditionalObjectKey> {
    /**
     * {@link JsonObjectKeyCheckerNode} を作成します。
     * @param name キー名
     * @param condition 条件となるオブジェクト
     * @param child 子ノード
     */
    public JsonObjectKeyCheckerNode(String name, JsonObject condition, @Nullable JsonPathNode<?, ?> child) {
        super(new JsonConditionalObjectKey(name, condition), child);
    }

    @Override
    public @Nullable JsonObject getValue(JsonStructure structure) throws JsonPathUnableToAccessException {
        if (!(structure instanceof JsonObject object)) {
            throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        if (!object.has(parameter.name())) {
            return null;
        }

        final JsonObject value = object.getOrThrow(parameter.name(), JsonValueTypes.OBJECT);
        final JsonObject condition = parameter.object();

        if (value.isSuperOf(condition)) {
            return value;
        }
        else {
            return null;
        }
    }

    @Nullable <U> U access(JsonStructure structure, BiFunction<JsonObject, String, @Nullable U> function) throws JsonPathUnableToAccessException {
        if (!(structure instanceof JsonObject object)) {
            throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        if (!object.has(parameter.name())) {
            throw new JsonPathUnableToAccessException("ノード " + this + " にアクセスできませんでした: " + object + " にキー " + parameter.name() + " が見つかりません");
        }

        final JsonObject value = object.getOrThrow(parameter.name(), JsonValueTypes.OBJECT);
        final JsonObject condition = parameter.object();

        if (value.isSuperOf(condition)) {
            return function.apply(object, parameter.name());
        }
        else {
            throw new JsonPathUnableToAccessException("ノード " + this + " にアクセスできませんでした: " + object + " のキー " + parameter.name() + " は条件 " + parameter.object() + " を満たしません");
        }
    }

    @Override
    public JsonPathNode<JsonObject, JsonConditionalObjectKey> copy() {
        return new JsonObjectKeyCheckerNode(parameter.name(), parameter.object(), child == null ? null : child.copy());
    }

    @Override
    public String toString() {
        return "key_checker<" + parameter.name() + ", " + parameter.object() + ">";
    }

    /**
     * jsonパスにおける条件付きのキーアクセスを表現します。
     * @param name キーの名前
     * @param object 条件となるオブジェクト
     */
    public record JsonConditionalObjectKey(String name, JsonObject object) {}
}
