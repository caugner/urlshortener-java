package com.github.caugner.urlshortener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(EasyMockRunner.class)
public class ServerTest {

  @Mock
  private ShortLinkRepository repository;

  private Server server;
  private String hostname = "localhost";
  private int port = 8080;

  private HttpClient client;

  private Executor executor;

  @Before
  public void setUp() throws Exception {
    server = new Server(repository, hostname, port);
    server.start();

    client = HttpClientBuilder.create().disableRedirectHandling().build();
    executor = Executor.newInstance(client);
  }

  @After
  public void tearDown() {
    server.stop();
  }

  @Test
  public void shouldHaveStartPage() throws Exception {
    String startUrl = "http://" + hostname + ":" + port + "/";
    HttpResponse response = executor.execute(Request.Get(startUrl)).returnResponse();

    assertEquals(200, response.getStatusLine().getStatusCode());
    assertTrue(EntityUtils.toString(response.getEntity()).contains("Paste a link to shorten it"));
  }

  @Test
  public void shouldCreateLinkInRepository() throws Exception {
    final String link = "https://bitly.com";
    final String id = "key42";
    EasyMock.expect(repository.createShortLink(link)).andReturn(id);
    EasyMock.replay(repository);

    String postUrl = "http://" + hostname + ":" + port + "/";
    HttpResponse response = executor
        .execute(Request.Post(postUrl).bodyForm(Form.form().add("url", link).build()))
        .returnResponse();

    assertEquals(200, response.getStatusLine().getStatusCode());
    assertTrue(EntityUtils.toString(response.getEntity()).contains(id));
  }

  @Test
  public void shouldReturnLinkInRepository() throws Exception {
    final String link = "https://bitly.com";
    final String id = "key42";
    EasyMock.expect(repository.getShortLink(id)).andReturn(link);
    EasyMock.replay(repository);

    String accessUrl = "http://" + hostname + ":" + port + "/" + id;

    final HttpResponse response = executor.execute(Request.Get(accessUrl)).returnResponse();

    assertEquals(link, response.getFirstHeader("Location").getValue());
    assertEquals(301, response.getStatusLine().getStatusCode());
  }

  @Test
  public void shouldReturn400ForPostWithoutUrl() throws Exception {
    String postUrl = "http://" + hostname + ":" + port + "/";
    HttpResponse response = executor.execute(Request.Post(postUrl)).returnResponse();

    assertEquals(400, response.getStatusLine().getStatusCode());
  }

  @Test
  public void shouldReturn404ForOtherLinks() throws Exception {
    final String id = "key42";
    EasyMock.expect(repository.getShortLink(id)).andReturn(null);
    EasyMock.replay(repository);

    String accessUrl = "http://" + hostname + ":" + port + "/" + id;

    final HttpResponse response = executor.execute(Request.Get(accessUrl)).returnResponse();

    assertEquals(404, response.getStatusLine().getStatusCode());
  }
}
