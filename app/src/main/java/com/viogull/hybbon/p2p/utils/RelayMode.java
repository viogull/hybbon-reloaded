package com.viogull.hybbon.p2p.utils;

/**
 * Created by ghost on 27.12.2017.
 */

public enum RelayMode {
    GCM(0),
    TCP(1),
    FULL(2);

    private final int spinnerPosition;

    RelayMode(int spinnerPosition) {
        this.spinnerPosition = spinnerPosition;
    }

    public int spinnerPosition() {
        return spinnerPosition;
    }

    public static RelayMode getByPosition(int pos) {
        for (RelayMode mode : RelayMode.values()) {
            if (mode.spinnerPosition() == pos) {
                return mode;
            }
        }

        // by default
        return TCP;
    }
}