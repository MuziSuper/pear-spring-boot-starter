package cn.muzisheng.pear.handler;

import cn.muzisheng.pear.model.RoleEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleEnumTypeHandler extends BaseTypeHandler<RoleEnum> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RoleEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public RoleEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return RoleEnum.codeOf(rs.getInt(columnName));
    }

    @Override
    public RoleEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return RoleEnum.codeOf(rs.getInt(columnIndex));
    }

    @Override
    public RoleEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return RoleEnum.codeOf(cs.getInt(columnIndex));

    }
}
