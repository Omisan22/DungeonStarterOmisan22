package edu.unc.comp301.dungeon.view;

import edu.unc.comp301.dungeon.controller.Controller;
import edu.unc.comp301.dungeon.model.Model;
import edu.unc.comp301.dungeon.model.Observer;
import java.util.Objects;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;

/** Chooses the title/game screen and refreshes it when the model changes. */
public class View implements FXComponent, Observer {
  private final Controller controller;
  private final Model model;
  private Scene scene;
  private boolean darkTheme = true;

  public View(Controller controller, Model model) {
    this.controller = Objects.requireNonNull(controller, "controller");
    this.model = Objects.requireNonNull(model, "model");
  }

  /** Retain the scene so its root can be replaced after observer notifications. */
  public void attachScene(Scene scene) {
    this.scene = Objects.requireNonNull(scene, "scene");
  }

  private void toggleTheme() {
    darkTheme = !darkTheme;
    update();
  }

  @Override
  public Parent render() {
    if (model.getStatus() == Model.STATUS.IN_PROGRESS) {
      return new GameView(model, controller, darkTheme, this::toggleTheme).render();
    }
    return new TitleScreenView(model, controller, darkTheme, this::toggleTheme).render();
  }

  @Override
  public void update() {
    if (scene == null) {
      return;
    }
    if (Platform.isFxApplicationThread()) {
      scene.setRoot(render());
    } else {
      Platform.runLater(() -> scene.setRoot(render()));
    }
  }
}
