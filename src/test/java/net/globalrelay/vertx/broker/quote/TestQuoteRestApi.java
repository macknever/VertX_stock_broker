package net.globalrelay.vertx.broker.quote;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import net.globalrelay.vertx.broker.MainVerticle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestQuoteRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(TestQuoteRestApi.class);

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(
        new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void return_quote_for_asset(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client
        .get("/quote/AMZN")
        .send()
        .onComplete(
            testContext.succeeding(
                response -> {
                  var json = response.bodyAsJsonObject();
                  LOG.info("Response: ", json);
                  assertEquals("{name=AMZN}", json.getString("assets"));
                  assertEquals(200, response.statusCode());
                  testContext.completeNow();
                }));
  }

  @Test
  void returns_not_found_for_unknow_asset(Vertx vertx, VertxTestContext testContext)
      throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(MainVerticle.PORT));
    client
        .get("/quote/UNKNOWN")
        .send()
        .onComplete(
            testContext.succeeding(
                response -> {
                  var json = response.bodyAsJsonObject();
                  LOG.info("Response: ", json);
                  assertEquals(
                      "{\"message\":\"quote for assetUNKNOWN not available\",\"path\":\"/quote/UNKNOWN\"}",
                      json.encode());
                  assertEquals(404, response.statusCode());
                  testContext.completeNow();
                }));
  }
}
