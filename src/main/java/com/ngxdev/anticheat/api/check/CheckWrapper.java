/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.api.check;

import com.ngxdev.anticheat.api.check.type.CheckType;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class CheckWrapper {
    @Setting
    private boolean alert;
    @Setting
    private boolean cancel;
    @Setting
    private boolean ban;

    @Setting
    private double alertSensitivity = 1;
    @Setting
    private double cancelSensitivity = 1;
    @Setting
    private double banSensitivity = 1;

    private int defaultViolations;
    private int defaultViolationTimeout;

    public CheckWrapper(Class<? extends Check> check) {
        CheckType type = check.getAnnotation(CheckType.class);// todo add a descriptive error
        alert = type.alert();
        cancel = type.cancel();
        ban = type.ban();
        defaultViolations = type.maxVl();
        defaultViolationTimeout = type.timeout();
        //TODO storage engine
    }
}
