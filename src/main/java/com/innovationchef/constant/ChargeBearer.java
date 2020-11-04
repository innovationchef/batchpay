package com.innovationchef.constant;

public enum ChargeBearer {
    OUR("OUR"), BEN("BEN"), SHA("SHA");

    private String def;

    ChargeBearer(String def) {
        this.def = def;
    }

    public String getDef() {
        return this.def;
    }

    public static String getDef(ChargeBearer bearer) {
        return bearer.def;
    }

    public static ChargeBearer getBearerType(String def) {
        switch (def) {
            case "OUR": return OUR;
            case "BEN": return BEN;
            case "SHA": return SHA;
            default: throw new IllegalArgumentException();
        }
    }
}
