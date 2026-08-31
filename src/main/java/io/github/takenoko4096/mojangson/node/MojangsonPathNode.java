package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * mojangsonパスを構成する各ノードを表現します。
 * @param <S> 親となるmojangson構造
 * @param <T> 子アクセス
 */
public abstract sealed class MojangsonPathNode<S extends MojangsonStructure, T> permits MojangsonArrayIndexFinderNode, MojangsonArrayIndexNode, MojangsonArrayIndexUnspecifiedNode, MojangsonObjectKeyCheckerNode, MojangsonObjectKeyNode {
    /**
     * 子アクセスのためのキーまたは添字
     */
    protected final T parameter;

    /**
     * 子ノード
     */
    @Nullable
    private MojangsonPathNode<?, ?> child;

    /**
     * サブクラスのためのコンストラクタ。
     * @param parameter 子アクセスのためのキーまたは添字
     * @param child 子ノード
     */
    protected MojangsonPathNode(T parameter, @Nullable MojangsonPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameter, child);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MojangsonPathNode<?, ?> node)) return false;
        return getClass().equals(node.getClass()) && parameter.equals(node.parameter) && Objects.equals(child, node.child);
    }

    /**
     * 引数に渡された構造体に対してノードが参照する値を返します。
     * @param structure 任意の構造体
     * @return ノードが参照する値
     * @throws MojangsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合。
     */
    public abstract @Nullable MojangsonValue<?> getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException;

    /**
     * 引数に渡された構造体に対してノードが参照する位置へのアクセスを提供します。
     * @param requirePreciseLocation 正確な位置を要求するかどうか; {@code true} のとき添字指定のない且つサイズが 1 でないリストアクセスを禁止します。
     * @param structure 任意の構造体
     * @param function1 構造体が {@link MojangsonCompound} だった場合のコールバック
     * @param function2 構造体が {@link MojangsonList} または {@link io.github.takenoko4096.mojangson.values.MojangsonArray} だった場合のコールバック
     * @return コールバックの戻り値
     * @param <U> コールバックの戻り値の型
     * @throws MojangsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合
     */
    public <U> @Nullable U access(boolean requirePreciseLocation, MojangsonStructure structure, BiFunction<MojangsonCompound, String, @Nullable U> function1, BiFunction<MojangsonList, Integer, @Nullable U> function2) throws MojangsonPathUnableToAccessException {
        return switch (this) {
            case MojangsonObjectKeyNode k -> k.access(structure, function1);
            case MojangsonObjectKeyCheckerNode kc -> kc.access(structure, function1);
            case MojangsonArrayIndexNode a -> a.access(structure, function2);
            case MojangsonArrayIndexFinderNode af -> af.access(structure, function2);
            case MojangsonArrayIndexUnspecifiedNode ni -> ni.access(requirePreciseLocation, structure, function2);
        };
    }

    /**
     * ノードのディープコピーを作成します。
     * @return ノードのディープコピー
     */
    public abstract MojangsonPathNode<S, T> copy();

    @Override
    public abstract String toString();

    /**
     * 子ノードを取得します。
     * @return 子ノード
     */
    public @Nullable MojangsonPathNode<?, ?> getChild() {
        return child;
    }

    /**
     * 子ノードを設定します。
     * @param child 子ノード
     */
    public void setChild(@Nullable MojangsonPathNode<?, ?> child) {
        this.child = child;
    }

    /**
     * パラメータを取得します。
     * @return パラメータ
     */
    public T getParameter() {
        return parameter;
    }
}
