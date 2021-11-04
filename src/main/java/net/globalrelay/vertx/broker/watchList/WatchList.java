package net.globalrelay.vertx.broker.watchList;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.globalrelay.vertx.broker.assets.Assets;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchList {

  private List<Assets> assets;

  JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
