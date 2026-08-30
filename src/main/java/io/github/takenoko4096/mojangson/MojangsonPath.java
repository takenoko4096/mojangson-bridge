package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.node.*;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * mojangson構造の任意の位置にアクセスするためのパスを表現します。
 */
public final class MojangsonPath {
    private final MojangsonPathNode<?, ?> root;

    MojangsonPath(MojangsonPathNode<?, ?> root) {
        this.root = root;
    }

    public <U> @Nullable U access(MojangsonCompound compound, boolean createWay, BiFunction<MojangsonCompound, String, @Nullable U> function1, BiFunction<MojangsonList, Integer, @Nullable U> function2) throws MojangsonPathUnableToAccessException {
        MojangsonPathNode<?, ?> node = root;
        MojangsonPathNode<?, ?> child;

        MojangsonStructure current = compound;
        MojangsonStructure next;

        MojangsonValue<?> value;

        while ((child = node.getChild()) != null) {
            value = node.getValue(current);

            if (value == null) {
                next = null;
            }
            else if (value instanceof MojangsonStructure structure) {
                next = structure;
            }
            else {
                throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: アクセス過程で取得された値 " + value + " は構造体ではありませんが、パスはこの先にも続いています");
            }

            if (next == null) {
                if (node instanceof MojangsonObjectKeyNode objectKeyNode && createWay) {
                    next = new MojangsonCompound();
                    ((MojangsonCompound) current).set(objectKeyNode.getParameter(), next);
                }
                else {
                    throw new MojangsonPathUnableToAccessException("パスに対応する値へのアクセスに失敗しました: " + "オブジェクト " + current + " に条件 " + node.getParameter() + " を満たすキーは存在しません");
                }
            }

            current = next;
            node = child;
        }

        return node.access(current, function1, function2);
    }

    /**
     * mojangsonパスの長さを返します。
     * @return mojangsonパスの長さ 例えば、 "foo.bar[0].baz" は4を返します。
     */
    public int length() {
        MojangsonPathNode<?, ?> node = root;

        int i = 0;
        while (node != null) {
            i++;
            node = node.getChild();
        }

        return i;
    }

    /**
     * mojangsonパスの部分パスを作成します。
     * @param begin 開始位置
     * @param end 終了位置 この値は含まれません。
     * @return 切り取られた部分パス 完全なコピーであり、元のオブジェクトとは関連しません。
     */
    public MojangsonPath slice(int begin, int end) {
        final int length = length();

        if (begin < 0) {
            begin = length + begin;
        }

        if (end < 0) {
            end = length + end;
        }

        if (begin > end) {
            throw new IllegalArgumentException("無効な範囲指定です: begin = " + begin + " > end = " + end);
        }

        if (end >= length()) {
            throw new IllegalArgumentException("無効な範囲指定です: end = " + end + " >= " + length);
        }

        MojangsonPathNode<?, ?> beginNode = root.copy();
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            beginNode = beginNode.getChild();
        }

        MojangsonPathNode<?, ?> node = beginNode;
        for (int i = begin; i < end; i++) {
            if (node == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            node = node.getChild();
        }

        if (node == null) {
            throw new IllegalStateException("NEVER HAPPENS");
        }

        node.setChild(null);

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
            node = node.getChild();

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
     * @throws MojangsonParseException パスが不正な場合
     */
    public static MojangsonPath of(String path) throws MojangsonParseException {
        return new MojangsonPathParser().parse(path);
    }
}
