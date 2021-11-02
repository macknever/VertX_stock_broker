package net.globalrelay.vertx.broker.assets;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);
  public static final List<String> ASSETS =
      Arrays.asList("AAPL", "AMZN", "FB", "GOOG", "MFST", "NFLX", "TSLA");

  public static void attach(Router parent) {

    parent
        .get("/assets")
        .handler(
            context -> {
              final JsonArray response = new JsonArray();
              ASSETS.stream().map(Assets::new).forEach(response::add);
              LOG.info("path {} from {}", context.normalizedPath(), response.encode());
              context.request().response().end(response.encode());
            });
  }
}
