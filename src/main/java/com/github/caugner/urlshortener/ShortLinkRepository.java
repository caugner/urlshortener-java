package com.github.caugner.urlshortener;

/**
 * A readable and writable repository for shortened links.
 */
public interface ShortLinkRepository {

  /**
   * Creates a short link.
   * 
   * @param originalLink
   *          the original link.
   * @return the short link.
   */
  String createShortLink(String originalLink);

  /**
   * Returns the original link
   * 
   * @param shortLink
   *          the short link.
   * @return the corresponding original link.
   */
  String getShortLink(String shortLink);
}
