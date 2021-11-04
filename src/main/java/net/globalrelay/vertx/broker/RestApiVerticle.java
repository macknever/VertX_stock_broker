package net.globalrelay.vertx.broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import net.globalrelay.vertx.broker.assets.AssetsRestApi;
import net.globalrelay.vertx.broker.qoutes.QuoteRestApi;
import net.globalrelay.vertx.broker.watchList.WatchListApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    StartHttpServerAndAttachRoutes(vertx, startPromise);
  }

  public static void StartHttpServerAndAttachRoutes(Vertx vertx, Promise<Void> startPromise) {
    final Router restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create()).failureHandler(MainVerticle.handleFailure());

    AssetsRestApi.attach(restApi);
    QuoteRestApi.attach(restApi);
    WatchListApi.attach(restApi);

    vertx
        .createHttpServer()
        .requestHandler(restApi)
        .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
        .listen(
            MainVerticle.PORT,
            http -> {
              if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
              } else {
                startPromise.fail(http.cause());
              }
            });
  }
}
