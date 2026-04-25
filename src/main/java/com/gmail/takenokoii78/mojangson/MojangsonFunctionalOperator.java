package com.gmail.takenokoii78.mojangson;

import java.util.List;
import java.util.function.BiFunction;

@FunctionalInterface
public interface MojangsonFunctionalOperator extends BiFunction<MojangsonParser, List<MojangsonValue<?>>, MojangsonValue<?>> {

}
