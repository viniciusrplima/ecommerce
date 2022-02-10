package com.pacheco.app.ecommerce.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataSource dataSource;

    private Connection connection;

    public void clearTablesAndResetSequences() {
        try (Connection connection = dataSource.getConnection()) {
            this.connection = connection;

            checkTestDatabase();
            tryToClearTables();
            tryToResetSequences();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            this.connection = null;
        }
    }

    private void checkTestDatabase() throws SQLException {
        String catalog = connection.getCatalog();

        if (catalog == null || !catalog.endsWith("test")) {
            throw new RuntimeException(
                    "Cannot clear database tables because '" + catalog
                    + "' is not a test database (suffix 'test' not found).");
        }
    }

    private void tryToClearTables() throws SQLException {
        List<String> tableNames = getTableNames();
        clear(tableNames);
    }

    private void tryToResetSequences() throws SQLException {
        List<String> sequenceNames = getSequenceNames();
        reset(sequenceNames);
    }

    private List<String> getTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();

        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet rs = metaData.getTables(connection.getCatalog(), null, null, new String[] { "TABLE" });

        while (rs.next()) {
            tableNames.add(rs.getString("TABLE_NAME"));
        }

        tableNames.remove("flyway_schema_history");

        return tableNames;
    }

    private List<String> getSequenceNames() throws SQLException {
        List<String> sequenceNames = new ArrayList<>();

        String sql = "SELECT sequencename FROM pg_sequences;";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            sequenceNames.add(rs.getString("sequencename"));
        }

        return sequenceNames;
    }

    private void clear(List<String> tableNames) throws SQLException {
        Statement statement = buildSqlClearTables(tableNames);

        logger.debug("Executing SQL");
        statement.executeBatch();
    }


    private void reset(List<String> sequenceNames) throws SQLException {
        Statement statement = buildSqlResetIndexes(sequenceNames);

        logger.debug("Executing SQL");
        statement.executeBatch();
    }

    private Statement buildSqlResetIndexes(List<String> sequenceNames) throws SQLException {
        Statement statement = connection.createStatement();
        addRestartStatements(sequenceNames, statement);

        return statement;
    }

    private void addRestartStatements(List<String> sequenceNames, Statement statement) {
        sequenceNames.forEach(sequenceName -> {
            try {
                statement.addBatch(sql("ALTER SEQUENCE " + sequenceName + " RESTART"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Statement buildSqlClearTables(List<String> tableNames) throws SQLException {
        Statement statement = connection.createStatement();

        statement.addBatch(sql("SET session_replication_role = replica"));
        addTruncateSatements(tableNames, statement);
        statement.addBatch(sql("SET session_replication_role = DEFAULT"));

        return statement;
    }

    private void addTruncateSatements(List<String> tableNames, Statement statement) {
        tableNames.forEach(tableName -> {
            try {
                statement.addBatch(sql("TRUNCATE TABLE " + tableName + " CASCADE"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String sql(String sql) {
        logger.debug("Adding SQL: {}", sql);
        return sql;
    }

}