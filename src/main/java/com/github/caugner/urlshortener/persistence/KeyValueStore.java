package com.github.caugner.urlshortener.persistence;

import java.util.Optional;

/**
 * Manages key-value pairs.
 *
 * @param <T>
 *          type of the values.
 */
public interface KeyValueStore<T> {

  /**
   * Checks whether the key exists.
   * 
   * @param key
   *          the key.
   * @return {@code true}, if it exists; {@code false} otherwise
   */
  public boolean has(String key);

  /**
   * Stores a key-value pair.
   * 
   * @param key
   *          the key.
   * @param value
   *          the value.
   */
  public void put(String key, T value);

  /**
   * Returns the value for a key.
   * 
   * @param key
   *          the key.
   * @return the corresponding value.
   */
  public Optional<T> get(String key);

  /**
   * Returns the number of pairs in the store.
   * 
   * @return store size.
   */
  public long size();
}
