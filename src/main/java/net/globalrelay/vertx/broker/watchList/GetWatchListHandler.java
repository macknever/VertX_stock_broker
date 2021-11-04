package net.globalrelay.vertx.broker.watchList;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static net.globalrelay.vertx.broker.watchList.WatchListApi.getAccountId;

public class GetWatchListHandler implements Handler<RoutingContext> {

  private final Map<UUID, WatchList> watchListPerAccount;

  public GetWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {

    String accountId = getAccountId(context, "path {} from {}");
    var maybeWatchList = Optional.ofNullable(watchListPerAccount.get(UUID.fromString(accountId)));
    if (maybeWatchList.isEmpty()) {
      context
          .response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(
              new JsonObject()
                  .put("message", "watchlist for account" + accountId + "is not available")
                  .put("path", context.normalizedPath())
                  .toBuffer());
      return;
    }

    context.response().end(maybeWatchList.get().toJsonObject().toBuffer());
  }
}
