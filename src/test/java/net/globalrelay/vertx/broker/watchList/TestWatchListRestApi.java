package net.globalrelay.vertx.broker.watchList;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.globalrelay.vertx.broker.MainVerticle;
import net.globalrelay.vertx.broker.assets.Assets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestWatchListRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestWatchListRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
        new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void add_and_return_watchlist_for_account(Vertx vertx, VertxTestContext testContext)
      throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    var accountId = UUID.randomUUID();
    client
        .put("/account/watchlist/" + accountId.toString())
        .sendJsonObject(body())
        .onComplete(
            testContext.succeeding(
                response -> {
                  var json = response.bodyAsJsonObject();
                  LOG.info("Response PUT: {} ", json);
                  assertEquals(
                      "{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"},{\"name\":\"GOOG\"}]}",
                      json.encode());
                  assertEquals(200, response.statusCode());
                }))
        .compose(
            next -> {
              client
                  .get("/account/watchlist/" + accountId.toString())
                  .send()
                  .onComplete(
                      testContext.succeeding(
                          response -> {
                            var json = response.bodyAsJsonObject();
                            LOG.info("Response GET: {} ", json);
                            assertEquals(
                                "{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"},{\"name\":\"GOOG\"}]}",
                                json.encode());
                            assertEquals(200, response.statusCode());
                            testContext.completeNow();
                          }));
              return Future.succeededFuture();
            });
  }

  @Test
  void adds_and_deletes_watchlist_for_account(Vertx vertx, VertxTestContext context) {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    var accountId = UUID.randomUUID();

    client
        .put("/account/watchlist/" + accountId.toString())
        .sendJsonObject(body())
        .onComplete(
            context.succeeding(
                response -> {
                  var json = response.bodyAsJsonObject();
                  LOG.info("Response PUT : {}", json);
                  assertEquals(
                      "{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"},{\"name\":\"GOOG\"}]}",
                      json.encode());
                  assertEquals(200, response.statusCode());
                }))
        .compose(
            next -> {
              client
                  .delete("/account/watchlist/" + accountId)
                  .send()
                  .onComplete(
                      context.succeeding(
                          response -> {
                            var json = response.bodyAsJsonObject();
                            LOG.info("Response DELETE: {}", json);
                            assertEquals(
                                "{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"},{\"name\":\"GOOG\"}]}",
                                json.encode());
                            assertEquals(200, response.statusCode());
                            context.completeNow();
                          }));
              return Future.succeededFuture();
            });
  }

  private JsonObject body() {
    return new WatchList(Arrays.asList(new Assets("AMZN"), new Assets("TSLA"), new Assets("GOOG")))
        .toJsonObject();
  }
}
