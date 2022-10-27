package com.colak.imdg;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

public class JDBCBatchInsert {

    private static DataSource getDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost/db");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    private static void doBatchInsert() throws SQLException {
        DataSource dataSource = getDataSource();

        String compiledQuery = "INSERT INTO dbmap(ID, name, SSN) VALUES(?, ?, ?)";

        int records = 1_000_000;

        int count = 0;
        int batchSize = 50;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(compiledQuery)) {

            connection.setAutoCommit(false);

            for (int index = 1; index <= records; index++) {
                preparedStatement.setInt(1, index);
                preparedStatement.setString(2, "");
                preparedStatement.setString(3, "");
                preparedStatement.addBatch();

                count++;

                if(count % batchSize == 0){
                    preparedStatement.executeBatch();
                    connection.commit();
                }

            }

        }
    }

    public static void main(String[] args) throws SQLException {

        Instant start = Instant.now();

        doBatchInsert();

        Instant end = Instant.now();

        Duration between = Duration.between(start, end);
        String format = String.format("%d minutes, %d seconds",
                between.toMinutes(),
                between.getSeconds() % 60
        );
        System.out.println("total time taken to insert the batch = " + format);

    }
}
