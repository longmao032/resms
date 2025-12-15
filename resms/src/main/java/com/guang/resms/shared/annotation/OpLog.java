package com.guang.resms.shared.annotation;

import com.guang.resms.shared.enums.RiskDimension;
import com.guang.resms.shared.enums.RiskLevel;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {

    String module();

    String operationType();

    String operationDesc() default "";

    RiskLevel level() default RiskLevel.NORMAL;

    RiskDimension[] dimensions() default {};

    boolean notifyAdmin() default true;
}
