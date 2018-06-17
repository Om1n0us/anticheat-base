package com.ngxdev.anticheat.api.check;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class MethodWrapper {
    private Check check;
    private Method method;
    private int priority;

    public void call(Object argument) throws Exception {
        if (method.getParameterTypes()[0] == argument.getClass()) {
            method.invoke(check, argument);
        }
    }
}
