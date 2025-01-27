package com.faforever.client.game;

import com.faforever.client.i18n.I18n;
import com.faforever.client.map.generator.GenerationType;
import com.faforever.client.map.generator.MapGeneratorService;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.preferences.Preferences;
import com.faforever.client.preferences.PreferencesBuilder;
import com.faforever.client.preferences.PreferencesService;
import com.faforever.client.test.AbstractPlainJavaFxTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.testfx.util.WaitForAsyncUtils;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenerateMapControllerTest extends AbstractPlainJavaFxTest {

  @Mock
  private PreferencesService preferencesService;
  @Mock
  private
  NotificationService notificationService;
  @Mock
  private MapGeneratorService mapGeneratorService;
  @Mock
  private I18n i18n;
  @Mock
  private CreateGameController createGameController;

  private Preferences preferences;
  private GenerateMapController instance;

  public void unbindProperties() {
    preferences.getGenerator().spawnCountProperty().unbind();
    preferences.getGenerator().mapSizeProperty().unbind();
    preferences.getGenerator().waterRandomProperty().unbind();
    preferences.getGenerator().plateauRandomProperty().unbind();
    preferences.getGenerator().mountainRandomProperty().unbind();
    preferences.getGenerator().rampRandomProperty().unbind();
    preferences.getGenerator().waterDensityProperty().unbind();
    preferences.getGenerator().plateauDensityProperty().unbind();
    preferences.getGenerator().mountainDensityProperty().unbind();
    preferences.getGenerator().rampDensityProperty().unbind();
  }

  @Before
  public void setUp() throws Exception {
    instance = new GenerateMapController(preferencesService, notificationService, mapGeneratorService, i18n);

    preferences = PreferencesBuilder.create().defaultValues()
        .forgedAlliancePrefs()
        .installationPath(Paths.get(""))
        .then()
        .generatorPrefs()
        .spawnCount(10)
        .mapSize("10km")
        .then()
        .get();

    when(preferencesService.getPreferences()).thenReturn(preferences);

    loadFxml("theme/play/generate_map.fxml", clazz -> instance);
    unbindProperties();
  }

  @Test
  public void testBadMapNameFails() {
    doNothing().when(notificationService).addImmediateErrorNotification(any(IllegalArgumentException.class), anyString());

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();
    instance.previousMapName.setText("Bad");
    instance.onGenerateMap();

    verify(notificationService).addImmediateErrorNotification(any(IllegalArgumentException.class), eq("mapGenerator.invalidName"));
  }

  @Test
  public void testSetLastSpawnCount() {
    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(instance.spawnCountSpinner.getValue().intValue(), 10);
  }

  @Test
  public void testSetLastMapSize() {

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(instance.mapSizeSpinner.getValue(), "10km");
    assertEquals((int) instance.spawnCountSpinner.getValue(), 10);
  }

  @Test
  public void testSetLastWaterRandom() {
    preferences.getGenerator().setWaterRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.waterRandom.isSelected());
  }

  @Test
  public void testSetLastPlateauRandom() {
    preferences.getGenerator().setPlateauRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.plateauRandom.isSelected());
  }

  @Test
  public void testSetLastMountainRandom() {
    preferences.getGenerator().setMountainRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.mountainRandom.isSelected());
  }

  @Test
  public void testSetLastRampRandom() {
    preferences.getGenerator().setRampRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.rampRandom.isSelected());
  }

  @Test
  public void testSetLastWaterSlider() {
    preferences.getGenerator().setWaterDensity(71);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(instance.waterSlider.getValue(), 71, 0);
  }

  @Test
  public void testSetLastMountainSlider() {
    preferences.getGenerator().setMountainDensity(71);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(instance.mountainSlider.getValue(), 71, 0);
  }

  @Test
  public void testSetLastPlateauSlider() {
    preferences.getGenerator().setPlateauDensity(71);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(instance.plateauSlider.getValue(), 71, 0);
  }

  @Test
  public void testSetLastRampSlider() {
    preferences.getGenerator().setRampDensity(71);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals(instance.rampSlider.getValue(), 71, 0);
  }

  @Test
  public void testWaterSliderVisibilityWhenRandom() {
    preferences.getGenerator().setWaterRandom(true);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.waterSliderBox.isVisible());
  }

  @Test
  public void testPlateauSliderVisibilityWhenRandom() {
    preferences.getGenerator().setPlateauRandom(true);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.plateauSliderBox.isVisible());
  }

  @Test
  public void testMountainSliderVisibilityWhenRandom() {
    preferences.getGenerator().setMountainRandom(true);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.mountainSliderBox.isVisible());
  }

  @Test
  public void testRampSliderVisibilityWhenRandom() {
    preferences.getGenerator().setRampRandom(true);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertFalse(instance.rampSliderBox.isVisible());
  }

  @Test
  public void testWaterSliderVisibilityWhenNotRandom() {
    preferences.getGenerator().setWaterRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(instance.waterSliderBox.isVisible());
  }

  @Test
  public void testPlateauSliderVisibilityWhenNotRandom() {
    preferences.getGenerator().setPlateauRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(instance.plateauSliderBox.isVisible());
  }

  @Test
  public void testMountainSliderVisibilityWhenNotRandom() {
    preferences.getGenerator().setMountainRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(instance.mountainSliderBox.isVisible());
  }

  @Test
  public void testRampSliderVisibilityWhenNotRandom() {
    preferences.getGenerator().setRampRandom(false);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    assertTrue(instance.rampSliderBox.isVisible());
  }

  @Test
  public void testOptionsNotDisabledWithoutMapName() {
    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();
    instance.previousMapName.setText("neroxis_map_generator");
    instance.previousMapName.setText("");

    assertFalse(instance.generationTypeComboBox.isDisabled());
    assertFalse(instance.rampRandomBox.isDisabled());
    assertFalse(instance.rampSliderBox.isDisabled());
    assertFalse(instance.waterRandomBox.isDisabled());
    assertFalse(instance.waterSliderBox.isDisabled());
    assertFalse(instance.plateauRandomBox.isDisabled());
    assertFalse(instance.plateauSliderBox.isDisabled());
    assertFalse(instance.mountainRandomBox.isDisabled());
    assertFalse(instance.mountainSliderBox.isDisabled());
  }

  @Test
  public void testOptionsDisabledWithMapName() {
    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();
    instance.previousMapName.setText("neroxis_map_generator");

    assertTrue(instance.generationTypeComboBox.isDisabled());
    assertTrue(instance.rampRandomBox.isDisabled());
    assertTrue(instance.rampSliderBox.isDisabled());
    assertTrue(instance.waterRandomBox.isDisabled());
    assertTrue(instance.waterSliderBox.isDisabled());
    assertTrue(instance.plateauRandomBox.isDisabled());
    assertTrue(instance.plateauSliderBox.isDisabled());
    assertTrue(instance.mountainRandomBox.isDisabled());
    assertTrue(instance.mountainSliderBox.isDisabled());
  }

  @Test
  public void testOptionsNotDisabledWithCasual() {
    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();
    instance.generationTypeComboBox.setValue(GenerationType.TOURNAMENT);
    instance.generationTypeComboBox.setValue(GenerationType.CASUAL);

    assertFalse(instance.rampRandomBox.isDisabled());
    assertFalse(instance.rampSliderBox.isDisabled());
    assertFalse(instance.waterRandomBox.isDisabled());
    assertFalse(instance.waterSliderBox.isDisabled());
    assertFalse(instance.plateauRandomBox.isDisabled());
    assertFalse(instance.plateauSliderBox.isDisabled());
    assertFalse(instance.mountainRandomBox.isDisabled());
    assertFalse(instance.mountainSliderBox.isDisabled());
  }

  @Test
  public void testOptionsDisabledWithTournament() {
    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();
    instance.generationTypeComboBox.setValue(GenerationType.TOURNAMENT);

    assertFalse(instance.generationTypeComboBox.isDisabled());
    assertTrue(instance.rampRandomBox.isDisabled());
    assertTrue(instance.rampSliderBox.isDisabled());
    assertTrue(instance.waterRandomBox.isDisabled());
    assertTrue(instance.waterSliderBox.isDisabled());
    assertTrue(instance.plateauRandomBox.isDisabled());
    assertTrue(instance.plateauSliderBox.isDisabled());
    assertTrue(instance.mountainRandomBox.isDisabled());
    assertTrue(instance.mountainSliderBox.isDisabled());
  }

  @Test
  public void testOptionsDisabledWithBlind() {
    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();
    instance.generationTypeComboBox.setValue(GenerationType.BLIND);

    assertFalse(instance.generationTypeComboBox.isDisabled());
    assertTrue(instance.rampRandomBox.isDisabled());
    assertTrue(instance.rampSliderBox.isDisabled());
    assertTrue(instance.waterRandomBox.isDisabled());
    assertTrue(instance.waterSliderBox.isDisabled());
    assertTrue(instance.plateauRandomBox.isDisabled());
    assertTrue(instance.plateauSliderBox.isDisabled());
    assertTrue(instance.mountainRandomBox.isDisabled());
    assertTrue(instance.mountainSliderBox.isDisabled());
  }

  @Test
  public void testGetOptionMap() {
    preferences.getGenerator().setWaterRandom(false);
    preferences.getGenerator().setMountainRandom(false);
    preferences.getGenerator().setPlateauRandom(false);
    preferences.getGenerator().setRampRandom(false);
    preferences.getGenerator().setWaterDensity(1);
    preferences.getGenerator().setPlateauDensity(2);
    preferences.getGenerator().setMountainDensity(3);
    preferences.getGenerator().setRampDensity(4);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    Map<String, Float> optionMap = instance.getOptionMap();

    assertEquals(optionMap.get("landDensity"), 1 - 1 / 127f, 1 / 127f / 2);
    assertEquals(optionMap.get("plateauDensity"), 2 / 127f, 1 / 127f / 2);
    assertEquals(optionMap.get("mountainDensity"), 3 / 127f, 1 / 127f / 2);
    assertEquals(optionMap.get("rampDensity"), 4 / 127f, 1 / 127f / 2);
  }

  @Test
  public void testGetOptionMapRandom() {
    preferences.getGenerator().setWaterRandom(true);
    preferences.getGenerator().setMountainRandom(true);
    preferences.getGenerator().setPlateauRandom(true);
    preferences.getGenerator().setRampRandom(true);

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    Map<String, Float> optionMap = instance.getOptionMap();

    assertFalse(optionMap.containsKey("landDensity"));
    assertFalse(optionMap.containsKey("mountainDensity"));
    assertFalse(optionMap.containsKey("plateauDensity"));
    assertFalse(optionMap.containsKey("rampDensity"));
  }

  @Test
  public void testOnGenerateMapNoName() {
    preferences.getGenerator().setWaterRandom(true);
    preferences.getGenerator().setMountainRandom(true);
    preferences.getGenerator().setPlateauRandom(true);
    preferences.getGenerator().setRampRandom(true);

    when(mapGeneratorService.generateMap(anyInt(), anyInt(), any(), any())).thenReturn(CompletableFuture.completedFuture("testname"));

    WaitForAsyncUtils.asyncFx(() -> instance.initialize());
    WaitForAsyncUtils.waitForFxEvents();

    instance.setOnCloseButtonClickedListener(() -> {
    });
    instance.setCreateGameController(createGameController);
    instance.onGenerateMap();
    WaitForAsyncUtils.waitForFxEvents();

    verify(mapGeneratorService).generateMap(eq(10), eq(512), eq(new HashMap<>()), eq(GenerationType.CASUAL));
  }
}

