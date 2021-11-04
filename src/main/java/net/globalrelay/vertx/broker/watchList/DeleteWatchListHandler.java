package net.globalrelay.vertx.broker.watchList;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;

public class DeleteWatchListHandler implements Handler<RoutingContext> {

  private final Map<UUID, WatchList> watchListPerAccount;

  public DeleteWatchListHandler(Map<UUID, WatchList> watchListPerAccount) {
    this.watchListPerAccount = watchListPerAccount;
  }

  @Override
  public void handle(RoutingContext context) {

    String accountId = WatchListApi.getAccountId(context, "{} from account {}");
    final WatchList delete = watchListPerAccount.remove(UUID.fromString(accountId));
    context.response().end(delete.toJsonObject().toBuffer());
  }
}
