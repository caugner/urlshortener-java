package com.github.caugner.urlshortener.persistence.internal;

import java.sql.SQLException;
import java.util.Optional;

import com.github.caugner.urlshortener.persistence.KeyValueStore;

/**
 * Wraps a {@link JdbcStore}, converting {@link SQLException}s to {@link RuntimeException}s.
 */
public class WrappedJdbcStore implements KeyValueStore<String> {

  private final JdbcStore store;

  public WrappedJdbcStore(JdbcStore store) {
    this.store = store;
  }

  @Override
  public boolean has(String key) {
    try {
      return store.has(key);
    } catch (SQLException e) {
      throw new RuntimeException("Could not determine existence of key", e);
    }
  }

  @Override
  public Optional<String> get(String key) {
    try {
      return Optional.of(store.get(key));
    } catch (SQLException e) {
      throw new RuntimeException("Could not get value for key", e);
    }
  }

  @Override
  public void put(String key, String value) {
    boolean result;
    try {
      result = store.put(key, value);
      if (!result) {
        throw new RuntimeException("Could not insert key-value pair");
      }
    } catch (SQLException e) {
      throw new RuntimeException("Could not put value", e);
    }
  }

  @Override
  public long size() {
    try {
      return store.size();
    } catch (SQLException e) {
      throw new RuntimeException("Could not determine store size", e);
    }
  }
}
