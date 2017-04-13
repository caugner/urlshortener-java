package com.github.caugner.urlshortener.internal;

import java.util.Random;

import com.github.caugner.urlshortener.KeyGenerator;

/**
 * Generates alpha-numeric keys.
 */
public class AlphaNumericKeyGenerator implements KeyGenerator<String> {

  private static final String CHARS;
  private static final int MAX;

  static {
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    CHARS = chars;
    MAX = chars.length();
  }

  @Override
  public String generate(int length) {
    StringBuilder sb = new StringBuilder(length);
    Random rnd = new Random();
    for (int i = 0; i < length; i++) {
      int rndInt = rnd.nextInt(MAX);
      char rndChar = CHARS.charAt(rndInt);
      sb.append(rndChar);
    }
    return sb.toString();
  }

  @Override
  public int getCharCount() {
    return CHARS.length();
  }
}
