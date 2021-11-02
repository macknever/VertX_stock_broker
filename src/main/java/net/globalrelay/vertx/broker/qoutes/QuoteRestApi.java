package net.globalrelay.vertx.broker.qoutes;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import net.globalrelay.vertx.broker.assets.Assets;
import net.globalrelay.vertx.broker.assets.AssetsRestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(QuoteRestApi.class);

  public static void attach(Router parent) {

    final Map<String, Quote> cacheQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(
        symbol -> {
          cacheQuotes.put(symbol, initRandomQuote(symbol));
        });
    parent
        .get("/quote/:asset")
        .handler(
            context -> {
              final String assetParam = context.pathParam("asset");
              LOG.debug("Asset parameter:", assetParam);

              var maybeQuote = Optional.ofNullable(cacheQuotes.get(assetParam));
              if (maybeQuote.isEmpty()) {
                context
                    .response()
                    .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                    .end(
                        new JsonObject()
                            .put("message", "quote for asset" + assetParam + " not available")
                            .put("path", context.normalizedPath())
                            .toBuffer());
              }
              final JsonObject response = maybeQuote.get().toJsonObject();
              LOG.info("Path {} from {}", context.normalizedPath(), response.encode());
              context.response().end(response.toBuffer());
            });
  }

  private static Quote initRandomQuote(String assetParam) {
    return Quote.builder()
        .assets(new Assets(assetParam))
        .ask(randomValue())
        .lastPrice(randomValue())
        .volume(randomValue())
        .bid(randomValue())
        .build();
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1, 100));
  }
}
