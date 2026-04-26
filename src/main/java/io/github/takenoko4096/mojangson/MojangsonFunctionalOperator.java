package io.github.takenoko4096.mojangson;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface MojangsonFunctionalOperator extends Function<List<MojangsonValue<?>>, MojangsonValue<?>> {

}
