package com.faforever.client.map;

import com.faforever.client.game.MapInfoBean;
import com.faforever.client.legacy.map.Comment;
import com.faforever.client.util.Callback;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface MapService {

  Image loadSmallPreview(String mapName);

  Image loadLargePreview(String mapName);

  void readMapVaultInBackground(int page, int maxEntries, Callback<List<MapInfoBean>> callback);

  ObservableList<MapInfoBean> getLocalMaps();

  MapInfoBean getMapInfoBeanLocallyFromName(String mapName);

  MapInfoBean getMapInfoBeanFromVaultFromName(String mapName);

  boolean isOfficialMap(String mapName);

  /**
   * Returns {@code true} if the given map is available locally, {@code false} otherwise.
   */
  boolean isAvailable(String mapName);

  CompletionStage<Void> download(String mapName);

  List<Comment> getComments(int mapId);
}