package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.MojangsonArray;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * mojangson構造の任意の位置にアクセスするためのパスを表現します。
 */
@NullMarked
public final class MojangsonPath {
    private final MojangsonPathNode<?, ?> root;

    MojangsonPath(MojangsonPathNode<?, ?> root) {
        this.root = root;
    }

    private <U> @Nullable U checkedAccess(MojangsonPathNode<?, ?> node, @Nullable MojangsonStructure structure, MojangsonLocationAccessProvider<MojangsonStructure, @Nullable U> function) throws MojangsonPathUnableToAccessException {
        switch (node) {
            case MojangsonPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(structure instanceof MojangsonCompound object)) {
                    throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " がコンパウンドである必要があります");
                }
                return objectKeyNode.access(object, function::use);
            }
            case MojangsonPathNode.ArrayIndexNode arrayIndexNode -> {
                return switch (structure) {
                    case MojangsonList list -> arrayIndexNode.access(list, function::use);
                    case MojangsonArray<?, ?> array -> arrayIndexNode.access(array.listView(), function::use);
                    case null, default -> throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " が配列またはリストである必要があります");
                };
            }
            case MojangsonPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(structure instanceof MojangsonCompound object)) {
                    throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " がコンパウンドである必要があります");
                }
                return objectKeyCheckerNode.access(object, function::use);
            }
            case MojangsonPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(structure instanceof MojangsonList array)) {
                    throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + node + " にアクセスするには " + structure + " がリストである必要があります");
                }
                return arrayIndexFinderNode.access(array, function::use);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U onTermination(MojangsonCompound compound, MojangsonLocationAccessProvider<MojangsonStructure, @Nullable U> function, boolean isForcedAccess) throws MojangsonPathUnableToAccessException {
        MojangsonPathNode<?, ?> node = root;
        MojangsonStructure currentStruct = compound;

        while (node.child != null) {
            MojangsonStructure nextStruct = checkedAccess(node, currentStruct, (a, b) -> {
                final MojangsonValue<?> value;
                switch (a) {
                    case MojangsonCompound obj: {
                        if (!obj.has((String) b)) return null;
                        value = obj.get((String) b, obj.getTypeOf((String) b));
                        break;
                    }
                    case MojangsonList arr: {
                        value = arr.get((Integer) b, arr.getTypeAt((Integer) b));
                        break;
                    }
                    default: throw new IllegalStateException("NEVER HAPPENS");
                }

                if (value instanceof MojangsonStructure structure) {
                    return structure;
                }
                else {
                    throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: アクセス過程で取得された値 " + value + " は構造体ではありませんが、パスはこの先にも続いています");
                }
            });

            if (nextStruct == null) {
                if (node instanceof MojangsonPathNode.ObjectKeyNode objectKeyNode && isForcedAccess) {
                    nextStruct = new MojangsonCompound();
                    ((MojangsonCompound) currentStruct).set(objectKeyNode.parameter, nextStruct);
                }
                else {
                    throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: 条件 " + node.parameter + " を満たすキーは存在しません");
                }
            }

            currentStruct = nextStruct;
            node = node.child;
        }

        return checkedAccess(node, currentStruct, function);
    }

    /**
     * 任意のコンパウンド上の、このパスに対応する位置へのアクセスを提供します。
     * @param mojangsonCompound 任意のコンパウンド。
     * @param function 参照を消費するコールバック関数。
     * @param isForcedAccess trueの場合、コンパウンドのキーに対する単純なアクセスに限り、キーが存在しなくてもその位置に空のコンパウンドを作成します。これにより強制的にアクセス処理の中断を回避します。
     * @param <T> コールバックの戻り値の型。
     * @return コールバックの戻り値をそのまま返します。何も返す必要がなければnullを返すことができます。
     * @throws MojangsonPathUnableToAccessException コンパウンドの構造との不整合によりアクセスできなかった場合。
     */
    public <T> @Nullable T access(MojangsonCompound mojangsonCompound, Function<MojangsonPathReference<?, ?>, @Nullable T> function, boolean isForcedAccess) throws MojangsonPathUnableToAccessException {
        return onTermination(mojangsonCompound, (lastStructure, nodeParameter) -> {
            final MojangsonPathReference<?, ?> reference = switch (lastStructure) {
                case MojangsonCompound object -> new MojangsonPathReference.MojangsonCompoundPathReference(object, (String) nodeParameter);
                case MojangsonList array -> new MojangsonPathReference.MojangsonListPathReference(array, (Integer) nodeParameter);
                default -> throw new IllegalArgumentException("NEVER HAPPENS");
            };

            return function.apply(reference);
        }, isForcedAccess);
    }

    /**
     * mojangsonパスの長さを返します。
     * @return mojangsonパスの長さ。例えば、 "foo.bar[0].baz" は4を返します。
     */
    public int length() {
        MojangsonPathNode<?, ?> node = root;

        int i = 0;
        while (node != null) {
            i++;
            node = node.child;
        }

        return i;
    }

    /**
     * mojangsonパスの部分パスを作成します。
     * @param begin 開始位置。
     * @param end 終了位置。この値は含まれません。
     * @return 切り取られた部分パス。完全なコピーであり、元のオブジェクトとは関連しません。
     */
    public MojangsonPath slice(int begin, int end) {
        if (begin < 0 || end > length() || begin > end) {
            throw new IllegalArgumentException("インデックスが範囲外です");
        }

        MojangsonPathNode<?, ?> beginNode = root;
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            beginNode = beginNode.child;
        }

        MojangsonPathNode<?, ?> node = beginNode;
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

        return new MojangsonPath(beginNode);
    }

    /**
     * 終端のアクセスを取り除いた新しいパスを返します。
     * @return slice(0, length() - 2) の結果に等しくなります。
     */
    public MojangsonPath parent() {
        return slice(0, length() - 2);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MojangsonPath { ");
        MojangsonPathNode<?, ?> node = root;

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
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (obj == this) return true;
        else if (obj.getClass() != getClass()) return false;
        else return toString().equals(obj.toString());
    }

    /**
     * 文字列からmojangsonパスを作成します。
     * @param path mojangsonパス
     * @return mojangsonパスオブジェクト
     * @throws MojangsonParseException パスが不正な場合。
     */
    public static MojangsonPath of(String path) throws MojangsonParseException {
        return new MojangsonPathParser().parse(path);
    }

    /**
     * mojangsonパスが構造にアクセスする際に作成される特定のオブジェクトへの参照を表現します。
     * @param <S> アクセス位置の親の構造。
     * @param <T> アクセスするために必要なキーまたは添え字。
     */
    public static abstract class MojangsonPathReference<S extends MojangsonStructure, T> {
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
        protected MojangsonPathReference(S structure, T parameter) {
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
        public abstract MojangsonValueType<?> getType();

        /**
         * パスの参照先に格納された値を取得します。
         * @param type 期待する型。
         * @param <U> 期待する型。
         * @return 格納された値。
         */
        public abstract <U extends MojangsonValue<?>> U get(MojangsonValueType<U> type);

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

        private static final class MojangsonCompoundPathReference extends MojangsonPathReference<MojangsonCompound, String> {
            private MojangsonCompoundPathReference(MojangsonCompound structure, String parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @Override
            public MojangsonValueType<?> getType() {
                return structure.getTypeOf(parameter);
            }

            @Override
            public <U extends MojangsonValue<?>> U get(MojangsonValueType<U> type) {
                return structure.get(parameter, type);
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

        private static final class MojangsonListPathReference extends MojangsonPathReference<MojangsonList, Integer> {
            private MojangsonListPathReference(MojangsonList structure, Integer parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @Override
            public MojangsonValueType<?> getType() {
                return structure.getTypeAt(parameter);
            }

            @Override
            public <U extends MojangsonValue<?>> U get(MojangsonValueType<U> type) {
                return structure.get(parameter, type);
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
