package com.github.caugner.urlshortener.persistence.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SqLiteStoreTest {

  private static final String TEST_VALUE = "aValue";
  private static final String TEST_KEY = "aKey";
  private SqLiteStore connector;
  private File tempFile;

  @Before
  public void setUp() throws Exception {
    tempFile = File.createTempFile(SqLiteStoreTest.class.getSimpleName(), ".db");
    connector = new SqLiteStore(tempFile);
  }

  @After
  public void tearDown() throws Exception {
    try {
      connector.close();
    } finally {
      tempFile.delete();
    }
  }

  @Test
  public void getShouldReturnValueSetByPut() throws Exception {
    assertEquals(null, connector.get(TEST_KEY));
    connector.put(TEST_KEY, TEST_VALUE);
    assertEquals(TEST_VALUE, connector.get(TEST_KEY));
  }

  @Test
  public void hasShouldReturnTrueAfterPut() throws Exception {
    assertFalse(connector.has(TEST_KEY));
    connector.put(TEST_KEY, TEST_VALUE);
    assertTrue(connector.has(TEST_KEY));
  }

  @Test
  public void sizeShouldIncreaseAfterPut() throws Exception {
    assertEquals(0, connector.size());
    connector.put(TEST_KEY, TEST_VALUE);
    assertEquals(1, connector.size());
  }
}
