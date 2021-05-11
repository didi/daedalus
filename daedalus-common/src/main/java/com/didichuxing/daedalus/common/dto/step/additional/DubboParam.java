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
package com.didichuxing.daedalus.common.dto.step.additional;

import com.didichuxing.daedalus.common.serializer.VirtualIdWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * A tuple of things.
 *
 * @param <DubboParamType> Type of the first thing.
 * @param <T>              Type of the second thing.
 * @author Tobias Trelle
 * @author Oliver Gierke
 * @author Christoph Strobl
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAppend(props = {@JsonAppend.Prop(name = "id", value = VirtualIdWriter.class)})
public final class DubboParam<T, V> {

    private final @NotNull(message = "Dubbo参数类型不能为空！") T type;
    private final @NotNull(message = "Dubbo参数值不能为空！") V value;

    /**
     * Creates a new {@link DubboParam} for the given elements.
     *
     * @param type  must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @return
     */
    public static <DubboParamType, T> DubboParam<DubboParamType, T> of(DubboParamType type, T value) {
        return new DubboParam<>(type, value);
    }

    /**
     * Returns the first element of the {@link DubboParam}.
     *
     * @return
     */
    public T getType() {
        return type;
    }

    /**
     * Returns the second element of the {@link DubboParam}.
     *
     * @return
     */
    public V getValue() {
        return value;
    }



}
