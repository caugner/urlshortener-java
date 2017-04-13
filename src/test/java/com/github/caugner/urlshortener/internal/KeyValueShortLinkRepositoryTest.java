package com.github.caugner.urlshortener.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.caugner.urlshortener.KeyGenerator;
import com.github.caugner.urlshortener.ShortLinkRepository;
import com.github.caugner.urlshortener.persistence.KeyValueStore;

public class KeyValueShortLinkRepositoryTest {

  private static final String URL_TINY = "https://tinyurl.com/";
  private static final String URL_BITLY = "https://bitly.com/";

  private ShortLinkRepository repository;
  private HashMapKeyValueStore store;

  @Before
  public void setUp() {
    store = new HashMapKeyValueStore();
    SimpleGenerator generator = new SimpleGenerator();
    repository = new KeyValueShortLinkRepository(store, generator);
  }

  @Test
  public void idShouldNotBeEmpty() {
    String id = repository.createShortLink(URL_BITLY);
    assertNotNull(id);
    assertFalse(id.isEmpty());
  }

  @Test
  public void idShouldBeAlphaNumeric() {
    String id = repository.createShortLink(URL_BITLY);
    assertTrue(id.matches("^[a-zA-Z0-9]+$"));
  }

  @Test
  public void idShouldBeDifferentForTwoLinks() {
    String oneId = repository.createShortLink(URL_BITLY);
    String otherId = repository.createShortLink(URL_TINY);
    assertNotEquals(oneId, otherId);
  }

  @Test
  public void idShouldBeLinkedToUrl() {
    String url = URL_BITLY;
    String id = repository.createShortLink(url);
    assertEquals(url, repository.getShortLink(id));
  }

  private static class HashMapKeyValueStore implements KeyValueStore<String> {

    private final Map<String, String> map = new HashMap<>();

    @Override
    public boolean has(String key) {
      return map.containsKey(key);
    }

    @Override
    public void put(String key, String value) {
      map.put(key, value);
    }

    @Override
    public Optional<String> get(String key) {
      return Optional.of(map.get(key));
    }

    @Override
    public long size() {
      return map.size();
    }
  }

  private static class SimpleGenerator implements KeyGenerator<String> {

    private int nextId = 0;

    @Override
    public String generate(int length) {
      return String.format("%0" + length + "d", nextId++);
    }

    @Override
    public int getCharCount() {
      return 10;
    }

  }
}
