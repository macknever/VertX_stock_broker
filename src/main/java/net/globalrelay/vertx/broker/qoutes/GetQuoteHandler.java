package net.globalrelay.vertx.broker.qoutes;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class GetQuoteHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetQuoteHandler.class);
  private Map<String, Quote> cacheQuotes;

  public GetQuoteHandler(Map<String, Quote> cacheQuotes) {
    this.cacheQuotes = cacheQuotes;
  }

  @Override
  public void handle(RoutingContext context) {
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
  }
}
