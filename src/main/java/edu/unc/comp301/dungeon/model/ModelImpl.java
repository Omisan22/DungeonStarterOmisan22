package edu.unc.comp301.dungeon.model;

import edu.unc.comp301.dungeon.model.board.Board;
import edu.unc.comp301.dungeon.model.board.BoardImpl;
import edu.unc.comp301.dungeon.model.board.Posn;
import edu.unc.comp301.dungeon.model.pieces.CollisionResult;
import edu.unc.comp301.dungeon.model.pieces.Piece;
import java.util.ArrayList;
import java.util.List;

/** Stores the score, levels, game status, and model observers. */
public class ModelImpl implements Model {
  private final Board board;
  private final List<Observer> observers;
  private int curScore;
  private int highScore;
  private int level;
  private STATUS status;

  public ModelImpl(int width, int height) {
    this(new BoardImpl(width, height));
  }

  public ModelImpl(Board board) {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    this.board = board;
    this.curScore = 0;
    this.highScore = 0;
    this.level = 0;
    this.status = STATUS.END_GAME;
    this.observers = new ArrayList<>();
  }

  @Override
  public int getWidth() { return board.getWidth(); }

  @Override
  public int getHeight() { return board.getHeight(); }

  @Override
  public Piece get(Posn p) { return board.get(p); }

  @Override
  public int getCurScore() { return curScore; }

  @Override
  public int getHighScore() { return highScore; }

  @Override
  public int getLevel() { return level; }

  @Override
  public STATUS getStatus() { return status; }

  /** Each level contains level + 1 enemies, two treasures, two walls, a hero, an exit. */
  private boolean initializeLevel() {
    try {
      board.init(level + 1, 2, 2);
      return true;
    } catch (IllegalArgumentException notEnoughSpace) {
      return false;
    }
  }

  @Override
  public void startGame() {
    curScore = 0;
    level = 1;
    status = STATUS.IN_PROGRESS;
    if (!initializeLevel()) {
      finishGame();
    }
    notifyObservers();
  }

  private void finishGame() {
    status = STATUS.END_GAME;
    highScore = Math.max(highScore, curScore);
  }

  @Override
  public void endGame() {
    finishGame();
    notifyObservers();
  }

  /** Apply the points and outcome of a single board turn. */
  private void move(int drow, int dcol) {
    if (status != STATUS.IN_PROGRESS) {
      return;
    }
    CollisionResult result = board.moveHero(drow, dcol);
    curScore += result.getPoints();

    switch (result.getResults()) {
      case GAME_OVER -> finishGame();
      case NEXT_LEVEL -> {
        level++;
        if (!initializeLevel()) {
          finishGame();
        }
      }
      case CONTINUE -> { /* Keep playing. */ }
    }
    notifyObservers();
  }

  @Override
  public void moveUp() { move(-1, 0); }

  @Override
  public void moveDown() { move(1, 0); }

  @Override
  public void moveLeft() { move(0, -1); }

  @Override
  public void moveRight() { move(0, 1); }

  @Override
  public void addObserver(Observer o) {
    if (o == null) {
      throw new IllegalArgumentException("Observer cannot be null");
    }
    observers.add(o);
  }

  private void notifyObservers() {
    // Iterate on a copy in case an observer subscribes a new observer in update().
    for (Observer observer : new ArrayList<>(observers)) {
      observer.update();
    }
  }
}
