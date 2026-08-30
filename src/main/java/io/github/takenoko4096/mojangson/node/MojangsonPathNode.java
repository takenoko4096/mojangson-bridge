package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * mojangsonパスを構成する各ノードを表現します。
 * @param <S> 親となるmojangson構造
 * @param <T> 子アクセス
 */
public abstract sealed class MojangsonPathNode<S extends MojangsonStructure, T> permits MojangsonObjectKeyNode, MojangsonArrayIndexNode, MojangsonObjectKeyCheckerNode, MojangsonArrayIndexFinderNode {
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

    /**
     * 第一引数に渡された構造体そのまま、及びこのノードに対応する位置にアクセスするためのキーとなる値の2つを引数に取るラムダを受け取ります。各サブクラスにてチェックや検索等その他の処理が事前に行われることがあります。
     * @param structure 任意の構造体
     * @return コールバックの戻り値そのまま
     * @throws MojangsonPathUnableToAccessException 構造との不整合によりアクセスできなかった場合。
     */
    public abstract @Nullable MojangsonValue<?> getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException;

    public <U> @Nullable U access(MojangsonStructure structure, BiFunction<MojangsonCompound, String, @Nullable U> function1, BiFunction<MojangsonList, Integer, @Nullable U> function2) throws MojangsonPathUnableToAccessException {
        return switch (this) {
            case MojangsonObjectKeyNode k -> k.access(structure, function1);
            case MojangsonObjectKeyCheckerNode kc -> kc.access(structure, function1);
            case MojangsonArrayIndexNode a -> a.access(structure, function2);
            case MojangsonArrayIndexFinderNode af -> af.access(structure, function2);
        };
    }

    /**
     * ノードのコピーを作成します。
     * @return ノードのディープコピー
     */
    public abstract MojangsonPathNode<S, T> copy();

    @Override
    public abstract String toString();

    /**
     * 子ノード
     */
    public @Nullable MojangsonPathNode<?, ?> getChild() {
        return child;
    }

    public void setChild(@Nullable MojangsonPathNode<?, ?> child) {
        this.child = child;
    }

    /**
     * 子アクセスのためのキーまたは添字
     */
    public T getParameter() {
        return parameter;
    }
}
