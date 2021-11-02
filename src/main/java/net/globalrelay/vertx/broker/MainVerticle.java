package net.globalrelay.vertx.broker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import net.globalrelay.vertx.broker.assets.AssetsRestApi;
import net.globalrelay.vertx.broker.qoutes.QuoteRestApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  public static final int PORT = 8888;
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(
        error -> {
          LOG.error("unhandled: ", error);
        });
    vertx.deployVerticle(
        new MainVerticle(),
        ar -> {
          if (ar.failed()) {
            LOG.error("Failed to deploy: ", ar.cause());
          }
          return;
        });
    LOG.info("Deployed {}", MainVerticle.class.getName());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final Router restApi = Router.router(vertx);
    restApi.route().failureHandler(handleFailure());

    AssetsRestApi.attach(restApi);
    QuoteRestApi.attach(restApi);

    vertx
        .createHttpServer()
        .requestHandler(restApi)
        .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
        .listen(
            PORT,
            http -> {
              if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
              } else {
                startPromise.fail(http.cause());
              }
            });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        return;
      }
      LOG.error("Route error: ", errorContext.failure());
      errorContext
          .response()
          .setStatusCode(500)
          .end(new JsonObject().put("message", "Something Wrong :(").toBuffer());
    };
  }
}
