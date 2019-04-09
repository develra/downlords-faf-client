package com.faforever.client.game;

import com.faforever.client.player.Player;
import com.faforever.client.preferences.PreferencesService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

/**
 * Knows how to starts/stop Forged Alliance with proper parameters. Downloading maps, mods and updates as well as
 * notifying the server about whether the preferences is running or not is <strong>not</strong> this service's
 * responsibility.
 */
@Lazy
@Service
public class ForgedAllianceService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final PreferencesService preferencesService;

  public ForgedAllianceService(PreferencesService preferencesService) {
    this.preferencesService = preferencesService;
  }

  public Process startGame(
    int uid,
    @Nullable Faction faction,
    String leaderboardName,
    int gpgPort,
    int localReplayPort,
    boolean rehost,
    Player currentPlayer,
    int team,
    Integer expectedPlayers
  ) throws IOException {
    Path executable = getExecutable();

    List<String> launchCommand = defaultLaunchCommand()
      .executable(executable)
      .uid(uid)
      .faction(faction)
      .clan(currentPlayer.getClanTag())
      .country(currentPlayer.getCountry())
      .rank(currentPlayer.getRating().get(leaderboardName))
      .username(currentPlayer.getDisplayName())
      .logFile(preferencesService.getFafLogDirectory().resolve("game.log"))
      .localGpgPort(gpgPort)
      .localReplayPort(localReplayPort)
      .rehost(rehost)
      .team(team)
      .expectedPlayers(expectedPlayers)
      .build();

    return launch(executable, launchCommand);
  }

  public Process startReplay(Path path, @Nullable Integer replayId) throws IOException {
    Path executable = getExecutable();

    List<String> launchCommand = defaultLaunchCommand()
      .executable(executable)
      .replayFile(path)
      .replayId(replayId)
      .logFile(preferencesService.getFafLogDirectory().resolve("game.log"))
      .build();

    return launch(executable, launchCommand);
  }

  public Process startReplay(URI replayUri, Integer replayId, Player currentPlayer) throws IOException {
    Path executable = getExecutable();

    List<String> launchCommand = defaultLaunchCommand()
      .executable(executable)
      .replayUri(replayUri)
      .replayId(replayId)
      .logFile(preferencesService.getFafLogDirectory().resolve("replay.log"))
      .username(currentPlayer.getDisplayName())
      .build();

    return launch(executable, launchCommand);
  }

  private Path getExecutable() {
    return preferencesService.getFafBinDirectory().resolve(PreferencesService.FORGED_ALLIANCE_EXE);
  }

  private LaunchCommandBuilder defaultLaunchCommand() {
    return LaunchCommandBuilder.create()
      .executableDecorator(preferencesService.getPreferences().getForgedAlliance().getExecutableDecorator());
  }

  @NotNull
  private Process launch(Path executablePath, List<String> launchCommand) throws IOException {
    Path executeDirectory = preferencesService.getPreferences().getForgedAlliance().getExecutionDirectory();
    if (executeDirectory == null) {
      executeDirectory = executablePath.getParent();
    }

    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.inheritIO();
    processBuilder.directory(executeDirectory.toFile());
    processBuilder.command(launchCommand);

    logger.info("Starting Forged Alliance with command: {} in directory: {}", processBuilder.command(), executeDirectory);

    return processBuilder.start();
  }
}