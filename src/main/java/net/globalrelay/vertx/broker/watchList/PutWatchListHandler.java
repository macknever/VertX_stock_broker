package net.globalrelay.vertx.broker.watchList;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;

public class PutWatchListHandler implements Handler<RoutingContext> {

  private final Map<UUID, WatchList> watchListPerAccount;

  public PutWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {

    String accountId = WatchListApi.getAccountId(context, "{} from account {}");

    var json = context.getBodyAsJson();
    var watchList = json.mapTo(WatchList.class);
    watchListPerAccount.put(UUID.fromString(accountId), watchList);
    context.response().end(json.toBuffer());
  }
}
