package net.globalrelay.vertx.broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);
  public static final List<String> ASSETS =
      Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MFST", "NFLX", "TSLA");

  @Override
  public void handle(RoutingContext context) {

    final JsonArray response = new JsonArray();
    ASSETS.stream().map(Assets::new).forEach(response::add);
    LOG.info("path {} from {}", context.normalizedPath(), response.encode());
    try {
      final int random = ThreadLocalRandom.current().nextInt(100, 300);
      if (random % 2 == 0) {
        Thread.sleep(random);
        context.response().setStatusCode(500).end("sleeping...");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    context
        .request()
        .response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .putHeader("my-value", "my-value")
        .end(response.encode());
  }
}
