package net.globalrelay.vertx.broker.qoutes;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;
import net.globalrelay.vertx.broker.assets.Assets;

import java.math.BigDecimal;

@Value
@Builder
public class Quote {

  Assets assets;

  BigDecimal bid;
  BigDecimal ask;
  BigDecimal lastPrice;
  BigDecimal volume;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
