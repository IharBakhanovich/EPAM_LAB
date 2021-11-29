package com.epam.esm.dao;

import java.sql.ResultSet;
import java.util.List;

public interface ListToResultSetConverter {
    ResultSet convertToResultSet(List<String> headers, List<List<Object>> data) throws Exception;
}
