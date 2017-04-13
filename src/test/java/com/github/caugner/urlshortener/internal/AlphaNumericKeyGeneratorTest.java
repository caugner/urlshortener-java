package com.github.caugner.urlshortener.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class AlphaNumericKeyGeneratorTest {

  private AlphaNumericKeyGenerator generator;

  @Before
  public void setUp() {
    generator = new AlphaNumericKeyGenerator();
  }

  @Test
  public void shouldGenerateKey() {
    int length = 5;
    String key = generator.generate(length);
    assertNotNull(key);
    assertEquals(length, key.length());
  }
}
