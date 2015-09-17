package com.faforever.client.patch;

import com.faforever.client.preferences.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

public class AbstractPatchService {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  @Autowired
  PreferencesService preferencesService;
  /**
   * Path to the FAF "bin" directory (e. g. "%PROGRAMDATA\FAForever\bin")
   */
  private Path fafBinDirectory;
  private Path faBinDirectory;

  /**
   * Since it's possible that the user has changed or never specified the game path, this method needs to be called
   * every time before any work is done.
   *
   * @return {@code true} if directories are set up correctly
   */
  protected boolean initAndCheckDirectories() {
    fafBinDirectory = preferencesService.getFafBinDirectory();
    Path faDirectory = preferencesService.getPreferences().getForgedAlliance().getPath();
    if (faDirectory == null) {
      return false;
    }
    faBinDirectory = faDirectory.resolve("bin");
    return true;
  }

  protected Path getFafBinDirectory() {
    return fafBinDirectory;
  }

  protected Path getFaBinDirectory() {
    return faBinDirectory;
  }
}