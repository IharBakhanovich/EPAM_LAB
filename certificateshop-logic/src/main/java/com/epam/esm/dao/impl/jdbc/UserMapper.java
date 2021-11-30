package com.epam.esm.dao.impl.jdbc;

import com.epam.esm.model.impl.Order;
import com.epam.esm.model.impl.Role;
import com.epam.esm.model.impl.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Maps values from {@link ResultSet} to {@link User}.
 */
@Component
public class UserMapper implements RowMapper<User> {
    /**
     * Maps the row from {@link ResultSet} to the {@link User}.
     *
     * @param resultSet is the {@link ResultSet} to map from.
     * @param i         is the row to map.
     * @return {@link User}.
     * @throws SQLException if something went wrong.
     */
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        return new User(
                resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ID),
                resultSet.getString(ColumnNames.TABLE_USER_COLUMN_NICKNAME),
                resultSet.getString(ColumnNames.TABLE_USER_COLUMN_PASSWORD),
                Role.resolveRoleById(resultSet.getLong(ColumnNames.TABLE_USER_COLUMN_ROLE))
        );
    }
}
