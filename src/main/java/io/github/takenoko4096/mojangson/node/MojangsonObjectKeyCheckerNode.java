package io.github.takenoko4096.mojangson.node;

import io.github.takenoko4096.mojangson.MojangsonConditionalCompoundKey;
import io.github.takenoko4096.mojangson.MojangsonPathUnableToAccessException;
import io.github.takenoko4096.mojangson.MojangsonValueTypes;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * オブジェクトが紐づけられたキーに対する条件付きアクセスを表現するノード。
 */
public final class MojangsonObjectKeyCheckerNode extends MojangsonPathNode<MojangsonCompound, MojangsonConditionalCompoundKey> {
    public MojangsonObjectKeyCheckerNode(String name, MojangsonCompound compound, @Nullable MojangsonPathNode<?, ?> child) {
        super(new MojangsonConditionalCompoundKey(name, compound), child);
    }

    @Override
    public @Nullable MojangsonCompound getValue(MojangsonStructure structure) throws MojangsonPathUnableToAccessException {
        if (!(structure instanceof MojangsonCompound compound)) {
            throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        if (!compound.has(parameter.name())) {
            return null;
        }

        final MojangsonCompound value = compound.getOrThrow(parameter.name(), MojangsonValueTypes.COMPOUND);
        final MojangsonCompound condition = parameter.compound();

        if (value.isSuperOf(condition)) {
            return value;
        }
        else {
            return null;
        }
    }

    public @Nullable <U> U access(MojangsonStructure structure, BiFunction<MojangsonCompound, String, @Nullable U> function) throws MojangsonPathUnableToAccessException {
        if (!(structure instanceof MojangsonCompound compound)) {
            throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: ノード " + this + " にアクセスするには " + structure + " がコンパウンドである必要があります");
        }

        if (!compound.has(parameter.name())) {
            throw new MojangsonPathUnableToAccessException("ノード " + this + " にアクセスできませんでした: " + compound + " にキー " + parameter.name() + " が見つかりません");
        }

        final MojangsonCompound value = compound.getOrThrow(parameter.name(), MojangsonValueTypes.COMPOUND);
        final MojangsonCompound condition = parameter.compound();

        if (value.isSuperOf(condition)) {
            return function.apply(compound, parameter.name());
        }
        else {
            throw new MojangsonPathUnableToAccessException("ノード " + this + " にアクセスできませんでした: " + compound + " のキー " + parameter.name() + " は条件 " + parameter.compound() + " を満たしません");
        }
    }

    @Override
    public MojangsonPathNode<MojangsonCompound, MojangsonConditionalCompoundKey> copy() {
        return new MojangsonObjectKeyCheckerNode(parameter.name(), parameter.compound(), getChild() == null ? null : getChild().copy());
    }

    @Override
    public String toString() {
        return "key_checker<" + parameter.name() + ", " + parameter.compound() + ">";
    }
}
