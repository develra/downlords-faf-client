package com.faforever.client.fa;

import com.faforever.client.game.KnownFeaturedMod;
import lombok.Getter;

public enum RatingMode {
  GLOBAL("userInfo.ratingHistory.global", KnownFeaturedMod.FAF, "global"),
  LADDER_1V1("userInfo.ratingHistory.1v1", KnownFeaturedMod.LADDER_1V1, "ladder_1v1"),
  NONE("", null, "");

  @Getter
  private final String i18nKey;
  @Getter
  private final KnownFeaturedMod featuredMod;
  //Used to determine game rating type during launch until server deployed with version that includes type in launch message
  @Getter
  private final String ratingType;

  RatingMode(String i18nKey, KnownFeaturedMod featuredMod, String ratingType) {
    this.i18nKey = i18nKey;
    this.featuredMod = featuredMod;
    this.ratingType = ratingType;
  }
}
