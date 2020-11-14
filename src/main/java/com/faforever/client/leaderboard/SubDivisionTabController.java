package com.faforever.client.leaderboard;

import com.faforever.client.fx.Controller;
import com.faforever.client.fx.StringCell;
import com.faforever.client.i18n.I18n;
import com.faforever.client.notification.ImmediateErrorNotification;
import com.faforever.client.notification.NotificationService;
import com.faforever.client.reporting.ReportingService;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

import static javafx.collections.FXCollections.observableList;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SubDivisionTabController implements Controller<Node> {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final LeaderboardService leaderboardService;
  private final NotificationService notificationService;
  private final I18n i18n;
  private final ReportingService reportingService;

  public Tab subDivisionTab;
  public TableColumn<LeaderboardEntry, Number> rankColumn;
  public TableColumn<LeaderboardEntry, String> nameColumn;
  public TableColumn<LeaderboardEntry, Number> gamesPlayedColumn;
  public TableColumn<LeaderboardEntry, Number> scoreColumn;
  public TableView<LeaderboardEntry> ratingTable;

  @Override
  // We can't return the tab, because it is not a node
  public Node getRoot() {
    return null;
  }

  @Override
  public void initialize() {
    rankColumn.setCellValueFactory(param -> param.getValue().rankProperty());
    rankColumn.setCellFactory(param -> new StringCell<>(rank -> i18n.number(rank.intValue())));

    nameColumn.setCellValueFactory(param -> param.getValue().usernameProperty());
    nameColumn.setCellFactory(param -> new StringCell<>(name -> name));

    gamesPlayedColumn.setCellValueFactory(param -> param.getValue().gamesPlayedProperty());
    gamesPlayedColumn.setCellFactory(param -> new StringCell<>(count -> i18n.number(count.intValue())));

    scoreColumn.setCellValueFactory(param -> param.getValue().ratingProperty());
    scoreColumn.setCellFactory(param -> new StringCell<>(rating -> i18n.number(rating.intValue())));
  }

  public Tab getTab() {
    return subDivisionTab;
  }

  public void populate(Division division) {
    subDivisionTab.setText(i18n.get(division.getSubDivisionName().getI18nKey()).toUpperCase());

    leaderboardService.getEntries(division).thenAccept(leaderboardEntryBeans -> {
      ratingTable.setItems(observableList(leaderboardEntryBeans));
    }).exceptionally(throwable -> {
      logger.warn("Error while loading leaderboard entries", throwable);
      notificationService.addNotification(new ImmediateErrorNotification(
          i18n.get("errorTitle"), i18n.get("leaderboard.failedToLoad"),
          throwable, i18n, reportingService
      ));
      return null;
    });
  }
}
