package com.epam.esm.dao;

import java.sql.ResultSet;
import java.util.List;

public interface ListToSetConverter {
    ResultSet getResultSet(List<String> headers, List<List<Object>> data) throws Exception;
}
