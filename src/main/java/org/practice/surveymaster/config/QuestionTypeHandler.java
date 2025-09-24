package org.practice.surveymaster.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.practice.surveymaster.constant.QuestionType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 问题类型枚举的MyBatis类型处理器
 * 用于在数据库存储和读取时自动转换QuestionType枚举
 *
 * @author ljn
 * @since 2025/9/24
 */
public class QuestionTypeHandler extends BaseTypeHandler<QuestionType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, QuestionType parameter, JdbcType jdbcType) throws SQLException {
        // 存储到数据库时使用枚举的code值
        ps.setString(i, parameter.getCode());
    }

    @Override
    public QuestionType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return code == null ? null : QuestionType.fromCode(code);
    }

    @Override
    public QuestionType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return code == null ? null : QuestionType.fromCode(code);
    }

    @Override
    public QuestionType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return code == null ? null : QuestionType.fromCode(code);
    }
}