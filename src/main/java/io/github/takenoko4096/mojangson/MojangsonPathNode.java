package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * mojangsonパスを構成する各ノードを表現します。
 * @param <S> 親となるmojangson構造
 * @param <T> 子アクセス
 */
@NullMarked
public abstract class MojangsonPathNode<S extends MojangsonStructure, T> {
    /**
     * 子アクセスのためのキーまたは添え字。
     */
    protected final T parameter;

    /**
     * 子ノード。
     */
    @Nullable
    protected MojangsonPathNode<?, ?> child;

    /**
     * サブクラスのためのコンストラクタ。
     * @param parameter 子アクセスのためのキーまたは添え字。
     * @param child 子ノード。
     */
    protected MojangsonPathNode(T parameter, @Nullable MojangsonPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    /**
     * 第一引数に渡された構造体そのまま、及びこのノードに対応する位置にアクセスするためのキーとなる値の2つを引数に取るラムダを受け取ります。各サブクラスにてチェックや検索等その他の処理が事前に行われることがあります。
     * @param structure 任意の構造体。
     * @param function コールバック。
     * @return コールバックの戻り値そのまま。
     * @throws MojangsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合。
     * @param <U> コールバックの戻り値の型
     */
    public abstract <U> @Nullable U access(S structure, MojangsonLocationAccessProvider<S, U> function) throws MojangsonPathUnableToAccessException;

    /**
     * ノードのコピーを作成します。
     * @return ノードのディープコピー。
     */
    public abstract MojangsonPathNode<S, T> copy();

    @Override
    public abstract String toString();

    /**
     * オブジェクトのキーに対する単純なアクセスを表現するノード。
     */
    public static final class ObjectKeyNode extends MojangsonPathNode<MojangsonCompound, String> {
        ObjectKeyNode(String name, @Nullable MojangsonPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public <U> @Nullable U access(MojangsonCompound structure, MojangsonLocationAccessProvider<MojangsonCompound, @Nullable U> function) throws MojangsonPathUnableToAccessException {
            return function.use(structure, parameter);
        }

        @Override
        public MojangsonPathNode<MojangsonCompound, String> copy() {
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
    public static final class ArrayIndexNode extends MojangsonPathNode<MojangsonList, Integer> {
        ArrayIndexNode(Integer index, @Nullable MojangsonPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public <U> @Nullable U access(MojangsonList structure, MojangsonLocationAccessProvider<MojangsonList, @Nullable U> function) throws MojangsonPathUnableToAccessException {
            return function.use(structure, parameter);
        }

        @Override
        public MojangsonPathNode<MojangsonList, Integer> copy() {
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
    public static final class ObjectKeyCheckerNode extends MojangsonPathNode<MojangsonCompound, MojnagsonConditionalCompoundKey> {
        ObjectKeyCheckerNode(String name, MojangsonCompound jsonObject, @Nullable MojangsonPathNode<?, ?> child) {
            super(new MojnagsonConditionalCompoundKey(name, jsonObject), child);
        }

        @Override
        public <U> @Nullable U access(MojangsonCompound structure, MojangsonLocationAccessProvider<MojangsonCompound, @Nullable U> function) throws MojangsonPathUnableToAccessException {
            if (!structure.has(parameter.name())) return null;
            else {
                final MojangsonCompound value = structure.get(parameter.name(), MojangsonValueTypes.COMPOUND);
                final MojangsonCompound condition = parameter.compound();

                if (value.isSuperOf(condition)) {
                    return function.use(structure, parameter.name());
                }
                else return null;
            }
        }

        @Override
        public MojangsonPathNode<MojangsonCompound, MojnagsonConditionalCompoundKey> copy() {
            return new ObjectKeyCheckerNode(parameter.name(), parameter.compound(), child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "key_checker<" + parameter.name() + ", " + parameter.compound() + ">";
        }
    }

    /**
     * 配列内において条件を満たす最初の要素への探索アクセスを表現するノード。
     */
    public static final class ArrayIndexFinderNode extends MojangsonPathNode<MojangsonList, MojangsonCompound> {
        ArrayIndexFinderNode(MojangsonCompound parameter, @Nullable MojangsonPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public <U> @Nullable U access(MojangsonList structure, MojangsonLocationAccessProvider<MojangsonList, @Nullable U> function) throws MojangsonPathUnableToAccessException {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != MojangsonValueTypes.COMPOUND) {
                    continue;
                }

                final MojangsonCompound element = structure.get(i, MojangsonValueTypes.COMPOUND);

                if (element.isSuperOf(parameter)) {
                    return function.use(structure, i);
                }
                else return null;
            }

            return null;
        }

        @Override
        public MojangsonPathNode<MojangsonList, MojangsonCompound> copy() {
            return new ArrayIndexFinderNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "index_finder<" + parameter + ">";
        }
    }
}
