package io.github.takenoko4096.json;

import io.github.takenoko4096.json.values.JsonArray;
import io.github.takenoko4096.json.values.JsonObject;
import io.github.takenoko4096.json.values.JsonStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * json構造の任意の位置にアクセスするためのパスを表現します。
 */
@NullMarked
public final class JsonPath {
    private final JsonPathNode<?, ?> root;

    JsonPath(JsonPathNode<?, ?> root) {
        this.root = root;
    }

    private <U> @Nullable U checkedAccess(JsonPathNode<?, ?> node, @Nullable JsonStructure structure, JsonLocationAccessProvider<JsonStructure, @Nullable U> function) throws JsonPathUnableToAccessException {
        switch (node) {
            case JsonPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(structure instanceof JsonObject object)) {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " がオブジェクトである必要があります");
                }
                return objectKeyNode.access(object, function::use);
            }
            case JsonPathNode.ArrayIndexNode arrayIndexNode -> {
                if (!(structure instanceof JsonArray array)) {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " が配列である必要があります");
                }
                return arrayIndexNode.access(array, function::use);
            }
            case JsonPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(structure instanceof JsonObject object)) {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " がオブジェクトである必要があります");
                }
                return objectKeyCheckerNode.access(object, function::use);
            }
            case JsonPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(structure instanceof JsonArray array)) {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " が配列である必要があります");
                }
                return arrayIndexFinderNode.access(array, function::use);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U onTermination(JsonObject jsonObject, JsonLocationAccessProvider<JsonStructure, @Nullable U> function, boolean isForcedAccess) throws JsonPathUnableToAccessException {
        JsonPathNode<?, ?> node = root;
        JsonStructure currentStruct = jsonObject;

        while (node.child != null) {
            JsonStructure nextStruct = checkedAccess(node, currentStruct, (a, b) -> {
                final JsonValue<?> value;
                switch (a) {
                    case JsonObject obj: {
                        if (!obj.has((String) b)) return null;
                        value = obj.getOrThrow((String) b, obj.getTypeOf((String) b));
                        break;
                    }
                    case JsonArray arr: {
                        value = arr.getOrThrow((Integer) b, arr.getTypeAt((Integer) b));
                        break;
                    }
                    default: throw new IllegalStateException("NEVER HAPPENS");
                }

                if (value instanceof JsonStructure structure) {
                    return structure;
                }
                else {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: アクセス過程で取得された値 " + value + " は構造体ではありませんが、パスはこの先にも続いています");
                }
            });

            if (nextStruct == null) {
                if (node instanceof JsonPathNode.ObjectKeyNode objectKeyNode && isForcedAccess) {
                    nextStruct = new JsonObject();
                    ((JsonObject) currentStruct).set(objectKeyNode.parameter, nextStruct);
                }
                else {
                    throw new JsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: オブジェクト " + currentStruct + " に条件 " + node.parameter + " を満たすキーは存在しません");
                }
            }

            currentStruct = nextStruct;
            node = node.child;
        }

        return checkedAccess(node, currentStruct, function);
    }

    /**
     * 任意のオブジェクト上の、このパスに対応する位置へのアクセスを提供します。
     * @param jsonObject 任意のオブジェクト。
     * @param function 参照を消費するコールバック関数。
     * @param isForcedAccess trueの場合、オブジェクトのキーに対する単純なアクセスに限り、キーが存在しなくてもその位置に空のオブジェクトを作成します。これにより強制的にアクセス処理の中断を回避します。
     * @param <T> コールバックの戻り値の型。
     * @return コールバックの戻り値をそのまま返します。何も返す必要がなければnullを返すことができます。
     * @throws JsonPathUnableToAccessException オブジェクトの構造との不整合によりアクセスできなかった場合。
     */
    public <T> @Nullable T access(JsonObject jsonObject, Function<JsonPathReference<?, ?>, @Nullable T> function, boolean isForcedAccess) throws JsonPathUnableToAccessException {
        return onTermination(jsonObject, (lastStructure, nodeParameter) -> {
            final JsonPathReference<?, ?> reference = switch (lastStructure) {
                case JsonObject object -> new JsonPathReference.JsonObjectPathReference(object, (String) nodeParameter);
                case JsonArray array -> new JsonPathReference.JsonArrayPathReference(array, (Integer) nodeParameter);
                default -> throw new IllegalArgumentException("NEVER HAPPENS");
            };

            return function.apply(reference);
        }, isForcedAccess);
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
            node = node.child;
        }

        return i;
    }

    /**
     * jsonパスの部分パスを作成します。
     * @param begin 開始位置。
     * @param end 終了位置。この値は含まれません。
     * @return 切り取られた部分パス。完全なコピーであり、元のオブジェクトとは関連しません。
     */
    public JsonPath slice(int begin, int end) {
        if (begin < 0 || end > length() || begin > end) {
            throw new IllegalArgumentException("インデックスが範囲外です");
        }

        JsonPathNode<?, ?> beginNode = root;
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            beginNode = beginNode.child;
        }

        JsonPathNode<?, ?> node = beginNode;
        for (int i = begin; i < end; i++) {
            if (node == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            node = node.child;
        }

        if (node == null) {
            throw new IllegalStateException("NEVER HAPPENS");
        }

        node.child = null;

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
            node = node.child;

            if (node != null) {
                sb.append(".");
            }
        }

        return sb.append(" }").toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        else if (obj == this) return true;
        else if (obj.getClass() != getClass()) return false;
        else return toString().equals(obj.toString());
    }

    /**
     * 文字列からjsonパスを作成します。
     * @param path jsonパス
     * @return jsonパスオブジェクト
     * @throws JsonParseException パスが不正な場合。
     */
    public static JsonPath of(String path) throws JsonParseException {
        return new JsonPathParser().parse(path);
    }

    /**
     * jsonパスが構造にアクセスする際に作成される特定のオブジェクトへの参照を表現します。
     * @param <S> アクセス位置の親の構造。
     * @param <T> アクセスするために必要なキーまたは添え字。
     */
    public static abstract class JsonPathReference<S extends JsonStructure, T> {
        /**
         * アクセス位置の親の構造。
         */
        protected final S structure;

        /**
         * アクセスするために必要なキーまたは添え字。
         */
        protected final T parameter;

        /**
         * サブクラスのためのコンストラクタ。
         * @param structure アクセス位置の親の構造。
         * @param parameter アクセスするために必要なキーまたは添え字。
         */
        protected JsonPathReference(S structure, T parameter) {
            this.structure = structure;
            this.parameter = parameter;
        }

        /**
         * パスの参照先が存在するかどうかを返します。
         * @return 存在すれば真。
         */
        public abstract boolean has();

        /**
         * パスの参照先に格納された値の型を取得します。
         * @return 型オブジェクト。
         */
        public abstract JsonValueType<?> getType();

        /**
         * パスの参照先に格納された値を取得します。
         * @param type 期待する型。
         * @param <U> 期待する型。
         * @return 格納された値。
         */
        public abstract <U extends JsonValue<?>> U getOrThrow(JsonValueType<U> type);

        public abstract <U extends JsonValue<?>> @Nullable U getOrNull(JsonValueType<U> type);

        public abstract <U extends JsonValue<?>> U getOrDefault(JsonValueType<U> type, U defaultValue);

        /**
         * パスの参照先を任意の値で上書きします。
         * @param value 任意の値。
         */
        public abstract void set(Object value);

        /**
         * パスの参照先の値を削除します。
         * @return 削除に成功した場合、真。
         */
        public abstract boolean delete();

        private static final class JsonObjectPathReference extends JsonPathReference<JsonObject, String> {
            private JsonObjectPathReference(JsonObject structure, String parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @Override
            public JsonValueType<?> getType() {
                return structure.getTypeOf(parameter);
            }

            @Override
            public <U extends JsonValue<?>> U getOrThrow(JsonValueType<U> type) {
                return structure.getOrThrow(parameter, type);
            }

            @Override
            public @Nullable <U extends JsonValue<?>> U getOrNull(JsonValueType<U> type) {
                return structure.getOrNull(parameter, type);
            }

            @Override
            public <U extends JsonValue<?>> U getOrDefault(JsonValueType<U> type, U defaultValue) {
                return structure.getOrDefault(parameter, type, defaultValue);
            }

            @Override
            public void set(Object value) {
                structure.set(parameter, value);
            }

            @Override
            public boolean delete() {
                return structure.delete(parameter);
            }
        }

        private static final class JsonArrayPathReference extends JsonPathReference<JsonArray, Integer> {
            private JsonArrayPathReference(JsonArray structure, Integer parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @Override
            public JsonValueType<?> getType() {
                return structure.getTypeAt(parameter);
            }

            @Override
            public <U extends JsonValue<?>> U getOrThrow(JsonValueType<U> type) {
                return structure.getOrThrow(parameter, type);
            }

            @Override
            public @Nullable <U extends JsonValue<?>> U getOrNull(JsonValueType<U> type) {
                return structure.getOrNull(parameter, type);
            }

            @Override
            public <U extends JsonValue<?>> U getOrDefault(JsonValueType<U> type, U defaultValue) {
                return structure.getOrDefault(parameter, type, defaultValue);
            }

            @Override
            public void set(Object value) {
                structure.set(parameter, value);
            }

            @Override
            public boolean delete() {
                return structure.delete(parameter);
            }
        }
    }
}
