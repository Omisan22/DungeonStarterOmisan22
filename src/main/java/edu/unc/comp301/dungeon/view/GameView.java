package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.model.Model;
import edu.unc.comp301.dungeon.model.board.Posn;
import edu.unc.comp301.dungeon.model.pieces.Piece;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Play screen with game board, scores, and directional controls. */
public class GameView implements FXComponent {
  private static final int TILE_SIZE = 49;

  private final Model model;
  private final Controller controller;
  private final boolean darkTheme;
  private final Runnable toggleTheme;

  public GameView(Model model, Controller controller, boolean darkTheme, Runnable toggleTheme) {
    this.model = Objects.requireNonNull(model, "model");
    this.controller = Objects.requireNonNull(controller, "controller");
    this.darkTheme = darkTheme;
    this.toggleTheme = Objects.requireNonNull(toggleTheme, "toggleTheme");
  }

  @Override
  public Parent render() {
    BorderPane root = new BorderPane();
    root.getStyleClass().addAll("app-root", darkTheme ? "theme-dark" : "theme-light");
    root.setPadding(new Insets(18, 28, 22, 28));

    HBox header = new HBox(24);
    header.setAlignment(Pos.CENTER_LEFT);
    header.getStyleClass().add("game-header");
    Label heading = new Label("⚔  DUNGEON CRAWLER");
    heading.getStyleClass().add("header-title");
    Label level = new Label("LEVEL  " + model.getLevel());
    level.getStyleClass().add("stat-chip");
    Label score = new Label("SCORE  " + model.getCurScore());
    score.getStyleClass().add("stat-chip");
    Label best = new Label("BEST  " + model.getHighScore());
    best.getStyleClass().add("stat-chip");
    header.getChildren().addAll(heading, level, score, best);
    root.setTop(header);

    GridPane board = new GridPane();
    board.setAlignment(Pos.CENTER);
    board.setHgap(0);
    board.setVgap(0);
    board.getStyleClass().add("game-board");
    for (int row = 0; row < model.getHeight(); row++) {
      for (int col = 0; col < model.getWidth(); col++) {
        Piece piece = model.get(new Posn(row, col));
        board.add(makeTile(piece), col, row);
      }
    }
    ScrollPane scroller = new ScrollPane(board);
    scroller.setFitToHeight(true);
    scroller.setFitToWidth(true);
    scroller.setPannable(true);
    scroller.getStyleClass().add("board-scroller");
    BorderPane.setMargin(scroller, new Insets(16, 0, 12, 0));
    root.setCenter(scroller);

    HBox footer = new HBox(35);
    footer.setAlignment(Pos.CENTER);
    VBox instructions = new VBox(10);
    instructions.setAlignment(Pos.CENTER_LEFT);
    Label legend = new Label("🧙 Hero   👾 Enemy   💎 Treasure   🚪 Exit   🧱 Wall");
    legend.getStyleClass().add("legend");
    Label help = new Label("Use WASD / arrows or click the buttons to move.");
    help.getStyleClass().add("help-text");
    Button theme = new Button(darkTheme ? "☀  Light mode" : "☾  Dark mode");
    theme.getStyleClass().add("secondary-button");
    theme.setOnAction(event -> toggleTheme.run());
    instructions.getChildren().addAll(legend, help, theme);
    footer.getChildren().addAll(instructions, directionPad());
    root.setBottom(footer);
    return root;
  }

  private StackPane makeTile(Piece piece) {
    StackPane tile = new StackPane();
    tile.setMinSize(TILE_SIZE, TILE_SIZE);
    tile.setPrefSize(TILE_SIZE, TILE_SIZE);
    tile.setMaxSize(TILE_SIZE, TILE_SIZE);
    tile.getStyleClass().add("tile");

    String type = piece == null ? "empty" : piece.getClass().getSimpleName();
    String glyph = symbolFor(type);
    Label symbol = new Label(glyph);
    symbol.getStyleClass().add("tile-symbol");
    tile.getChildren().add(symbol);
    tile.getStyleClass().add("tile-" + type.toLowerCase());
    if (piece != null) {
      Tooltip.install(tile, new Tooltip(piece.getName()));
    }
    return tile;
  }

  private String symbolFor(String type) {
    return switch (type) {
      case "Hero" -> "🧙";
      case "Enemy" -> "👾";
      case "Treasure" -> "💎";
      case "Wall" -> "🧱";
      case "Exit" -> "🚪";
      case "empty" -> "";
      default -> "✦";
    };
  }

  private GridPane directionPad() {
    GridPane pad = new GridPane();
    pad.setAlignment(Pos.CENTER);
    pad.setHgap(8);
    pad.setVgap(8);
    pad.getStyleClass().add("direction-pad");

    pad.add(moveButton("↑", controller::moveUp), 1, 0);
    pad.add(moveButton("←", controller::moveLeft), 0, 1);
    pad.add(moveButton("↓", controller::moveDown), 1, 1);
    pad.add(moveButton("→", controller::moveRight), 2, 1);
    return pad;
  }

  private Button moveButton(String text, Runnable action) {
    Button button = new Button(text);
    button.getStyleClass().add("direction-button");
    button.setPrefSize(54, 46);
    button.setOnAction(event -> action.run());
    return button;
  }
}
