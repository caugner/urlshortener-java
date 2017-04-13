package com.github.caugner.urlshortener.persistence.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Common implementation for {@link JdbcStore}s.
 */
public abstract class AbstractJdbcStore implements AutoCloseable, JdbcStore {

  private static final String TABLE = "store";
  private static final String COLUMN_KEY = "key";
  private static final String COLUMN_VALUE = "value";

  private static final String TEMPLATE_CREATE = "CREATE TABLE IF NOT EXISTS %1$s "
      + "(%2$s string, %3$s string, PRIMARY KEY (%2$s))";
  private static final String TEMPLATE_SELECT = "SELECT %3$s FROM %1$s WHERE %2$s = ?";
  private static final String TEMPLATE_INSERT = "INSERT INTO %1$s (%2$s, %3$s) VALUES (?, ?)";
  private static final String TEMPLATE_COUNT = "SELECT COUNT(*) FROM %1$s";

  private final Connection connection;
  private final PreparedStatement selectStmt;
  private final PreparedStatement insertStmt;
  private final PreparedStatement countStmt;

  public AbstractJdbcStore(String className, String connectionUrl) throws SQLException {
    // Verify driver availability.
    try {
      Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("JDBC driver " + className + " is not available", e);
    }

    // Connect database.
    connection = DriverManager.getConnection(connectionUrl);

    // Create table if it does not exist.
    Statement statement = connection.createStatement();
    statement.executeUpdate(applyQueryTemplate(TEMPLATE_CREATE));

    // Prepare statements.
    selectStmt = connection.prepareStatement(applyQueryTemplate(TEMPLATE_SELECT));
    insertStmt = connection.prepareStatement(applyQueryTemplate(TEMPLATE_INSERT));
    countStmt = connection.prepareStatement(applyQueryTemplate(TEMPLATE_COUNT));
  }

  private String applyQueryTemplate(String template) throws SQLException {
    return String.format(template, TABLE, COLUMN_KEY, COLUMN_VALUE);
  }

  @Override
  public boolean has(String key) throws SQLException {
    return get(key) != null;
  }

  @Override
  public String get(String key) throws SQLException {
    selectStmt.setString(1, key);
    ResultSet result = selectStmt.executeQuery();

    final String returnValue;
    if (result.isClosed()) {
      returnValue = null;
    } else {
      returnValue = result.getString(1);
    }
    return returnValue;
  }

  @Override
  public boolean put(String key, String value) throws SQLException {
    insertStmt.setString(1, key);
    insertStmt.setString(2, value);
    int affectedRows = insertStmt.executeUpdate();
    return affectedRows > 0;
  }

  @Override
  public long size() throws SQLException {
    ResultSet result = countStmt.executeQuery();
    return result.getLong(1);
  }

  @Override
  public void close() throws SQLException {
    connection.close();
  }
}