package org.practice.surveymaster.controller.debug_controller;

import org.practice.surveymaster.annotation.Sensitive;
import org.practice.surveymaster.constant.SensitiveType;
import org.practice.surveymaster.model.User;
import org.practice.surveymaster.vo.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 数据脱敏演示控制器
 * </p>
 *
 * <p>
 * 用于演示和测试数据脱敏功能的控制器。
 * 提供多个接口展示不同场景下的数据脱敏效果，包括单个对象、列表对象以及复杂嵌套对象的脱敏处理。
 * 可以通过访问这些接口直观地看到脱敏功能的实际效果。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午5:10
 */
@RestController
@RequestMapping("/api/demo/sensitive")
public class SensitiveDemoController {

    /**
     * 演示用户信息脱敏
     */
    @GetMapping("/user")
    public ApiResponse<User> getUserInfo() {
        User user = new User();
        user.setId(1L);
        user.setUsername("张三丰");
        user.setPassword("mySecretPassword123");
        user.setEmail("zhangsan@example.com");
        user.setCreatedAt(LocalDateTime.now());
        
        return ApiResponse.success(user);
    }

    /**
     * 演示用户列表脱敏
     */
    @GetMapping("/users")
    public ApiResponse<List<User>> getUserList() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("李四");
        user1.setPassword("password123");
        user1.setEmail("lisi@test.com");
        user1.setCreatedAt(LocalDateTime.now().minusDays(1));

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("王五六");
        user2.setPassword("abc123456");
        user2.setEmail("wangwuliu@company.org");
        user2.setCreatedAt(LocalDateTime.now().minusDays(2));

        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("欧阳修");
        user3.setPassword("strongPassword");
        user3.setEmail("ouyangxiu@poetry.cn");
        user3.setCreatedAt(LocalDateTime.now().minusDays(3));

        List<User> users = Arrays.asList(user1, user2, user3);
        return ApiResponse.success(users);
    }

    /**
     * 演示敏感信息对象
     */
    @GetMapping("/sensitive-info")
    public ApiResponse<SensitiveInfoDemo> getSensitiveInfo() {
        SensitiveInfoDemo info = new SensitiveInfoDemo();
        info.setMobile("13812345678");
        info.setIdCard("110101199001011234");
        info.setBankCard("6217000123456789012");
        info.setAddress("北京市朝阳区三里屯街道1号");
        info.setCarLicense("京A12345");
        info.setFixedPhone("010-12345678");
        
        return ApiResponse.success(info);
    }

    /**
     * 演示敏感信息类
     */
    public static class SensitiveInfoDemo {
        @Sensitive(SensitiveType.MOBILE)
        private String mobile;
        
        @Sensitive(SensitiveType.ID_CARD)
        private String idCard;
        
        @Sensitive(SensitiveType.BANK_CARD)
        private String bankCard;
        
        @Sensitive(SensitiveType.ADDRESS)
        private String address;
        
        @Sensitive(SensitiveType.CAR_LICENSE)
        private String carLicense;
        
        @Sensitive(SensitiveType.FIXED_PHONE)
        private String fixedPhone;
        
        @Sensitive(value = SensitiveType.CUSTOM, keepStart = 3, keepEnd = 3, maskChar = "#")
        private String customField = "ThisIsACustomSensitiveField";

        // Getters and Setters
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        
        public String getIdCard() { return idCard; }
        public void setIdCard(String idCard) { this.idCard = idCard; }
        
        public String getBankCard() { return bankCard; }
        public void setBankCard(String bankCard) { this.bankCard = bankCard; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getCarLicense() { return carLicense; }
        public void setCarLicense(String carLicense) { this.carLicense = carLicense; }
        
        public String getFixedPhone() { return fixedPhone; }
        public void setFixedPhone(String fixedPhone) { this.fixedPhone = fixedPhone; }
        
        public String getCustomField() { return customField; }
        public void setCustomField(String customField) { this.customField = customField; }
    }
}