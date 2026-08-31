package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * 配列内において条件を満たす最初の要素への探索アクセスを表現するノード。
 */
public final class MojangsonArrayIndexFinderNode extends MojangsonPathNode<MojangsonList, MojangsonCompound> {
    /**
     * {@link MojangsonArrayIndexFinderNode} を作成します。
     * @param condition 条件となるコンパウンド
     * @param child 子ノード
     */
    public MojangsonArrayIndexFinderNode(MojangsonCompound condition, @Nullable MojangsonPathNode<?, ?> child) {
        super(condition, child);
    }

    @Override
    public @Nullable MojangsonCompound getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException {
        if (!(structure instanceof MojangsonList list)) {
            throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がリストである必要があります");
        }

        for (int i = 0; i < list.length(); i++) {
            if (list.getTypeAt(i) != MojangsonValueTypes.COMPOUND) {
                continue;
            }

            final MojangsonCompound element = list.getOrThrow(i, MojangsonValueTypes.COMPOUND);

            if (element.isSuperOf(parameter)) {
                return element;
            }
        }

        return null;
    }

    @Nullable <U> U access(MojangsonStructure structure, BiFunction<MojangsonList, Integer, @Nullable U> function) throws MojangsonPathUnableToAccessException {
        if (!(structure instanceof MojangsonList list)) {
            throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がリストである必要があります");
        }

        for (int i = 0; i < list.length(); i++) {
            if (list.getTypeAt(i) != MojangsonValueTypes.COMPOUND) {
                continue;
            }

            final MojangsonCompound element = list.getOrThrow(i, MojangsonValueTypes.COMPOUND);

            if (element.isSuperOf(parameter)) {
                return function.apply(list, i);
            }
        }

        throw new MojangsonPathUnableToAccessException("ノード " + this + " にアクセスできませんでした: 条件 " + parameter + " を満たす要素が見つかりませんでした");
    }

    @Override
    public MojangsonPathNode<MojangsonList, MojangsonCompound> copy() {
        return new MojangsonArrayIndexFinderNode(parameter, getChild() == null ? null : getChild().copy());
    }

    @Override
    public String toString() {
        return "index_finder<" + parameter + ">";
    }
}
