package org.practice.surveymaster.util;

import org.practice.surveymaster.constant.SensitiveType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * <p>
 * 数据脱敏工具类
 * </p>
 *
 * <p>
 * 提供各种常见数据类型的脱敏处理方法，包括手机号、身份证、邮箱、银行卡等。
 * 支持自定义脱敏规则，可灵活配置保留位数和替换字符。
 * 所有脱敏方法都会进行参数校验，确保处理的安全性和稳定性。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午2:15
 */
@Component
public class SensitiveUtil {

    /** 默认替换字符 */
    private static final String DEFAULT_MASK_CHAR = "*";
    
    /** 手机号正则表达式 */
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    /** 身份证正则表达式 */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");
    
    /** 邮箱正则表达式 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    /** 银行卡正则表达式 */
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("^[1-9]\\d{12,19}$");
    
    /** 车牌号正则表达式 */
    private static final Pattern CAR_LICENSE_PATTERN = Pattern.compile("^[一-龥]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");
    
    /**
     * 重复字符串辅助方法（用于兼容 Java 8）
     */
    private static String repeatChar(String str, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * 根据脱敏类型对数据进行脱敏处理
     *
     * @param data 原始数据
     * @param type 脱敏类型
     * @return 脱敏后的数据
     */
    public static String desensitize(String data, SensitiveType type) {
        if (!StringUtils.hasText(data) || type == null) {
            return data;
        }
        
        switch (type) {
            case MOBILE:
                return desensitizeMobile(data);
            case NAME:
                return desensitizeName(data);
            case EMAIL:
                return desensitizeEmail(data);
            case PASSWORD:
                return desensitizePassword(data);
            case ID_CARD:
                return desensitizeIdCard(data);
            case BANK_CARD:
                return desensitizeBankCard(data);
            case ADDRESS:
                return desensitizeAddress(data);
            case CAR_LICENSE:
                return desensitizeCarLicense(data);
            case FIXED_PHONE:
                return desensitizeFixedPhone(data);
            case CUSTOM:
            default:
                return desensitizeDefault(data);
        }
    }
    
    /**
     * 手机号脱敏处理
     * 保留前3位和后4位，中间用*替换
     * 示例：13812345678 -> 138****5678
     */
    public static String desensitizeMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return mobile;
        }
        
        // 校验手机号格式
        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            return mobile; // 格式不正确，直接返回
        }
        
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }
    
    /**
     * 姓名脱敏处理
     * 保留姓氏，名字用*替换
     * 示例：张三 -> 张*；欧阳修 -> 欧阳*
     */
    public static String desensitizeName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        
        int length = name.length();
        if (length <= 1) {
            return name;
        }
        
        // 中文姓名处理
        if (length == 2) {
            return name.charAt(0) + "*";
        } else if (length == 3) {
            return name.substring(0, 2) + "*";
        } else {
            // 复姓或较长姓名，保留前面部分
            return name.substring(0, 2) + repeatChar("*", length - 2);
        }
    }
    
    /**
     * 邮箱脱敏处理
     * 保留前3位和@后的域名，用户名部分用*替换
     * 示例：zhangsan@example.com -> zha***@example.com
     */
    public static String desensitizeEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return email;
        }
        
        // 校验邮箱格式
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 3) {
            return email; // 用户名太短，不脱敏
        }
        
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        String maskedUsername = username.substring(0, 3) + repeatChar("*", username.length() - 3);
        return maskedUsername + domain;
    }
    
    /**
     * 密码脱敏处理
     * 完全用*替换
     */
    public static String desensitizePassword(String password) {
        if (!StringUtils.hasText(password)) {
            return password;
        }
        return repeatChar("*", Math.min(password.length(), 8)); // 最多显示8个*
    }
    
    /**
     * 身份证号脱敏处理
     * 保留前4位和后4位，中间用*替换
     * 示例：110101199001011234 -> 1101***********1234
     */
    public static String desensitizeIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return idCard;
        }
        
        // 校验身份证格式
        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return idCard;
        }
        
        return idCard.substring(0, 4) + repeatChar("*", idCard.length() - 8) + idCard.substring(idCard.length() - 4);
    }
    
    /**
     * 银行卡号脱敏处理
     * 保留前4位和后4位，中间用*替换
     * 示例：6217000123456789012 -> 6217************9012
     */
    public static String desensitizeBankCard(String bankCard) {
        if (!StringUtils.hasText(bankCard)) {
            return bankCard;
        }
        
        // 校验银行卡格式
        if (!BANK_CARD_PATTERN.matcher(bankCard).matches()) {
            return bankCard;
        }
        
        if (bankCard.length() <= 8) {
            return bankCard; // 太短，不脱敏
        }
        
        return bankCard.substring(0, 4) + repeatChar("*", bankCard.length() - 8) + bankCard.substring(bankCard.length() - 4);
    }
    
    /**
     * 地址脱敏处理
     * 保留省市信息，详细地址用*替换
     * 示例：北京市朝阳区三里屯街道1号 -> 北京市朝阳区****
     */
    public static String desensitizeAddress(String address) {
        if (!StringUtils.hasText(address)) {
            return address;
        }
        
        // 简单的地址脱敏逻辑，保留前面的省市区信息
        int length = address.length();
        if (length <= 6) {
            return address;
        }
        
        // 查找省市区分隔符（市、区、县等）
        int keepLength = Math.min(6, length / 2);
        for (int i = 3; i < Math.min(8, length); i++) {
            char c = address.charAt(i);
            if (c == '市' || c == '区' || c == '县') {
                keepLength = i + 1;
                break;
            }
        }
        
        return address.substring(0, keepLength) + repeatChar("*", length - keepLength);
    }
    
    /**
     * 车牌号脱敏处理
     * 保留前2位和后1位，中间用*替换
     * 示例：京A12345 -> 京A***5
     */
    public static String desensitizeCarLicense(String carLicense) {
        if (!StringUtils.hasText(carLicense)) {
            return carLicense;
        }
        
        // 校验车牌号格式
        if (!CAR_LICENSE_PATTERN.matcher(carLicense).matches()) {
            return carLicense;
        }
        
        if (carLicense.length() != 7) {
            return carLicense;
        }
        
        return carLicense.substring(0, 2) + "***" + carLicense.substring(6);
    }
    
    /**
     * 固定电话脱敏处理
     * 保留区号和后4位，中间用*替换
     * 示例：010-12345678 -> 010-****5678
     */
    public static String desensitizeFixedPhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return phone;
        }
        
        // 处理带横线的格式
        if (phone.contains("-")) {
            String[] parts = phone.split("-");
            if (parts.length == 2 && parts[1].length() >= 4) {
                String number = parts[1];
                String maskedNumber = repeatChar("*", number.length() - 4) + number.substring(number.length() - 4);
                return parts[0] + "-" + maskedNumber;
            }
        }
        
        // 处理不带横线的格式
        if (phone.length() >= 7) {
            return phone.substring(0, 3) + repeatChar("*", phone.length() - 7) + phone.substring(phone.length() - 4);
        }
        
        return phone;
    }
    
    /**
     * 默认脱敏处理
     * 保留前后各两位，中间用*替换
     */
    public static String desensitizeDefault(String data) {
        if (!StringUtils.hasText(data)) {
            return data;
        }
        
        int length = data.length();
        if (length <= 4) {
            return data;
        }
        
        return data.substring(0, 2) + repeatChar("*", length - 4) + data.substring(length - 2);
    }
    
    /**
     * 自定义脱敏处理
     *
     * @param data 原始数据
     * @param keepStart 保留开始位数
     * @param keepEnd 保留结束位数
     * @param maskChar 替换字符
     * @return 脱敏后的数据
     */
    public static String desensitizeCustom(String data, int keepStart, int keepEnd, String maskChar) {
        if (!StringUtils.hasText(data)) {
            return data;
        }
        
        if (keepStart < 0) keepStart = 0;
        if (keepEnd < 0) keepEnd = 0;
        if (!StringUtils.hasText(maskChar)) maskChar = DEFAULT_MASK_CHAR;
        
        int length = data.length();
        if (keepStart + keepEnd >= length) {
            return data; // 保留位数超过总长度
        }
        
        String start = data.substring(0, keepStart);
        String end = data.substring(length - keepEnd);
        String middle = repeatChar(maskChar, length - keepStart - keepEnd);
        
        return start + middle + end;
    }
    
    /**
     * 批量脱敏处理
     *
     * @param dataArray 数据数组
     * @param type 脱敏类型
     * @return 脱敏后的数据数组
     */
    public static String[] desensitizeBatch(String[] dataArray, SensitiveType type) {
        if (dataArray == null || dataArray.length == 0) {
            return dataArray;
        }
        
        String[] result = new String[dataArray.length];
        for (int i = 0; i < dataArray.length; i++) {
            result[i] = desensitize(dataArray[i], type);
        }
        
        return result;
    }
}
