package com.epam.esm.dao.impl.jpa;

import com.epam.esm.dao.ListToResultSetConverter;
import com.mockrunner.mock.jdbc.MockResultSet;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

/**
 * @author edw
 */
@Component
public class ResultListToResultSetConverter implements ListToResultSetConverter {

    public ResultSet convertToResultSet(List<String> headers, List<List<Object>> data) throws Exception {
        // validation
        if (headers == null || data == null) {
            throw new Exception("null parameters");
        }
        for (List<Object> list : data) {
            if (headers.size() != list.size()) {
                throw new Exception("parameters size are not equals");
            }
        }
        // create a mock result set
        MockResultSet mockResultSet = new MockResultSet("myResultSet");
        // add header to resultSet
        for (String string : headers) {
            mockResultSet.addColumn(string);
        }
        // add data to resultSet
        for (List<Object> list : data) {
            mockResultSet.addRow(list);
        }
        return mockResultSet;
    }
}

