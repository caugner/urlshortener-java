package com.github.caugner.urlshortener.internal;

import com.github.caugner.urlshortener.KeyGenerator;
import com.github.caugner.urlshortener.ShortLinkRepository;
import com.github.caugner.urlshortener.persistence.KeyValueStore;

/**
 * Stores links in a {@link KeyValueStore} using keys from a {@link KeyGenerator}.
 */
public class KeyValueShortLinkRepository implements ShortLinkRepository {

  /**
   * Extra key length to mitigate scraping.
   */
  private static final int EXTRA_LENGTH = 3;

  private final KeyValueStore<String> store;
  private final KeyGenerator<String> generator;

  /**
   * Current length of keys.
   */
  private int length;

  /**
   * Remaining number of items that the current length allows.
   */
  private long capacityLeft;

  public KeyValueShortLinkRepository(KeyValueStore<String> store, KeyGenerator<String> generator) {
    this.store = store;
    this.generator = generator;
    determineStoreCapacityAndRequiredKeyLength();
  }

  private void determineStoreCapacityAndRequiredKeyLength() {
    long currentSize = store.size();
    int uniqueCharCount = generator.getCharCount();
    int minimalLength = 1 + (int) Math.ceil(Math.log(1 + currentSize) / Math.log(uniqueCharCount));
    this.capacityLeft = (long) Math.floor(Math.pow(uniqueCharCount, minimalLength)) - currentSize;
    this.length = EXTRA_LENGTH + minimalLength;
  }

  /**
   * Creates a short link for the link provided.
   * 
   * @param link
   *          the original ink.
   * @return the short link.
   */
  public synchronized String createShortLink(String link) {
    String key = getUniqueKey();
    storeShortLink(link, key);
    return key;
  }

  private String getUniqueKey() {
    String key;
    do {
      key = generator.generate(length);
    } while (store.has(key));
    return key;
  }

  private void storeShortLink(String link, String key) {
    store.put(key, link);
    capacityLeft -= 1;
    if (capacityLeft <= 0) {
      determineStoreCapacityAndRequiredKeyLength();
    }
  }

  /**
   * Returns the original link for a short link.
   * 
   * @param shortLink
   *          the short link.
   * @return the original link, if exists; else {@code null}
   */
  public String getShortLink(String shortLink) {
    return store.get(shortLink).orElse(null);
  }

}
