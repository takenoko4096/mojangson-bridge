package io.github.takenoko4096.mojangson;

import io.github.takenoko4096.mojangson.values.MojangsonCompound;
import io.github.takenoko4096.mojangson.values.MojangsonList;
import io.github.takenoko4096.mojangson.values.MojangsonStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

@NullMarked
public abstract class MojangsonPathNode<S extends MojangsonStructure, T> {
    protected final T parameter;

    @Nullable
    protected MojangsonPathNode<?, ?> child;

    protected MojangsonPathNode(T parameter, @Nullable MojangsonPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    public abstract @Nullable MojangsonValue<?> get(S structure);

    public abstract <U> @Nullable U access(S structure, BiFunction<S, Object, U> function);

    public abstract MojangsonPathNode<S, T> copy();

    public abstract String toString();

    public static final class ObjectKeyNode extends MojangsonPathNode<MojangsonCompound, String> {
        ObjectKeyNode(String name, @Nullable MojangsonPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public @Nullable MojangsonValue<?> get(MojangsonCompound structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeOf(parameter));
        }

        @Override
        public <U> @Nullable U access(MojangsonCompound structure, BiFunction<MojangsonCompound, Object, @Nullable U> function) {
            return function.apply(structure, parameter);
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

    public static final class ArrayIndexNode extends MojangsonPathNode<MojangsonList, Integer> {
        ArrayIndexNode(Integer index, @Nullable MojangsonPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public @Nullable MojangsonValue<?> get(MojangsonList structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeAt(parameter));
        }

        @Override
        public <U> @Nullable U access(MojangsonList structure, BiFunction<MojangsonList, Object, @Nullable U> function) {
            return function.apply(structure, parameter);
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

    public static final class ObjectKeyCheckerNode extends MojangsonPathNode<MojangsonCompound, Pair<String, MojangsonCompound>> {
        ObjectKeyCheckerNode(String name, MojangsonCompound jsonObject, @Nullable MojangsonPathNode<?, ?> child) {
            super(new Pair<>(name, jsonObject), child);
        }

        @Override
        public @Nullable MojangsonCompound get(MojangsonCompound structure) {
            if (!structure.has(parameter.a())) return null;
            else {
                final MojangsonCompound value = structure.get(parameter.a(), MojangsonValueTypes.COMPOUND);

                if (value instanceof MojangsonCompound target) {
                    final MojangsonCompound condition = parameter.b();
                    if (target.isSuperOf(condition)) {
                        return value;
                    }
                    else return null;
                }
                else return null;
            }
        }

        @Override
        public <U> @Nullable U access(MojangsonCompound structure, BiFunction<MojangsonCompound, Object, @Nullable U> function) {
            if (!structure.has(parameter.a())) return null;
            else {
                final MojangsonCompound value = structure.get(parameter.a(), MojangsonValueTypes.COMPOUND);

                if (value instanceof MojangsonCompound target) {
                    final MojangsonCompound condition = parameter.b();
                    if (target.isSuperOf(condition)) {
                        // return value;
                        return function.apply(structure, parameter.a());
                    }
                    else return null;
                }
                else return null;
            }
        }

        @Override
        public MojangsonPathNode<MojangsonCompound, Pair<String, MojangsonCompound>> copy() {
            return new ObjectKeyCheckerNode(parameter.a(), parameter.b(), child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "key_checker<" + parameter.a() + ", " + parameter.b() + ">";
        }
    }

    public static final class ArrayIndexFinderNode extends MojangsonPathNode<MojangsonList, MojangsonCompound> {
        ArrayIndexFinderNode(MojangsonCompound parameter, @Nullable MojangsonPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public @Nullable MojangsonCompound get(MojangsonList structure) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != MojangsonValueTypes.COMPOUND) {
                    continue;
                }

                final MojangsonCompound element = structure.get(i, MojangsonValueTypes.COMPOUND);

                if (element instanceof MojangsonCompound object) {
                    if (object.isSuperOf(parameter)) {
                        return element;
                    }
                    else return null;
                }
                else return null;
            }

            return null;
        }

        @Override
        public <U> @Nullable U access(MojangsonList structure, BiFunction<MojangsonList, Object, U> function) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != MojangsonValueTypes.LIST) {
                    continue;
                }

                final MojangsonCompound element = structure.get(i, MojangsonValueTypes.COMPOUND);

                if (element instanceof MojangsonCompound object) {
                    if (object.isSuperOf(parameter)) {
                        // return element;
                        return function.apply(structure, i);
                    }
                    else return null;
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

        public static final class MojangsonArrayIndexNotFoundException extends Exception {
            private MojangsonArrayIndexNotFoundException(String message) {
                super(message);
            }
        }
    }

    public record Pair<A, B>(A a, B b) {}
}
