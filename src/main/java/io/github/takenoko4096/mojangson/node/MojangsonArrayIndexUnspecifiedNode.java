package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.values.MojangsonArray;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * 配列の添字を明示しないアクセスを表現するノード。
 */
public final class MojangsonArrayIndexUnspecifiedNode extends MojangsonPathNode<MojangsonList, Integer> {
    /**
     * {@link MojangsonArrayIndexUnspecifiedNode} を作成します。
     * @param child 子ノード
     */
    public MojangsonArrayIndexUnspecifiedNode(@Nullable MojangsonPathNode<?, ?> child) {
        super(0, child);
    }

    @Override
    public @Nullable MojangsonValue<?> getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException {
        return switch (structure) {
            case MojangsonList list -> {
                if (list.length() != 1) yield null;
                yield list.getOrNull(parameter, list.getTypeAt(parameter));
            }
            case MojangsonArray<?, ?> array -> {
                final MojangsonList list = array.untypedView();
                if (list.length() != 1) yield null;
                yield list.getOrNull(parameter, list.getTypeAt(parameter));
            }
            case null, default -> throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Nullable <U> U access(boolean requirePreciseLocation, MojangsonStructure structure, BiFunction<MojangsonList, Integer, @Nullable U> function) throws MojangsonPathUnableToAccessException {
        return switch (structure) {
            case MojangsonList list -> {
                if (!requirePreciseLocation || list.length() == 1) yield function.apply(list, parameter);
                else throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " のサイズが 1 である必要があります");
            }
            case MojangsonArray<?, ?> array -> {
                if (!requirePreciseLocation || array.length() == 1) yield function.apply(array.untypedView(), parameter);
                else throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " のサイズが 1 である必要があります");
            }
            case null, default -> throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Override
    public MojangsonArrayIndexUnspecifiedNode copy() {
        return new MojangsonArrayIndexUnspecifiedNode(getChild() == null ? null : getChild().copy());
    }

    @Override
    public String toString() {
        return "index_unspecified";
    }
}
