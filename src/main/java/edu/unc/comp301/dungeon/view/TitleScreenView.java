package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.model.Model;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Starting and game-over screen, including score history. */
public class TitleScreenView implements FXComponent {
  private final Model model;
  private final Controller controller;
  private final boolean darkTheme;
  private final Runnable toggleTheme;

  public TitleScreenView(
      Model model, Controller controller, boolean darkTheme, Runnable toggleTheme) {
    this.model = Objects.requireNonNull(model, "model");
    this.controller = Objects.requireNonNull(controller, "controller");
    this.darkTheme = darkTheme;
    this.toggleTheme = Objects.requireNonNull(toggleTheme, "toggleTheme");
  }

  @Override
  public Parent render() {
    StackPane root = new StackPane();
    root.getStyleClass().addAll("app-root", darkTheme ? "theme-dark" : "theme-light");

    VBox content = new VBox(18);
    content.setAlignment(Pos.CENTER);
    content.setMaxWidth(560);
    content.setPadding(new Insets(36));
    content.getStyleClass().add("title-card");

    Label crest = new Label("⚔  ✦  ♜  ✦  ⚔");
    crest.getStyleClass().add("crest");

    Label name = new Label("DUNGEON\nCRAWLER");
    name.setAlignment(Pos.CENTER);
    name.getStyleClass().add("game-title");

    Label subtitle = new Label("Explore. Collect treasure. Reach the exit.");
    subtitle.getStyleClass().add("subtitle");

    HBox scores = new HBox(20);
    scores.setAlignment(Pos.CENTER);
    Label best = new Label("BEST SCORE\n" + model.getHighScore());
    best.getStyleClass().add("score-card");
    Label last = new Label("LAST SCORE\n" + model.getCurScore());
    last.getStyleClass().add("score-card");
    scores.getChildren().addAll(best, last);

    Button start = new Button("▶  START ADVENTURE");
    start.getStyleClass().add("primary-button");
    start.setOnAction(event -> controller.startGame());

    Button theme = new Button(darkTheme ? "☀  Light mode" : "☾  Dark mode");
    theme.getStyleClass().add("secondary-button");
    theme.setOnAction(event -> toggleTheme.run());

    Label howTo = new Label("Move with arrow keys, WASD, or the on-screen controls");
    howTo.getStyleClass().add("help-text");
    howTo.setWrapText(true);

    Label signature = new Label("By Naomi");
    signature.getStyleClass().add("byline");

    content.getChildren().addAll(crest, name, subtitle, scores, start, theme, howTo, signature);
    root.getChildren().add(content);
    return root;
  }
}
