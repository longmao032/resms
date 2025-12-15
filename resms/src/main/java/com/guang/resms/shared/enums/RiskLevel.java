package com.guang.resms.shared.enums;

public enum RiskLevel {
    HIGH(1, "高危"),
    MEDIUM(2, "中等风险"),
    NORMAL(3, "普通");

    private final int code;
    private final String label;

    RiskLevel(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
