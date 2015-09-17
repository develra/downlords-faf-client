package com.faforever.client.legacy.domain;

import java.util.List;
import java.util.Map;

public class GameInfo extends ServerMessage {

  private String host;
  private Integer uid;
  private String title;
  private GameAccess access;
  private String mapname;
  private GameState state;
  private Boolean[] options;
  // FAF calls this "game_type" but it's actually the victory condition.
  private VictoryCondition gameType;
  private String featuredMod;
  private Integer maxPlayers;
  private Integer numPlayers;
  private Map<String, String> simMods;
  private Map<String, List<String>> teams;
  private Map<String, Integer> featuredModVersions;

  @Override
  public String toString() {
    return "GameInfo{" +
        "uid=" + getUid() +
        ", title='" + getTitle() + '\'' +
        ", state=" + getState() +
        '}';
  }

  public Integer getUid() {
    return uid;
  }

  public void setUid(Integer uid) {
    this.uid = uid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public GameState getState() {
    return state;
  }

  public void setState(GameState state) {
    this.state = state;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public GameAccess getAccess() {
    return access;
  }

  public void setAccess(GameAccess access) {
    this.access = access;
  }

  public String getMapname() {
    return mapname;
  }

  public void setMapname(String mapname) {
    this.mapname = mapname;
  }

  public Boolean[] getOptions() {
    return options;
  }

  public void setOptions(Boolean[] options) {
    this.options = options;
  }

  public VictoryCondition getGameType() {
    return gameType;
  }

  public void setGameType(VictoryCondition gameType) {
    this.gameType = gameType;
  }

  public String getFeaturedMod() {
    return featuredMod;
  }

  public void setFeaturedMod(String featuredMod) {
    this.featuredMod = featuredMod;
  }

  public Integer getMaxPlayers() {
    return maxPlayers;
  }

  public void setMaxPlayers(Integer maxPlayers) {
    this.maxPlayers = maxPlayers;
  }

  public Integer getNumPlayers() {
    return numPlayers;
  }

  public void setNumPlayers(Integer numPlayers) {
    this.numPlayers = numPlayers;
  }

  public Map<String, String> getSimMods() {
    return simMods;
  }

  public void setSimMods(Map<String, String> simMods) {
    this.simMods = simMods;
  }

  public Map<String, List<String>> getTeams() {
    return teams;
  }

  public void setTeams(Map<String, List<String>> teams) {
    this.teams = teams;
  }

  public Map<String, Integer> getFeaturedModVersions() {
    return featuredModVersions;
  }

  public void setFeaturedModVersions(Map<String, Integer> featuredModVersions) {
    this.featuredModVersions = featuredModVersions;
  }
}