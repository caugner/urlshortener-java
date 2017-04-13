package com.github.caugner.urlshortener.persistence.internal;

import java.sql.SQLException;

import com.github.caugner.urlshortener.persistence.KeyValueStore;

/**
 * A {@link KeyValueStore} that may throw {@link SQLException}s.
 */
public interface JdbcStore {

  /**
   * Like {@link KeyValueStore#get(String)}, but may throw an {@link SQLException}.
   * 
   * @see KeyValueStore#get(String)
   */
  public String get(String key) throws SQLException;

  /**
   * Like {@link KeyValueStore#has(String)}, but may throw an {@link SQLException}.
   * 
   * @see KeyValueStore#has(String)
   */
  public boolean has(String key) throws SQLException;

  /**
   * Like {@link KeyValueStore#put(String, Object)}, but may throw an {@link SQLException}.
   * 
   * @see KeyValueStore#put(String, Object)
   */
  public boolean put(String key, String value) throws SQLException;

  /**
   * Like {@link KeyValueStore#size()}, but may throw an {@link SQLException}.
   * 
   * @see KeyValueStore#size()
   */
  public long size() throws SQLException;

}