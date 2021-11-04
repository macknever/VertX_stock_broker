package net.globalrelay.vertx.broker.watchList;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WatchListApi {
  private static final Logger LOG = LoggerFactory.getLogger(WatchListApi.class);

  public static void attach(final Router parent) {

    final Map<UUID, WatchList> watchListPerAccount = new HashMap<>();

    final String path = "/account/watchlist/:accountId";

    parent.get(path).handler(new GetWatchListHandler(watchListPerAccount));

    parent.put(path).handler(new PutWatchListHandler(watchListPerAccount));

    parent.delete(path).handler(new DeleteWatchListHandler(watchListPerAccount));
  }

  public static String getAccountId(RoutingContext context, String s) {
    var accountId = context.pathParam("accountId");
    LOG.debug(s, context.normalizedPath(), accountId);
    return accountId;
  }
}
