package com.colak.imdg;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.datastore.ExternalDataStoreFactory;
import com.hazelcast.datastore.ExternalDataStoreService;
import com.hazelcast.datastore.JdbcDataStoreFactory;
import com.hazelcast.jet.impl.util.Util;
import com.hazelcast.spi.impl.NodeEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PrepareDatabase {

    private static final Logger logger = LoggerFactory.getLogger(PrepareDatabase.class);

    public static void prepareDatabase(HazelcastInstance hazelcastInstance) {


        DataSource dataSource = getDataSource(hazelcastInstance);

        executeUpdate(dataSource, "create table if not exists dbmap (id int not null, name varchar(45), ssn varchar(45), primary key (id))");
        executeUpdate(dataSource, "truncate table dbmap");
    }

    protected static DataSource getDataSource(HazelcastInstance hazelcastInstance) {
        NodeEngineImpl nodeEngine = Util.getNodeEngine(hazelcastInstance);
        ExternalDataStoreService externalDataStoreService = nodeEngine.getExternalDataStoreService();
        ExternalDataStoreFactory<?> dataStoreFactory = externalDataStoreService.getExternalDataStoreFactory("my-mysql-database");
        DataSource dataSource = ((JdbcDataStoreFactory) dataStoreFactory).getDataStore();
        return dataSource;
    }
    /*
    protected static DataSource getDataSource(HazelcastInstance hazelcastInstance) {
        Config config = hazelcastInstance.getConfig();
        ExternalDataStoreConfig externalDataStoreConfig = config.getExternalDataStoreConfig("my-mysql-database");
        String jdbcUrl = externalDataStoreConfig.getProperty("jdbcUrl");
        String username = externalDataStoreConfig.getProperty("username");
        String password = externalDataStoreConfig.getProperty("password");

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(user);
        dataSource.setPassword(password);
        return dataSource;
    }*/

    private static void executeUpdate(DataSource dataSource, String query) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();) {

            int result = statement.executeUpdate(query);
            logger.info("executeUpdate result : {}", result);

        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
