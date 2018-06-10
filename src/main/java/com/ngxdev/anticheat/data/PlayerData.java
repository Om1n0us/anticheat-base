/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.data;

import com.ngxdev.anticheat.api.check.Check;

// Note, the only reason these don't have getters and setters to make the code cleaner in checks
public class PlayerData {
    public _velocity velocity = new _velocity();
    public _movement movement = new _movement();
    public _debug debug = new _debug();

    public class _velocity {

    }

    public class _movement {
        public double fx, fy, fz; // Last location
        public double tx, ty, tz; // Current location
        public double deltaH;
        public double deltaV;
    }

    public class _debug {
        public Check check;
    }
}
