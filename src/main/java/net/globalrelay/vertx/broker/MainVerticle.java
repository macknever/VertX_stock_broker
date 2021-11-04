package net.globalrelay.vertx.broker;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
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
    vertx
        .deployVerticle(new MainVerticle())
        .onFailure(
            error -> {
              LOG.error("Failed to deploy :", error);
            })
        .onSuccess(
            id -> {
              LOG.info("Deployed {} with id {}", MainVerticle.class.getName(), id);
            });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    vertx
        .deployVerticle(
            RestApiVerticle.class.getName(), new DeploymentOptions().setInstances(processors()))
        .onSuccess(
            id -> {
              LOG.info("Deployed {} with id {}", RestApiVerticle.class.getName(), id);
              startPromise.complete();
            })
        .onFailure(startPromise::fail);
  }

  private int processors() {
    System.out.println(Runtime.getRuntime().availableProcessors());
    return Math.max(1, Runtime.getRuntime().availableProcessors());
  }

  public static Handler<RoutingContext> handleFailure() {
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
