package io.github.takenoko4096.json.node;

import io.github.takenoko4096.json.JsonPathUnableToAccessException;
import io.github.takenoko4096.json.JsonValue;
import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * jsonパスを構成する各ノードを表現します。
 * @param <S> 親となるjson構造
 * @param <T> 子アクセス
 */
public abstract sealed class JsonPathNode<S extends JsonStructure, T> permits JsonArrayIndexFinderNode, JsonArrayIndexNode, JsonArrayIndexUnspecifiedNode, JsonObjectKeyCheckerNode, JsonObjectKeyNode {
    /**
     * 子アクセスのためのキーまたは添字
     */
    protected final T parameter;

    /**
     * 子ノード
     */
    @Nullable
    protected JsonPathNode<?, ?> child;

    /**
     * サブクラスのためのコンストラクタ。
     * @param parameter 子アクセスのためのキーまたは添字
     * @param child 子ノード
     */
    protected JsonPathNode(T parameter, @Nullable JsonPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter, child);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonPathNode<?, ?> node)) return false;
        return getClass().equals(node.getClass()) && parameter.equals(node.parameter) && Objects.equals(child, node.child);
    }

    /**
     * 引数に渡された構造体に対してノードが参照する値を返します。
     * @param structure 任意の構造体
     * @return ノードが参照する値
     * @throws JsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合。
     */
    public abstract @Nullable JsonValue<?> getValue(JsonStructure structure) throws JsonPathUnableToAccessException;

    /**
     * 引数に渡された構造体に対してノードが参照する位置へのアクセスを提供します。
     * @param requirePreciseLocation 正確な位置を要求するかどうか; {@code true} のとき添字指定のない且つサイズが 1 でないリストアクセスを禁止します。
     * @param structure 任意の構造体
     * @param function1 構造体が {@link JsonObject} だった場合のコールバック
     * @param function2 構造体が {@link JsonArray} だった場合のコールバック
     * @return コールバックの戻り値
     * @param <U> コールバックの戻り値の型
     * @throws JsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合
     */
    public <U> @Nullable U access(boolean requirePreciseLocation, JsonStructure structure, BiFunction<JsonObject, String, @Nullable U> function1, BiFunction<JsonArray, Integer, @Nullable U> function2) throws JsonPathUnableToAccessException {
        return switch (this) {
            case JsonObjectKeyNode k -> k.access(structure, function1);
            case JsonObjectKeyCheckerNode kc -> kc.access(structure, function1);
            case JsonArrayIndexNode a -> a.access(structure, function2);
            case JsonArrayIndexFinderNode af -> af.access(structure, function2);
            case JsonArrayIndexUnspecifiedNode ni -> ni.access(requirePreciseLocation, structure, function2);
        };
    }

    /**
     * ノードのディープコピーを作成します。
     * @return ノードのディープコピー
     */
    public abstract JsonPathNode<S, T> copy();

    /**
     * 子ノードを取得します。
     * @return 子ノード
     */
    public @Nullable JsonPathNode<?, ?> getChild() {
        return child;
    }

    /**
     * 子ノードを設定します。
     * @param child 子ノード
     */
    public void setChild(@Nullable JsonPathNode<?, ?> child) {
        this.child = child;
    }

    /**
     * パラメータを取得します。
     * @return パラメータ
     */
    public T getParameter() {
        return parameter;
    }

    @Override
    public abstract String toString();
}
