package com.github.caugner.urlshortener;

/**
 * Generates keys.
 *
 * @param <T>
 *          type of the generated keys.
 */
public interface KeyGenerator<T> {

  /**
   * Generates a key of the desired length.
   * 
   * @param length
   *          key length.
   * @return the generated key.
   */
  public String generate(int length);

  /**
   * Returns the number of different key characters.
   * 
   * @return number of different characters.
   */
  public int getCharCount();
}
