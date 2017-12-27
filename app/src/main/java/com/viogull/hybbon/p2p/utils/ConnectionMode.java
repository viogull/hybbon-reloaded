package com.viogull.hybbon.p2p.utils;



public enum ConnectionMode {
    WIFI("Wifi"),
    CELLULAR("3G"),
    OFFLINE("Offline");

    private final String name;

    ConnectionMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}