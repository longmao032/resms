package com.guang.resms.shared.enums;

public enum RiskDimension {
    FINANCIAL("资金安全"),
    DATA("数据资产安全"),
    SYSTEM("系统权限安全");

    private final String label;

    RiskDimension(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
