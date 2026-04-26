package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.MojangsonArray;
import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@NullMarked
public final class MojangsonPath {
    private final MojangsonPathNode<?, ?> root;

    MojangsonPath(MojangsonPathNode<?, ?> root) {
        this.root = root;
    }

    private @Nullable MojangsonValue<?> getNextValue(MojangsonPathNode<?, ?> node, @Nullable MojangsonValue<?> p) {
        switch (node) {
            case MojangsonPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.get(object);
            }
            case MojangsonPathNode.ArrayIndexNode arrayIndexNode -> {
                if (!(p instanceof MojangsonList array)) {
                    throw new IllegalArgumentException(p.getClass().getName());
                }
                return arrayIndexNode.get(array);
            }
            case MojangsonPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.get(object);
            }
            case MojangsonPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof MojangsonList array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.get(array);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U useNextValue(MojangsonPathNode<?, ?> node, @Nullable MojangsonValue<?> p, BiFunction<MojangsonStructure, Object, U> function) {
        switch (node) {
            case MojangsonPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.access(object, function::apply);
            }
            case MojangsonPathNode.ArrayIndexNode arrayIndexNode -> {
                if (p == null) {
                    throw new IllegalArgumentException();
                }

                return switch (p) {
                    case MojangsonList list -> arrayIndexNode.access(list, function::apply);
                    case MojangsonArray<?, ?> array -> arrayIndexNode.access(array.listView(), function::apply);
                    default -> throw new IllegalArgumentException();
                };
            }
            case MojangsonPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.access(object, function::apply);
            }
            case MojangsonPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof MojangsonList array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.access(array, function::apply);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U onLastNode(MojangsonCompound compound, BiFunction<MojangsonStructure, Object, U> function, boolean isForcedAccess) throws MojangsonInaccessiblePathException {
        MojangsonPathNode<?, ?> node = root;
        MojangsonValue<?> p = compound;

        while (node.child != null) {
            var q = getNextValue(node, p);

            if (q == null) {
                if (node instanceof MojangsonPathNode.ObjectKeyNode n && isForcedAccess) {
                    q = new MojangsonCompound();
                    ((MojangsonCompound) p).set(n.parameter, q);
                }
                else {
                    throw new MojangsonInaccessiblePathException(node.parameter);
                }
            }

            p = q;
            node = node.child;
        }

        return useNextValue(node, p, function);
    }

    public <T> @Nullable T access(MojangsonCompound MojangsonCompound, Function<MojangsonPathReference<?, ?>, @Nullable T> function, boolean isForcedAccess) throws MojangsonInaccessiblePathException {
        return onLastNode(MojangsonCompound, (lastStructure, nodeParameter) -> {
            final MojangsonPathReference<?, ?> reference = switch (lastStructure) {
                case MojangsonCompound object -> new MojangsonPathReference.MojangsonCompoundPathReference(object, (String) nodeParameter);
                case MojangsonList array -> new MojangsonPathReference.MojangsonListPathReference(array, (Integer) nodeParameter);
                default -> throw new IllegalArgumentException("NEVER HAPPENS");
            };

            return function.apply(reference);
        }, isForcedAccess);
    }

    public int length() {
        MojangsonPathNode<?, ?> node = root;

        int i = 0;
        while (node != null) {
            i++;
            node = node.child;
        }

        return i;
    }

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

    public static MojangsonPath of(String path) throws MojangsonParseException {
        return MojangsonPathParser.parse(path);
    }

    public static abstract class MojangsonPathReference<S extends MojangsonStructure, T> {
        protected final S structure;

        protected final T parameter;

        protected MojangsonPathReference(S structure, T parameter) {
            this.structure = structure;
            this.parameter = parameter;
        }

        public abstract boolean has();

        public abstract MojangsonValueType<?> getType();

        public abstract <U extends MojangsonValue<?>> U get(MojangsonValueType<U> type);

        public abstract void set(Object value);

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

    public static final class MojangsonInaccessiblePathException extends Exception {
        private MojangsonInaccessiblePathException(Object nodeParameter) {
            super("パスに対応する値へのアクセスに失敗しました: 条件 " + nodeParameter + " を満たすキーは存在しません");
        }
    }
}
