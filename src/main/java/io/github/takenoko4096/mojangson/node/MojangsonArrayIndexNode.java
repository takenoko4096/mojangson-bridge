package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import io.github.takenoko4096.mojangson.values.*;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * 配列の添え字に対する単純なアクセスを表現するノード。
 */
public final class MojangsonArrayIndexNode extends MojangsonPathNode<MojangsonList, Integer> {
    public MojangsonArrayIndexNode(Integer index, @Nullable MojangsonPathNode<?, ?> child) {
        super(index, child);
    }

    @Override
    public @Nullable MojangsonValue<?> getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException {
        return switch (structure) {
            case MojangsonList list -> {
                if (!list.has(parameter)) yield null;
                yield list.getOrNull(parameter, list.getTypeAt(parameter));
            }
            case MojangsonArray<?, ?> array -> {
                final MojangsonList list = array.boxed().untyped();
                if (!list.has(parameter)) yield null;
                yield list.getOrNull(parameter, list.getTypeAt(parameter));
            }
            case null, default -> throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    public @Nullable <U> U access(MojangsonStructure structure, BiFunction<MojangsonList, Integer, @Nullable U> function) throws MojangsonPathUnableToAccessException {
        return switch (structure) {
            case MojangsonList list -> function.apply(list, parameter);
            case MojangsonArray<?, ?> array -> array.write(list -> function.apply(list, parameter));
            case null, default -> throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " が配列またはリストである必要があります");
        };
    }

    @Override
    public MojangsonPathNode<MojangsonList, Integer> copy() {
        return new MojangsonArrayIndexNode(parameter, getChild() == null ? null : getChild().copy());
    }

    @Override
    public String toString() {
        return "index<" + parameter + ">";
    }
}
