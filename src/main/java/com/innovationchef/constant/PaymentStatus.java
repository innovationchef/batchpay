package com.innovationchef.constant;

public enum PaymentStatus {
    ACTC("ACCEPTED"), RJCT("REJECTED"), ACSP("SETTLED");

    private String def;

    PaymentStatus(String def) {
        this.def = def;
    }

    public String getDef() {
        return this.def;
    }

    public static String getDef(PaymentStatus status) {
        return status.def;
    }

    public static PaymentStatus getStatus(String status) {
        switch (status) {
            case "ACCEPTED": return ACTC;
            case "REJECTED": return RJCT;
            case "SETTLED": return ACSP;
            default: throw new IllegalArgumentException();
        }
    }
}
