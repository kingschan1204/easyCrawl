package com.github.kingschan1204.easycrawl.helper.ref.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author kingschan 2025-02-27
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {}
