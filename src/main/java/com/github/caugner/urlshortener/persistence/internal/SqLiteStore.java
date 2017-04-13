package com.github.caugner.urlshortener.persistence.internal;

import java.io.File;
import java.sql.SQLException;

/**
 * SQLite implementation of a {@link JdbcStore}.
 */
public class SqLiteStore extends AbstractJdbcStore {

  /**
   * Initiates a SQLite-backed store.
   * 
   * @param sqLiteFile
   *          the file in which to store the SQLite database.
   * @throws SQLException
   *           if the database cannot be connected, e.g. if the file is not writable.
   */
  public SqLiteStore(File sqLiteFile) throws SQLException {
    super("org.sqlite.JDBC", "jdbc:sqlite:" + sqLiteFile.getAbsolutePath());
  }
}