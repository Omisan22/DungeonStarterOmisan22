package edu.unc.comp301.dungeon.controller;

import edu.unc.comp301.dungeon.model.Model;
import java.util.Objects;

/** Relays player actions to the model without storing game logic. */
public class ControllerImpl implements Controller {
  private final Model model;

  public ControllerImpl(Model model) {
    this.model = Objects.requireNonNull(model, "model");
  }

  @Override
  public void moveUp() {
    model.moveUp();
  }

  @Override
  public void moveDown() {
    model.moveDown();
  }

  @Override
  public void moveLeft() {
    model.moveLeft();
  }

  @Override
  public void moveRight() {
    model.moveRight();
  }

  @Override
  public void startGame() {
    model.startGame();
  }
}
