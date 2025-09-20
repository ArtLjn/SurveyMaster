package org.practice.surveymaster.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * [在此处用一两句话描述该类的核心职责和目的。]
 * </p>
 *
 * <p>
 * [可以在这里补充更详细的说明，例如设计思路、关键算法、使用示例或注意事项。]
 * </p>
 *
 * @author ljn
 * @since 2025/9/16 下午4:01
 */

@Data
public class UserRegister {
    @NotBlank(message = "用户名不能为空")
    public String username;
    @NotBlank(message = "密码不能为空")
    public String password;
    @NotBlank(message = "邮箱不能为空")
    public String email;
}
