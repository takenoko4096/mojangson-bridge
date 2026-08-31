package io.github.takenoko4096.json;

import io.github.takenoko4096.json.node.*;
import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * json構造の任意の位置にアクセスするためのパスを表現します。
 */
public final class JsonPath {
    private final JsonPathNode<?, ?> root;

    JsonPath(JsonPathNode<?, ?> root) {
        this.root = root;
    }

    /**
     * 引数に渡された構造体に対してパスが参照する位置へのアクセスを提供します。
     * @param object ルート構造体 (オブジェクト)
     * @param createWay アクセスに必要な空のオブジェクトを自動作成するかどうか; {@code true} のときオブジェクトに対するキーによる単純なアクセスに限り空のオブジェクトを生成し例外の発生を回避します。
     * @param requirePreciseLocation 正確な位置を要求するかどうか; {@code true} のとき添字指定のない且つサイズが 1 でないリストアクセスを禁止します。
     * @param function1 構造体が {@link JsonObject} だった場合のコールバック
     * @param function2 構造体が {@link JsonArray} だった場合のコールバック
     * @return コールバックの戻り値
     * @param <U> コールバックの戻り値の型
     * @throws JsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合
     */
    public <U> @Nullable U access(JsonObject object, boolean createWay, boolean requirePreciseLocation, BiFunction<JsonObject, String, @Nullable U> function1, BiFunction<JsonArray, Integer, @Nullable U> function2) throws JsonPathUnableToAccessException {
        JsonPathNode<?, ?> node = root;
        JsonPathNode<?, ?> child;

        JsonStructure current = object;
        JsonStructure next;

        JsonValue<?> value;

        while ((child = node.getChild()) != null) {
            value = node.getValue(current);

            if (value == null) {
                next = null;
            }
            else if (value instanceof JsonStructure structure) {
                next = structure;
            }
            else {
                throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: アクセス過程で取得された値 " + value + " は構造体ではありませんが、パスはこの先にも続いています");
            }

            if (next == null) {
                if (node instanceof JsonObjectKeyNode objectKeyNode && createWay) {
                    next = new JsonObject();
                    ((JsonObject) current).set(objectKeyNode.getParameter(), next);
                }
                else {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: " + "オブジェクト " + current + " に条件 " + node.getParameter() + " を満たすキーは存在しません");
                }
            }

            current = next;
            node = child;
        }

        return node.access(requirePreciseLocation, current, function1, function2);
    }

    /**
     * jsonパスの長さを返します。
     * @return jsonパスの長さ。例えば、 "foo.bar[0].baz" は4を返します。
     */
    public int length() {
        JsonPathNode<?, ?> node = root;

        int i = 0;
        while (node != null) {
            i++;
            node = node.getChild();
        }

        return i;
    }

    /**
     * jsonパスの部分パスを作成します。
     * @param begin 開始位置。
     * @param end 終了位置。この値は含まれません。
     * @return 切り取られた部分パス。完全なコピーであり、元のオブジェクトとは関連しません。
     * @throws IllegalArgumentException {@code begin > end} 、または {@code end >=} {@link JsonPath#length()} のとき
     */
    public JsonPath slice(int begin, int end) {
        final int length = length();

        if (begin < 0) {
            begin = length + begin;
        }

        if (end < 0) {
            end = length + end;
        }

        if (begin > end) {
            throw new IllegalArgumentException("無効な範囲指定です: begin = " + begin + " > end = " + end);
        }

        if (end >= length()) {
            throw new IllegalArgumentException("無効な範囲指定です: end = " + end + " >= " + length);
        }

        JsonPathNode<?, ?> beginNode = root.copy();
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            beginNode = beginNode.getChild();
        }

        JsonPathNode<?, ?> node = beginNode;
        for (int i = begin; i < end; i++) {
            if (node == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            node = node.getChild();
        }

        if (node == null) {
            throw new IllegalStateException("NEVER HAPPENS");
        }

        node.setChild(null);

        return new JsonPath(beginNode);
    }

    /**
     * 終端のアクセスを取り除いた新しいパスを返します。
     * @return slice(0, length() - 2) の結果に等しくなります。
     */
    public JsonPath parent() {
        return slice(0, length() - 2);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("JsonPath { ");
        JsonPathNode<?, ?> node = root;

        while (node != null) {
            sb.append(node);
            node = node.getChild();

            if (node != null) {
                sb.append(".");
            }
        }

        return sb.append(" }").toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JsonPath path)) return false;
        return root.equals(path.root);
    }

    /**
     * 文字列からjsonパスを作成します。
     * @param path jsonパス
     * @return jsonパスオブジェクト
     * @throws JsonParseException パスが不正な場合
     */
    public static JsonPath of(String path) throws JsonParseException {
        return new JsonPathParser().parse(path);
    }
}
