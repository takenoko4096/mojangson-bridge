package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValue;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * オブジェクトのキーに対する単純なアクセスを表現するノード。
 */
public final class MojangsonObjectKeyNode extends MojangsonPathNode<MojangsonCompound, String> {
    /**
     * {@link MojangsonObjectKeyNode} を作成します。
     * @param name キー名
     * @param child 子ノード
     */
    public MojangsonObjectKeyNode(String name, @Nullable MojangsonPathNode<?, ?> child) {
        super(name, child);
    }

    @Override
    public @Nullable MojangsonValue<?> getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException {
        if (!(structure instanceof MojangsonCompound compound)) {
            throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        if (!compound.has(parameter)) {
            return null;
        }

        return compound.getOrNull(parameter, compound.getTypeOf(parameter));
    }

    <U> @Nullable U access(MojangsonStructure structure, BiFunction<MojangsonCompound, String, @Nullable U> function) throws MojangsonPathUnableToAccessException {
        if (!(structure instanceof MojangsonCompound compound)) {
            throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        return function.apply(compound, parameter);
    }

    @Override
    public MojangsonPathNode<MojangsonCompound, String> copy() {
        return new MojangsonObjectKeyNode(parameter, getChild() == null ? null : getChild().copy());
    }

    @Override
    public String toString() {
        return "key<" + parameter + ">";
    }
}
