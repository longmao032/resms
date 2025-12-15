package com.guang.resms.shared.aspect;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.guang.resms.common.exception.ResponseResult;
import com.guang.resms.common.utils.SecurityUtils;
import com.guang.resms.module.notice.entity.WorkNotice;
import com.guang.resms.module.notice.service.WorkNoticeService;
import com.guang.resms.shared.annotation.OpLog;
import com.guang.resms.shared.entity.OperationLog;
import com.guang.resms.shared.enums.RiskDimension;
import com.guang.resms.shared.enums.RiskLevel;
import com.guang.resms.shared.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final WorkNoticeService workNoticeService;
    private final ObjectMapper objectMapper;

    public OperationLogAspect(OperationLogService operationLogService,
            WorkNoticeService workNoticeService,
            ObjectMapper objectMapper) {
        this.operationLogService = operationLogService;
        this.workNoticeService = workNoticeService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.guang.resms.shared.annotation.OpLog) || @within(com.guang.resms.shared.annotation.OpLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OpLog opLog = method.getAnnotation(OpLog.class);
        if (opLog == null) {
            opLog = joinPoint.getTarget().getClass().getAnnotation(OpLog.class);
        }
        if (opLog == null) {
            return joinPoint.proceed();
        }

        OperationLog log = new OperationLog();
        log.setModule(opLog.module());
        log.setOperationType(opLog.operationType());
        log.setOperationDesc(StringUtils.hasText(opLog.operationDesc()) ? opLog.operationDesc() : opLog.operationType());
        log.setRiskLevel(opLog.level().getCode());
        log.setRiskDimension(buildRiskDimension(opLog.dimensions()));
        log.setUserId(SecurityUtils.getUserId());
        log.setUserRealName(SecurityUtils.getRealName());

        HttpServletRequest request = getRequest();
        if (request != null) {
            log.setIpAddress(getClientIp(request));
            log.setRequestUrl(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
        }

        log.setRequestParams(safeJson(buildArgsNode(joinPoint.getArgs())));
        log.setOperationTime(LocalDateTime.now());

        Object result = null;
        try {
            result = joinPoint.proceed();
            log.setExecuteTime(System.currentTimeMillis() - start);

            log.setStatus(isSuccess(result) ? 1 : 0);
            log.setResponseResult(safeJson(toSafeNode(result)));

            operationLogService.saveLog(log);
            if (opLog.level() == RiskLevel.HIGH && opLog.notifyAdmin()) {
                notifyAdminHighRisk(log);
            }
            return result;
        } catch (Throwable ex) {
            log.setExecuteTime(System.currentTimeMillis() - start);
            log.setStatus(0);
            log.setErrorMessage(truncate(ex.getMessage(), 2000));
            operationLogService.saveLog(log);
            if (opLog.level() == RiskLevel.HIGH && opLog.notifyAdmin()) {
                notifyAdminHighRisk(log);
            }
            throw ex;
        }
    }

    private String buildRiskDimension(RiskDimension[] dimensions) {
        if (dimensions == null || dimensions.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimensions.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(dimensions[i].name());
        }
        return sb.toString();
    }

    private boolean isSuccess(Object result) {
        if (result instanceof ResponseResult<?> rr) {
            return Boolean.TRUE.equals(rr.getStatus()) && rr.getCode() != null && rr.getCode() == 200;
        }
        return true;
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return null;
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            String[] parts = xff.split(",");
            return parts[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private JsonNode buildArgsNode(Object[] args) {
        ArrayNode arr = objectMapper.createArrayNode();
        if (args == null) {
            return arr;
        }
        for (Object arg : args) {
            if (arg == null) {
                arr.addNull();
                continue;
            }
            String name = arg.getClass().getName();
            if (name.startsWith("jakarta.servlet.") || name.startsWith("org.springframework.web.")) {
                continue;
            }
            if (name.startsWith("org.springframework.validation.")) {
                continue;
            }
            if (name.startsWith("org.springframework.web.multipart.")) {
                continue;
            }
            try {
                arr.add(objectMapper.valueToTree(arg));
            } catch (Exception e) {
                arr.add(String.valueOf(arg));
            }
        }
        return arr;
    }

    private JsonNode toSafeNode(Object obj) {
        if (obj == null) {
            return objectMapper.nullNode();
        }
        try {
            return objectMapper.valueToTree(obj);
        } catch (Exception e) {
            return objectMapper.getNodeFactory().textNode(String.valueOf(obj));
        }
    }

    private String safeJson(JsonNode node) {
        if (node == null) {
            return null;
        }
        try {
            JsonNode masked = maskSensitive(node.deepCopy());
            String s = objectMapper.writeValueAsString(masked);
            return truncate(s, 6000);
        } catch (Exception e) {
            return null;
        }
    }

    private JsonNode maskSensitive(JsonNode node) {
        if (node == null) return null;
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            obj.fieldNames().forEachRemaining(fn -> {
                JsonNode child = obj.get(fn);
                String lower = fn.toLowerCase();
                if (lower.contains("password") || lower.contains("token")) {
                    obj.put(fn, "******");
                } else {
                    obj.set(fn, maskSensitive(child));
                }
            });
            return obj;
        }
        if (node.isArray()) {
            ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                arr.set(i, maskSensitive(arr.get(i)));
            }
            return arr;
        }
        return node;
    }

    private void notifyAdminHighRisk(OperationLog log) {
        try {
            WorkNotice notice = new WorkNotice();
            notice.setNoticeTitle("高危操作告警");
            String actor = StringUtils.hasText(log.getUserRealName()) ? log.getUserRealName() : String.valueOf(log.getUserId());
            String content = String.format("%s 执行了高危操作【%s-%s】：%s", actor, log.getModule(), log.getOperationType(), log.getOperationDesc());
            notice.setNoticeContent(content);
            notice.setNoticeType(1);
            notice.setPriority(1);
            notice.setBizType("op_log_high_risk");
            notice.setBizId(log.getId());
            notice.setRouterPath("/operation-log");
            notice.setReceiverType(2);
            notice.setReceiverIds("[1]");
            notice.setSenderId(0);
            notice.setSenderName("系统");
            workNoticeService.publishNotice(notice);
        } catch (Exception ignored) {
        }
    }

    private String truncate(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max);
    }
}
