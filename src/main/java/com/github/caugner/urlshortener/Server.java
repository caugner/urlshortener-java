package com.github.caugner.urlshortener;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.caugner.urlshortener.internal.AlphaNumericKeyGenerator;
import com.github.caugner.urlshortener.internal.KeyValueShortLinkRepository;
import com.github.caugner.urlshortener.persistence.internal.JdbcStore;
import com.github.caugner.urlshortener.persistence.internal.SqLiteStore;
import com.github.caugner.urlshortener.persistence.internal.WrappedJdbcStore;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;
import fi.iki.elonen.NanoHTTPD.Response.Status;

/**
 * A web interface for a {@link ShortLinkRepository}.
 */
public class Server extends NanoHTTPD {

  private static final String DEFAULT_HOSTNAME = "localhost";
  private static final int DEFAULT_PORT = 8080;
  private static final String FILENAME = "store.db";

  private final ShortLinkRepository repository;

  public Server(ShortLinkRepository repository, String hostname, int port) {
    super(hostname, port);
    this.repository = repository;
  }

  public static void main(String[] args) {
    try {
      ShortLinkRepository repository = getRepository();

      String hostname = args.length >= 1 ? args[0] : DEFAULT_HOSTNAME;
      int port = args.length >= 2 ? Integer.parseInt(args[1]) : DEFAULT_PORT;

      Server server = new Server(repository, hostname, port);
      server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true);
      while (true) {
        try {
          Thread.sleep(10);
        } catch (InterruptedException ie) {
          break;
        }
      }
    } catch (SQLException sqle) {
      System.err.println("Failed to connect to database:\n" + sqle);
    } catch (IOException ioe) {
      System.err.println("Failed to start server:\n" + ioe);
    }
  }

  private static ShortLinkRepository getRepository() throws SQLException {
    WrappedJdbcStore store = getStore();
    AlphaNumericKeyGenerator generator = getGenerator();
    ShortLinkRepository repository = new KeyValueShortLinkRepository(store, generator);
    return repository;
  }

  private static AlphaNumericKeyGenerator getGenerator() {
    return new AlphaNumericKeyGenerator();
  }

  private static WrappedJdbcStore getStore() throws SQLException {
    File storeFile = getSqLiteStoreFile();
    JdbcStore jdbcStore = new SqLiteStore(storeFile);
    return new WrappedJdbcStore(jdbcStore);
  }

  private static File getSqLiteStoreFile() {
    String workingDirectory = System.getProperty("user.dir");
    File storeFile = new File(workingDirectory + FILENAME);
    return storeFile;
  }

  @Override
  public Response serve(IHTTPSession session) {
    Method method = session.getMethod();
    String uri = session.getUri();

    if (Method.GET.equals(method) && "/".equals(uri)) {
      return showForm();
    } else if (Method.POST.equals(method) && "/".equals(uri)) {
      return createShortLink(session);
    } else if (Method.GET.equals(method)) {
      String linkId = uri.substring(1);
      return accessShortLink(linkId);
    } else {
      return notFoundResponse();
    }
  }

  private Response showForm() {
    return newFixedLengthResponse("<html><body><form method=\"post\" action=\".\">"
        + "<input type=\"text\" name=\"url\" placeholder=\"Paste a link to shorten it\" />"
        + " <input type=\"submit\" value=\"Shorten\" />" + "</form></body></html>");
  }

  private Response accessShortLink(String linkId) {
    String link = repository.getShortLink(linkId);
    if (link == null) {
      return notFoundResponse();
    } else {
      return redirectResponse(link);
    }
  }

  private Response createShortLink(IHTTPSession session) {
    final String link;
    try {
      link = determinePostedUrl(session);
    } catch (IOException | ResponseException exception) {
      System.err.println("Failed to determine posted URL:\n" + exception);
      return internalErrorResponse();
    }
    if (link == null) {
      return badRequestResponse();
    }
    String linkId = repository.createShortLink(link);
    if (linkId == null) {
      return badRequestResponse();
    } else {
      String url = createAccessUrl(linkId);
      return newFixedLengthResponse("<html><body><p>Your shortened URL is: <a href=\"" + url + "\">"
          + url + "</a></p><p><a href=\"/\">Shorten another link</a></p></body></html>");
    }
  }

  private String determinePostedUrl(IHTTPSession session) throws IOException, ResponseException {
    Map<String, String> parameters = new HashMap<>();
    session.parseBody(parameters);
    List<String> urls = session.getParameters().get("url");

    if (urls == null || urls.isEmpty()) {
      return null;
    }
    return urls.get(0);
  }

  private String createAccessUrl(String linkId) {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getHostname());
    if (this.getListeningPort() != 80) {
      sb.append(':');
      sb.append(getListeningPort());
    }
    sb.append('/');
    sb.append(linkId);
    return sb.toString();
  }

  private Response badRequestResponse() {
    return emptyResponse(Response.Status.BAD_REQUEST);
  }

  private Response emptyResponse(IStatus status) {
    Response response = newFixedLengthResponse("");
    response.setStatus(status);
    return response;
  }

  private Response notFoundResponse() {
    return emptyResponse(Response.Status.NOT_FOUND);
  }

  private Response internalErrorResponse() {
    return emptyResponse(Response.Status.INTERNAL_ERROR);
  }

  private Response redirectResponse(String link) {
    Response response = emptyResponse(Status.REDIRECT);
    response.addHeader("Location", link);
    return response;
  }
}
