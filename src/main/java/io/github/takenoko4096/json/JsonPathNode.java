package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * jsonパスを構成する各ノードを表現します。
 * @param <S> 親となるjson構造
 * @param <T> 子アクセス
 */
@NullMarked
public abstract class JsonPathNode<S extends JsonStructure, T> {
    /**
     * 子アクセスのためのキーまたは添え字。
     */
    protected final T parameter;

    /**
     * 子ノード。
     */
    @Nullable
    protected JsonPathNode<?, ?> child;

    /**
     * サブクラスのためのコンストラクタ。
     * @param parameter 子アクセスのためのキーまたは添え字。
     * @param child 子ノード。
     */
    protected JsonPathNode(T parameter, @Nullable JsonPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    /**
     * 第一引数に渡された構造体そのまま、及びこのノードに対応する位置にアクセスするためのキーとなる値の2つを引数に取るラムダを受け取ります。各サブクラスにてチェックや検索等その他の処理が事前に行われることがあります。
     * @param structure 任意の構造体。
     * @param function コールバック。
     * @return コールバックの戻り値そのまま。
     * @throws JsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合。
     * @param <U> コールバックの戻り値の型
     */
    public abstract <U> @Nullable U access(S structure, JsonLocationAccessProvider<S, U> function) throws JsonPathUnableToAccessException;

    /**
     * ノードのコピーを作成します。
     * @return ノードのディープコピー。
     */
    public abstract JsonPathNode<S, T> copy();

    @Override
    public abstract String toString();

    /**
     * オブジェクトのキーに対する単純なアクセスを表現するノード。
     */
    public static final class ObjectKeyNode extends JsonPathNode<JsonObject, String> {
        ObjectKeyNode(String name, @Nullable JsonPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public <U> @Nullable U access(JsonObject structure, JsonLocationAccessProvider<JsonObject, @Nullable U> function) throws JsonPathUnableToAccessException {
            return function.use(structure, parameter);
        }

        @Override
        public JsonPathNode<JsonObject, String> copy() {
            return new ObjectKeyNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "key<" + parameter + ">";
        }
    }

    /**
     * 配列の添え字に対する単純なアクセスを表現するノード。
     */
    public static final class ArrayIndexNode extends JsonPathNode<JsonArray, Integer> {
        ArrayIndexNode(Integer index, @Nullable JsonPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public <U> @Nullable U access(JsonArray structure, JsonLocationAccessProvider<JsonArray, @Nullable U> function) throws JsonPathUnableToAccessException {
            return function.use(structure, parameter);
        }

        @Override
        public JsonPathNode<JsonArray, Integer> copy() {
            return new ArrayIndexNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "index<" + parameter + ">";
        }
    }

    /**
     * オブジェクトが紐づけられたキーに対する条件付きアクセスを表現するノード。
     */
    public static final class ObjectKeyCheckerNode extends JsonPathNode<JsonObject, JsonConditionalObjectKey> {
        ObjectKeyCheckerNode(String name, JsonObject jsonObject, @Nullable JsonPathNode<?, ?> child) {
            super(new JsonConditionalObjectKey(name, jsonObject), child);
        }

        @Override
        public <U> @Nullable U access(JsonObject structure, JsonLocationAccessProvider<JsonObject, @Nullable U> function) throws JsonPathUnableToAccessException {
            if (!structure.has(parameter.name())) return null;
            else {
                final JsonObject value = structure.getOrThrow(parameter.name(), JsonValueTypes.OBJECT);
                final JsonObject condition = parameter.object();

                if (value.isSuperOf(condition)) {
                    return function.use(structure, parameter.name());
                }
                else return null;
            }
        }

        @Override
        public JsonPathNode<JsonObject, JsonConditionalObjectKey> copy() {
            return new ObjectKeyCheckerNode(parameter.name(), parameter.object(), child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "key_checker<" + parameter.name() + ", " + parameter.object() + ">";
        }
    }

    /**
     * 配列内において条件を満たす最初の要素への探索アクセスを表現するノード。
     */
    public static final class ArrayIndexFinderNode extends JsonPathNode<JsonArray, JsonObject> {
        ArrayIndexFinderNode(JsonObject parameter, @Nullable JsonPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public <U> @Nullable U access(JsonArray structure, JsonLocationAccessProvider<JsonArray, @Nullable U> function) throws JsonPathUnableToAccessException {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != JsonValueTypes.OBJECT) {
                    continue;
                }

                final JsonObject element = structure.getOrThrow(i, JsonValueTypes.OBJECT);

                if (element.isSuperOf(parameter)) {
                    return function.use(structure, i);
                }
                else return null;
            }

            return null;
        }

        @Override
        public JsonPathNode<JsonArray, JsonObject> copy() {
            return new ArrayIndexFinderNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "index_finder<" + parameter + ">";
        }
    }
}
