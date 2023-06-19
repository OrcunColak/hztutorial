package com.colak.jet.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultFunction {

    public static Worker mapOutput(ResultSet resultSet) throws SQLException {
        return new Worker(resultSet.getInt("id"), resultSet.getString("name"));
    }

}
