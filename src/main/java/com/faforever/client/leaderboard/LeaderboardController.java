package com.faforever.client.leaderboard;

import com.faforever.client.fx.AbstractViewController;
import com.faforever.client.fx.JavaFxUtil;
import com.faforever.client.fx.StringCell;
import com.faforever.client.game.KnownFeaturedMod;
import com.faforever.client.i18n.I18n;
import com.faforever.client.main.event.NavigateEvent;
import com.faforever.client.notification.ImmediateErrorNotification;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.player.Player;
import com.faforever.client.player.PlayerService;
import com.faforever.client.reporting.ReportingService;
import com.faforever.client.theme.UiService;
import com.faforever.client.util.Assert;
import com.faforever.client.util.RatingUtil;
import com.faforever.client.util.Validator;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableList;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class LeaderboardController extends AbstractViewController<Node> {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final PseudoClass NOTIFICATION_HIGHLIGHTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("highlighted-bar");

  private final LeaderboardService leaderboardService;
  private final NotificationService notificationService;
  private final I18n i18n;
  private final ReportingService reportingService;
  private final PlayerService playerService;
  private final UiService uiService;
  public VBox leaderboardRoot;
  public TableColumn<LeaderboardEntry, Number> rankColumn;
  public TableColumn<LeaderboardEntry, String> nameColumn;
  public TableColumn<LeaderboardEntry, Number> gamesPlayedColumn;
  public TableColumn<LeaderboardEntry, Number> scoreColumn;
  public TableView<LeaderboardEntry> ratingTable;
  public TextField searchTextField;
  public Pane connectionProgressPane;
  public Pane contentPane;
  public BarChart ratingDistributionChart;
  public Label playerDivisionNameLabel;
  public Label playerScoreLabel;
  public Label seasonLabel;
  public ComboBox<Division> majorDivisionPicker;
  public Arc scoreArc;
  public TabPane subDivisionTabs;
  private KnownFeaturedMod ratingType;
  private Text youLabel;
  private InvalidationListener playerLeagueScoreListener;

  @Override
  public void initialize() {
    super.initialize();
    rankColumn.setCellValueFactory(param -> param.getValue().rankProperty());
    rankColumn.setCellFactory(param -> new StringCell<>(rank -> i18n.number(rank.intValue())));

    nameColumn.setCellValueFactory(param -> param.getValue().usernameProperty());
    nameColumn.setCellFactory(param -> new StringCell<>(name -> name));

    gamesPlayedColumn.setCellValueFactory(param -> param.getValue().gamesPlayedProperty());
    gamesPlayedColumn.setCellFactory(param -> new StringCell<>(count -> i18n.number(count.intValue())));

    scoreColumn.setCellValueFactory(param -> param.getValue().ratingProperty());
    scoreColumn.setCellFactory(param -> new StringCell<>(rating -> i18n.number(rating.intValue())));

    contentPane.managedProperty().bind(contentPane.visibleProperty());
    connectionProgressPane.managedProperty().bind(connectionProgressPane.visibleProperty());
    connectionProgressPane.visibleProperty().bind(contentPane.visibleProperty().not());

    youLabel = new Text("You");
    youLabel.setId("1v1-you-text");
    majorDivisionPicker.setConverter(divisionStringConverter());

    leaderboardService.getDivisions().thenAccept(divisions -> Platform.runLater(() -> {
      majorDivisionPicker.getItems().clear();

      majorDivisionPicker.getItems().addAll(
          divisions.stream().filter(division -> division.getSubDivisionIndex() == 1).collect(Collectors.toList()));
    })).exceptionally(throwable -> {
      logger.warn("Could not read divisions", throwable);
      return null;
    });

    JavaFxUtil.addListener(playerService.currentPlayerProperty(), (observable, oldValue, newValue) -> Platform.runLater(() -> setCurrentPlayer(newValue)));
    playerService.getCurrentPlayer().ifPresent(this::setCurrentPlayer);

    searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (Validator.isInt(newValue)) {
        ratingTable.scrollTo(Integer.parseInt(newValue) - 1);
      } else {
        LeaderboardEntry foundPlayer = null;
        for (LeaderboardEntry leaderboardEntry : ratingTable.getItems()) {
          if (leaderboardEntry.getUsername().toLowerCase().startsWith(newValue.toLowerCase())) {
            foundPlayer = leaderboardEntry;
            break;
          }
        }
        if (foundPlayer == null) {
          for (LeaderboardEntry leaderboardEntry : ratingTable.getItems()) {
            if (leaderboardEntry.getUsername().toLowerCase().contains(newValue.toLowerCase())) {
              foundPlayer = leaderboardEntry;
              break;
            }
          }
        }
        if (foundPlayer != null) {
          ratingTable.scrollTo(foundPlayer);
          ratingTable.getSelectionModel().select(foundPlayer);
        } else {
          ratingTable.getSelectionModel().select(null);
        }
      }
    });
  }

  @Override
  protected void onDisplay(NavigateEvent navigateEvent) {
    Assert.checkNullIllegalState(ratingType, "ratingType must not be null");

    contentPane.setVisible(false);
    leaderboardService.getEntries(ratingType).thenAccept(leaderboardEntryBeans -> {
      ratingTable.setItems(observableList(leaderboardEntryBeans));
      contentPane.setVisible(true);
    }).exceptionally(throwable -> {
      contentPane.setVisible(false);
      logger.warn("Error while loading leaderboard entries", throwable);
      notificationService.addNotification(new ImmediateErrorNotification(
          i18n.get("errorTitle"), i18n.get("leaderboard.failedToLoad"),
          throwable, i18n, reportingService
      ));
      return null;
    });
  }

  public Node getRoot() {
    return leaderboardRoot;
  }

  public void setRatingType(KnownFeaturedMod ratingType) {
    this.ratingType = ratingType;
  }

  private void setCurrentPlayer(Player player) {
    playerLeagueScoreListener = leagueObservable -> Platform.runLater(() -> updateStats(player));

    JavaFxUtil.addListener(player.subDivisionIndexProperty(), new WeakInvalidationListener(playerLeagueScoreListener));
    JavaFxUtil.addListener(player.scoreProperty(), new WeakInvalidationListener(playerLeagueScoreListener));
    updateStats(player);
  }

  private void updateStats(Player player) {

    leaderboardService.getLeagueEntryForPlayer(player.getId()).thenAccept(leaderboardEntry -> Platform.runLater(() -> {
      playerScoreLabel.setText(i18n.number(leaderboardEntry.getScore()));
      leaderboardService.getDivisions().thenAccept(divisions -> {
        divisions.forEach(division -> {
          if (division.getMajorDivisionIndex() == leaderboardEntry.getMajorDivisionIndex()
              && division.getSubDivisionIndex() == leaderboardEntry.getSubDivisionIndex()) {
            playerDivisionNameLabel.setText(i18n.get("leaderboard.divisionName",
                division.getMajorDivisionName(), division.getSubDivisionName()));
                //i18n.get(division.getMajorDivisionName().getI18nKey()).toUpperCase(),
                //i18n.get(division.getSubDivisionName().getI18nKey()).toUpperCase()));
            scoreArc.setLength(-360.0 * leaderboardEntry.getScore() / division.getHighestScore());
          }
        });
      });
    })).exceptionally(throwable -> {
      // Debug instead of warn, since it's fairly common that players don't have a leaderboard entry which causes a 404
      logger.debug("Leaderboard entry could not be read for current player: " + player.getUsername(), throwable);
      return null;
    });

    leaderboardService.getLadder1v1Stats()
        .thenAccept(ranked1v1Stats -> {
          ranked1v1Stats.sort(Comparator.comparingInt(RatingStat::getRating));
          plotRatingDistributions(ranked1v1Stats, player);
        })
        .exceptionally(throwable -> {
          logger.warn("Could not plot rating distribution", throwable);
          return null;
        });
  }

  private void plotRatingDistributions(List<RatingStat> ratingStats, Player player) {
    XYChart.Series<String, Integer> series = new XYChart.Series<>();
    series.setName(i18n.get("ranked1v1.players", LeaderboardService.MINIMUM_GAMES_PLAYED_TO_BE_SHOWN));
    int currentPlayerRating = RatingUtil.roundRatingToNextLowest100(RatingUtil.getLeaderboardRating(player));

    series.getData().addAll(ratingStats.stream()
        .sorted(Comparator.comparingInt(RatingStat::getRating))
        .map(item -> {
          int rating = item.getRating();
          XYChart.Data<String, Integer> data = new XYChart.Data<>(i18n.number(rating), item.getCountWithEnoughGamesPlayed());
          if (rating == currentPlayerRating) {
            data.nodeProperty().addListener((observable, oldValue, newValue) -> {
              newValue.pseudoClassStateChanged(NOTIFICATION_HIGHLIGHTED_PSEUDO_CLASS, true);
              addNodeOnTopOfBar(data, youLabel);
            });
          }
          return data;
        })
        .collect(Collectors.toList()));

    Platform.runLater(() -> ratingDistributionChart.getData().setAll(series));
  }

  private void plotDivisionDistributions(List<Division> divisions, Player player) {
    XYChart.Series<String, Integer> series = new XYChart.Series<>();
    series.setName("Players");

  }

//  private void plotDivisionDistributions(List<Division> divisions, Player player) {
//   // List<XYChart.Series> serieses = new List<XYChart.Series>();
//    for (Division division : divisions) {
//      if (division.getMajorDivisionIndex() == 1) {
//        XYChart.Series<String, Integer> series = new XYChart.Series<>();
//        series.setName(division.getSubDivisionName());
//      }
//      int playerCount = leaderboardService.getDivisionStats(division).thenAccept(divisionStats -> divisionStats.stream().)
//
//    }
//  }

  private void addNodeOnTopOfBar(XYChart.Data<String, Integer> data, Node nodeToAdd) {
    final Node node = data.getNode();
    node.parentProperty().addListener((ov, oldParent, parent) -> {
      if (parent == null) {
        return;
      }
      Group parentGroup = (Group) parent;
      ObservableList<Node> children = parentGroup.getChildren();
      if (!children.contains(nodeToAdd)) {
        children.add(nodeToAdd);
        nodeToAdd.setViewOrder(-0.5);
      }
    });

    JavaFxUtil.addListener(node.boundsInParentProperty(), (ov, oldBounds, bounds) -> {
      nodeToAdd.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - nodeToAdd.prefWidth(-1) / 2));
      nodeToAdd.setLayoutY(Math.round(bounds.getMaxY() - nodeToAdd.prefHeight(-1) * 0.5));
    });
  }

  @NotNull
  private StringConverter<Division> divisionStringConverter() {
    return new StringConverter<>() {
      @Override
      public String toString(Division division) {
        return division.getMajorDivisionName().toUpperCase();
        //return i18n.get(division.getMajorDivisionName().getI18nKey()).toUpperCase();
      }

      @Override
      public Division fromString(String string) {
        return null;
      }
    };
  }

  public void onMajorDivisionChanged(ActionEvent actionEvent) {
    subDivisionTabs.getTabs().clear();
    leaderboardService.getDivisions().thenAccept(divisions -> Platform.runLater(() -> {
      divisions.stream()
          .filter(division -> division.getMajorDivisionIndex() == majorDivisionPicker.getValue().getMajorDivisionIndex())
          .forEach(division -> {
            SubDivisionTabController controller = uiService.loadFxml("theme/leaderboard/subDivisionTab.fxml");
            controller.setDivision(division);
            //controller.setButtonText(i18n.get(division.getSubDivisionName().getI18nKey()).toUpperCase());
            controller.setTabText(division.getSubDivisionName());
            subDivisionTabs.getTabs().add(controller.getTab());
            subDivisionTabs.setTabMinWidth((subDivisionTabs.getWidth() / subDivisionTabs.getTabs().size()) - 100.0);
            subDivisionTabs.getSelectionModel().selectLast();
          });
    }));

  }
}
