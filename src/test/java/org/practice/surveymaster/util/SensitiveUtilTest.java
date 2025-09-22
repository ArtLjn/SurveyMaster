package org.practice.surveymaster.util;

import org.junit.jupiter.api.Test;
import org.practice.surveymaster.constant.SensitiveType;
import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 数据脱敏工具类测试
 * </p>
 *
 * <p>
 * 全面测试 SensitiveUtil 工具类的各种脱敏功能，包括手机号、身份证、邮箱、银行卡等各类敏感数据的脱敏处理。
 * 验证脱敏算法的正确性、边界情况处理以及异常情况的处理能力。
 * 确保脱敏功能在各种输入情况下都能正常工作，保障数据安全性。
 * </p>
 *
 * @author ljn
 * @since 2025/9/22 下午5:00
 */
public class SensitiveUtilTest {

    @Test
    public void testDesensitizeMobile() {
        // 正常手机号脱敏
        assertEquals("138****5678", SensitiveUtil.desensitizeMobile("13812345678"));
        assertEquals("159****9999", SensitiveUtil.desensitizeMobile("15999999999"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeMobile(null));
        assertEquals("", SensitiveUtil.desensitizeMobile(""));
        assertEquals("12345", SensitiveUtil.desensitizeMobile("12345")); // 格式不正确，不脱敏
    }

    @Test
    public void testDesensitizeName() {
        // 中文姓名脱敏
        assertEquals("张*", SensitiveUtil.desensitizeName("张三"));
        assertEquals("李四*", SensitiveUtil.desensitizeName("李四五"));
        assertEquals("欧阳*", SensitiveUtil.desensitizeName("欧阳修"));
        assertEquals("司马**", SensitiveUtil.desensitizeName("司马相如"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeName(null));
        assertEquals("", SensitiveUtil.desensitizeName(""));
        assertEquals("王", SensitiveUtil.desensitizeName("王")); // 单字不脱敏
    }

    @Test
    public void testDesensitizeEmail() {
        // 正常邮箱脱敏
        assertEquals("zha***@example.com", SensitiveUtil.desensitizeEmail("zhangsan@example.com"));
        assertEquals("adm***@company.org", SensitiveUtil.desensitizeEmail("admin123@company.org"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeEmail(null));
        assertEquals("", SensitiveUtil.desensitizeEmail(""));
        assertEquals("abc@test.com", SensitiveUtil.desensitizeEmail("abc@test.com")); // 用户名太短，不脱敏
        assertEquals("invalid-email", SensitiveUtil.desensitizeEmail("invalid-email")); // 格式不正确，不脱敏
    }

    @Test
    public void testDesensitizePassword() {
        // 密码完全脱敏
        assertEquals("********", SensitiveUtil.desensitizePassword("password"));
        assertEquals("******", SensitiveUtil.desensitizePassword("123456"));
        assertEquals("********", SensitiveUtil.desensitizePassword("verylongpassword")); // 超过8位也显示8个*
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizePassword(null));
        assertEquals("", SensitiveUtil.desensitizePassword(""));
    }

    @Test
    public void testDesensitizeIdCard() {
        // 身份证号脱敏
        assertEquals("1101***********1234", SensitiveUtil.desensitizeIdCard("110101199001011234"));
        assertEquals("4403***********5678", SensitiveUtil.desensitizeIdCard("440301198012125678"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeIdCard(null));
        assertEquals("", SensitiveUtil.desensitizeIdCard(""));
        assertEquals("123456", SensitiveUtil.desensitizeIdCard("123456")); // 格式不正确，不脱敏
    }

    @Test
    public void testDesensitizeBankCard() {
        // 银行卡号脱敏
        assertEquals("6217************9012", SensitiveUtil.desensitizeBankCard("6217000123456789012"));
        assertEquals("6225***********1234", SensitiveUtil.desensitizeBankCard("622500012341234"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeBankCard(null));
        assertEquals("", SensitiveUtil.desensitizeBankCard(""));
        assertEquals("12345678", SensitiveUtil.desensitizeBankCard("12345678")); // 太短，不脱敏
    }

    @Test
    public void testDesensitizeAddress() {
        // 地址脱敏
        assertEquals("北京市朝阳区****", SensitiveUtil.desensitizeAddress("北京市朝阳区三里屯街道1号"));
        assertEquals("广东省深圳市****", SensitiveUtil.desensitizeAddress("广东省深圳市南山区科技园"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeAddress(null));
        assertEquals("", SensitiveUtil.desensitizeAddress(""));
        assertEquals("短地址", SensitiveUtil.desensitizeAddress("短地址")); // 太短，不脱敏
    }

    @Test
    public void testDesensitizeCarLicense() {
        // 车牌号脱敏
        assertEquals("京A***5", SensitiveUtil.desensitizeCarLicense("京A12345"));
        assertEquals("粤B***0", SensitiveUtil.desensitizeCarLicense("粤B98760"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeCarLicense(null));
        assertEquals("", SensitiveUtil.desensitizeCarLicense(""));
        assertEquals("ABC123", SensitiveUtil.desensitizeCarLicense("ABC123")); // 格式不正确，不脱敏
    }

    @Test
    public void testDesensitizeFixedPhone() {
        // 固定电话脱敏
        assertEquals("010-****5678", SensitiveUtil.desensitizeFixedPhone("010-12345678"));
        assertEquals("0755-***1234", SensitiveUtil.desensitizeFixedPhone("0755-8881234"));
        assertEquals("020****5678", SensitiveUtil.desensitizeFixedPhone("02012345678")); // 不带横线格式
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeFixedPhone(null));
        assertEquals("", SensitiveUtil.desensitizeFixedPhone(""));
    }

    @Test
    public void testDesensitizeCustom() {
        // 自定义脱敏
        assertEquals("12***89", SensitiveUtil.desensitizeCustom("1234589", 2, 2, "*"));
        assertEquals("A###Z", SensitiveUtil.desensitizeCustom("ABCDEFZ", 1, 1, "#"));
        assertEquals("Hello***!", SensitiveUtil.desensitizeCustom("HelloWorld!", 5, 1, "*"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeCustom(null, 1, 1, "*"));
        assertEquals("", SensitiveUtil.desensitizeCustom("", 1, 1, "*"));
        assertEquals("ABC", SensitiveUtil.desensitizeCustom("ABC", 2, 2, "*")); // 保留位数超过总长度，不脱敏
    }

    @Test
    public void testDesensitizeWithType() {
        // 测试通过类型进行脱敏
        assertEquals("138****5678", SensitiveUtil.desensitize("13812345678", SensitiveType.MOBILE));
        assertEquals("张*", SensitiveUtil.desensitize("张三", SensitiveType.NAME));
        assertEquals("zha***@example.com", SensitiveUtil.desensitize("zhangsan@example.com", SensitiveType.EMAIL));
        assertEquals("********", SensitiveUtil.desensitize("password", SensitiveType.PASSWORD));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitize(null, SensitiveType.MOBILE));
        assertEquals("test", SensitiveUtil.desensitize("test", null));
    }

    @Test
    public void testDesensitizeBatch() {
        String[] mobileArray = {"13812345678", "15999999999", "18888888888"};
        String[] expected = {"138****5678", "159****9999", "188****8888"};
        
        String[] result = SensitiveUtil.desensitizeBatch(mobileArray, SensitiveType.MOBILE);
        assertArrayEquals(expected, result);
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeBatch(null, SensitiveType.MOBILE));
        assertArrayEquals(new String[0], SensitiveUtil.desensitizeBatch(new String[0], SensitiveType.MOBILE));
    }

    @Test
    public void testDesensitizeDefault() {
        // 默认脱敏逻辑测试
        assertEquals("12***89", SensitiveUtil.desensitizeDefault("1234589"));
        assertEquals("ab***yz", SensitiveUtil.desensitizeDefault("abcdefghijklmnopqrstuvwxyz"));
        
        // 边界情况
        assertNull(SensitiveUtil.desensitizeDefault(null));
        assertEquals("", SensitiveUtil.desensitizeDefault(""));
        assertEquals("ABC", SensitiveUtil.desensitizeDefault("ABC")); // 长度不足，不脱敏
    }
}