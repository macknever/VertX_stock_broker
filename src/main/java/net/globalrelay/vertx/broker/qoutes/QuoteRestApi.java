package net.globalrelay.vertx.broker.qoutes;

import io.vertx.ext.web.Router;
import net.globalrelay.vertx.broker.assets.Assets;
import net.globalrelay.vertx.broker.assets.AssetsRestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class QuoteRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(QuoteRestApi.class);

  public static void attach(Router parent) {

    final Map<String, Quote> cacheQuotes = new HashMap<>();
    AssetsRestApi.ASSETS.forEach(
        symbol -> {
          cacheQuotes.put(symbol, initRandomQuote(symbol));
        });
    parent.get("/quote/:asset").handler(new GetQuoteHandler(cacheQuotes));
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
