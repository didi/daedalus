/*
 * Copyright 2015-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.daedalus.common.dto;

import com.didichuxing.daedalus.common.serializer.VirtualIdWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A tuple of things.
 *
 * @param <S> Type of the first thing.
 * @param <T> Type of the second thing.
 * @author Tobias Trelle
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAppend(props = {@JsonAppend.Prop(name = "id", value = VirtualIdWriter.class)})
public final class Pair<S, T> {

    private final S name;
    private final T value;

    /**
     * Creates a new {@link Pair} for the given elements.
     *
     * @param first must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return
     */
    public static <S, T> Pair<S, T> of(S name, T value) {
        return new Pair<>(name, value);
    }

    /**
     * Returns the first element of the {@link Pair}.
     *
     * @return
     */
    public S getName() {
        return name;
    }

    /**
     * Returns the second element of the {@link Pair}.
     *
     * @return
     */
    public T getValue() {
        return value;
    }

    /**
     * A collector to create a {@link Map} from a {@link Stream} of {@link Pair}s.
     *
     * @return
     */
    public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
        return Collectors.toMap(Pair::getName, Pair::getValue);
    }

    public String toPairString() {
        return this.name + "=" + this.value;
    }

    public String toKVString() {
        return this.name + ":" + this.value;
    }

    public boolean valid() {
        return name != null && value != null;
    }
}
