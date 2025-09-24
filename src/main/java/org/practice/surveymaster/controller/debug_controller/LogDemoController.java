package org.practice.surveymaster.controller.debug_controller;

import org.practice.surveymaster.annotation.LogBusiness;
import org.practice.surveymaster.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志使用示例控制器
 * 展示如何使用统一的日志系统
 */
@RestController
@RequestMapping("/log-demo")
public class LogDemoController {

    private static final Logger logger = LoggerFactory.getLogger(LogDemoController.class);

    /**
     * 测试普通日志记录
     */
    @GetMapping("/test")
    public Map<String, Object> testLog(@RequestParam(defaultValue = "test") String name) {
        LogUtil.logDebug(logger, "测试日志记录", new HashMap<String, Object>(){{ put("name", name); }});
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello " + name);
        result.put("timestamp", System.currentTimeMillis());
        
        LogUtil.logBusiness(logger, "测试业务操作", new HashMap<String, Object>(){{ put("name", name); }}, result);
        
        return result;
    }

    /**
     * 测试业务日志注解
     */
    @GetMapping("/business")
    @LogBusiness("用户查询操作")
    public Map<String, Object> testBusinessLog(@RequestParam String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("username", "张三");
        result.put("email", "zhangsan@example.com");
        
        // 这里会自动记录业务日志
        return result;
    }

    /**
     * 测试异常日志记录
     */
    @GetMapping("/exception")
    public Map<String, Object> testException(@RequestParam(defaultValue = "0") int error) {
        try {
            if (error == 1) {
                throw new RuntimeException("测试异常");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            return result;
            
        } catch (Exception e) {
            LogUtil.logException(logger, "异常测试操作", e, new HashMap<String, Object>(){{ put("error", error); }});
            throw e;
        }
    }

    /**
     * 测试性能日志记录
     */
    @GetMapping("/performance")
    public Map<String, Object> testPerformance(@RequestParam(defaultValue = "1000") long sleepTime) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 模拟耗时操作
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("duration", duration);
        result.put("sleepTime", sleepTime);
        
        LogUtil.logPerformance(logger, "性能测试操作", duration, new HashMap<String, Object>(){{ put("sleepTime", sleepTime); }});
        
        return result;
    }

    /**
     * 测试用户操作日志
     */
    @GetMapping("/user-action")
    public Map<String, Object> testUserAction(@RequestParam String userId, @RequestParam String action) {
        Map<String, Object> details = new HashMap<>();
        details.put("action", action);
        details.put("time", System.currentTimeMillis());
        
        LogUtil.logUserAction(logger, userId, action, details);
        LogUtil.logSecurity(logger, userId, action, "127.0.0.1", details);
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("action", action);
        result.put("status", "logged");
        
        return result;
    }
}