package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JSONArray;
import io.github.takenoko4096.json.values.JSONObject;
import io.github.takenoko4096.json.values.JSONStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * jsonパスを構成する各ノードを表現します。
 * @param <S> 親となるjson構造
 * @param <T> 子アクセス
 */
@NullMarked
public abstract class JSONPathNode<S extends JSONStructure, T> {
    /**
     * 子アクセスのためのキーまたは添え字。
     */
    protected final T parameter;

    /**
     * 子ノード。
     */
    @Nullable
    protected JSONPathNode<?, ?> child;

    /**
     * サブクラスのためのコンストラクタ。
     * @param parameter 子アクセスのためのキーまたは添え字。
     * @param child 子ノード。
     */
    protected JSONPathNode(T parameter, @Nullable JSONPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    /**
     * 第一引数に渡された構造体そのまま、及びこのノードに対応する位置にアクセスするためのキーとなる値の2つを引数に取るラムダを受け取ります。各サブクラスにてチェックや検索等その他の処理が事前に行われることがあります。
     * @param structure 任意の構造体。
     * @param function コールバック。
     * @return コールバックの戻り値そのまま。
     * @throws JSONPathUnableToAccessException 構造との不整合によりアクセスできなかった場合。
     * @param <U> コールバックの戻り値の型
     */
    public abstract <U> @Nullable U access(S structure, JSONLocationAccessProvider<S, U> function) throws JSONPathUnableToAccessException;

    /**
     * ノードのコピーを作成します。
     * @return ノードのディープコピー。
     */
    public abstract JSONPathNode<S, T> copy();

    @Override
    public abstract String toString();

    /**
     * オブジェクトのキーに対する単純なアクセスを表現するノード。
     */
    public static final class ObjectKeyNode extends JSONPathNode<JSONObject, String> {
        ObjectKeyNode(String name, @Nullable JSONPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public <U> @Nullable U access(JSONObject structure, JSONLocationAccessProvider<JSONObject, @Nullable U> function) throws JSONPathUnableToAccessException {
            return function.use(structure, parameter);
        }

        @Override
        public JSONPathNode<JSONObject, String> copy() {
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
    public static final class ArrayIndexNode extends JSONPathNode<JSONArray, Integer> {
        ArrayIndexNode(Integer index, @Nullable JSONPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public <U> @Nullable U access(JSONArray structure, JSONLocationAccessProvider<JSONArray, @Nullable U> function) throws JSONPathUnableToAccessException {
            return function.use(structure, parameter);
        }

        @Override
        public JSONPathNode<JSONArray, Integer> copy() {
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
    public static final class ObjectKeyCheckerNode extends JSONPathNode<JSONObject, JSONConditionalObjectKey> {
        ObjectKeyCheckerNode(String name, JSONObject jsonObject, @Nullable JSONPathNode<?, ?> child) {
            super(new JSONConditionalObjectKey(name, jsonObject), child);
        }

        @Override
        public <U> @Nullable U access(JSONObject structure, JSONLocationAccessProvider<JSONObject, @Nullable U> function) throws JSONPathUnableToAccessException {
            if (!structure.has(parameter.name())) return null;
            else {
                final JSONObject value = structure.get(parameter.name(), JSONValueTypes.OBJECT);
                final JSONObject condition = parameter.object();

                if (value.isSuperOf(condition)) {
                    return function.use(structure, parameter.name());
                }
                else return null;
            }
        }

        @Override
        public JSONPathNode<JSONObject, JSONConditionalObjectKey> copy() {
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
    public static final class ArrayIndexFinderNode extends JSONPathNode<JSONArray, JSONObject> {
        ArrayIndexFinderNode(JSONObject parameter, @Nullable JSONPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public <U> @Nullable U access(JSONArray structure, JSONLocationAccessProvider<JSONArray, @Nullable U> function) throws JSONPathUnableToAccessException {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != JSONValueTypes.OBJECT) {
                    continue;
                }

                final JSONObject element = structure.get(i, JSONValueTypes.OBJECT);

                if (element.isSuperOf(parameter)) {
                    return function.use(structure, i);
                }
                else return null;
            }

            return null;
        }

        @Override
        public JSONPathNode<JSONArray, JSONObject> copy() {
            return new ArrayIndexFinderNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "index_finder<" + parameter + ">";
        }
    }
}
