/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

public interface CustomEvent<T> {
    void event(T event) throws Exception;
}
