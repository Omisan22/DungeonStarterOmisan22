package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.controller.ControllerImpl;
import edu.unc.comp301.dungeon.model.Model;
import edu.unc.comp301.dungeon.model.ModelImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/** Creates and connects the model, controller, and JavaFX view. */
public class AppLauncher extends Application {
  private static final int BOARD_WIDTH = 12;
  private static final int BOARD_HEIGHT = 9;
  private static final int WINDOW_WIDTH = 960;
  private static final int WINDOW_HEIGHT = 770;

  @Override
  public void start(Stage stage) {
    Model model = new ModelImpl(BOARD_WIDTH, BOARD_HEIGHT);
    Controller controller = new ControllerImpl(model);
    View view = new View(controller, model);

    Scene scene = new Scene(view.render(), WINDOW_WIDTH, WINDOW_HEIGHT);
    scene.getStylesheets().add(AppLauncher.class.getResource("/dungeon.css").toExternalForm());
    view.attachScene(scene);
    model.addObserver(view);

    // The event filter works even if a button has keyboard focus.
    scene.addEventFilter(
        KeyEvent.KEY_PRESSED,
        event -> {
          if (model.getStatus() != Model.STATUS.IN_PROGRESS) {
            return;
          }
          switch (event.getCode()) {
            case UP, W -> controller.moveUp();
            case DOWN, S -> controller.moveDown();
            case LEFT, A -> controller.moveLeft();
            case RIGHT, D -> controller.moveRight();
            default -> {
              return;
            }
          }
          event.consume();
        });

    stage.setTitle("Naomi's Dungeon Crawler");
    stage.setMinWidth(760);
    stage.setMinHeight(680);
    stage.setScene(scene);
    stage.show();
  }
}
