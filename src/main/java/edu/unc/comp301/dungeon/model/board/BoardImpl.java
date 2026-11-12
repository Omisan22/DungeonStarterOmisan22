package edu.unc.comp301.dungeon.model.board;

import edu.unc.comp301.dungeon.model.pieces.CollisionResult;
import edu.unc.comp301.dungeon.model.pieces.Enemy;
import edu.unc.comp301.dungeon.model.pieces.Exit;
import edu.unc.comp301.dungeon.model.pieces.Hero;
import edu.unc.comp301.dungeon.model.pieces.Piece;
import edu.unc.comp301.dungeon.model.pieces.Treasure;
import edu.unc.comp301.dungeon.model.pieces.Wall;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Game board: rows are vertical; columns are horizontal. */
public class BoardImpl implements Board {
  private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
  private final Piece[][] board;
  private Hero hero;

  public BoardImpl(int width, int height) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Width and height must be positive");
    }
    board = new Piece[height][width];
  }

  public BoardImpl(Piece[][] pieces) {
    if (pieces == null || pieces.length == 0 || pieces[0] == null || pieces[0].length == 0) {
      throw new IllegalArgumentException("Board must be a nonempty rectangular array");
    }
    int width = pieces[0].length;
    for (Piece[] row : pieces) {
      if (row == null || row.length != width) {
        throw new IllegalArgumentException("Board must be rectangular");
      }
    }
    board = pieces;
    for (int row = 0; row < getHeight(); row++) {
      for (int col = 0; col < getWidth(); col++) {
        Piece p = board[row][col];
        if (p != null) {
          p.setPosn(new Posn(row, col));
          if (p instanceof Hero h) {
            hero = h;
          }
        }
      }
    }
  }

  @Override
  public int getWidth() {
    return board[0].length;
  }

  @Override
  public int getHeight() {
    return board.length;
  }

  private boolean inBounds(int row, int col) {
    return row >= 0 && row < getHeight() && col >= 0 && col < getWidth();
  }

  @Override
  public Piece get(Posn posn) {
    if (posn == null || !inBounds(posn.getRow(), posn.getCol())) {
      return null;
    }
    return board[posn.getRow()][posn.getCol()];
  }

  /**
   * Moves a piece into the given square, clearing any old reference to that same piece. Passing
   * null clears the destination square.
   */
  @Override
  public void set(Piece p, Posn newPos) {
    if (newPos == null || !inBounds(newPos.getRow(), newPos.getCol())) {
      throw new IllegalArgumentException("Position is outside the board");
    }
    if (p != null && p.getPosn() != null) {
      Posn oldPos = p.getPosn();
      if (inBounds(oldPos.getRow(), oldPos.getCol())
          && board[oldPos.getRow()][oldPos.getCol()] == p) {
        board[oldPos.getRow()][oldPos.getCol()] = null;
      }
    }

    Piece replaced = get(newPos);
    if (replaced != null && replaced != p) {
      replaced.setPosn(null);
      if (replaced == hero) {
        hero = null;
      }
    }

    board[newPos.getRow()][newPos.getCol()] = p;
    if (p != null) {
      p.setPosn(newPos);
      if (p instanceof Hero h) {
        hero = h;
      }
    }
  }

  @Override
  public void init(int enemies, int treasures, int walls) {
    long required = (long) enemies + treasures + walls + 2; // hero and exit
    if (enemies < 0 || treasures < 0 || walls < 0 || required > (long) getWidth() * getHeight()) {
      throw new IllegalArgumentException("Not enough board squares for all pieces");
    }

    // Clear old positions before creating a new level.
    for (int row = 0; row < getHeight(); row++) {
      for (int col = 0; col < getWidth(); col++) {
        if (board[row][col] != null) {
          board[row][col].setPosn(null);
          board[row][col] = null;
        }
      }
    }
    hero = null;

    List<Posn> emptyPositions = new ArrayList<>();
    for (int row = 0; row < getHeight(); row++) {
      for (int col = 0; col < getWidth(); col++) {
        emptyPositions.add(new Posn(row, col));
      }
    }
    Collections.shuffle(emptyPositions);
    int next = 0;
    set(new Hero(), emptyPositions.get(next++));
    set(new Exit(), emptyPositions.get(next++));
    for (int i = 0; i < enemies; i++) {
      set(new Enemy(), emptyPositions.get(next++));
    }
    for (int i = 0; i < treasures; i++) {
      set(new Treasure(), emptyPositions.get(next++));
    }
    for (int i = 0; i < walls; i++) {
      set(new Wall(), emptyPositions.get(next++));
    }
  }

  private List<Enemy> enemiesOnBoard() {
    List<Enemy> enemies = new ArrayList<>();
    for (Piece[] row : board) {
      for (Piece p : row) {
        if (p instanceof Enemy enemy) {
          enemies.add(enemy);
        }
      }
    }
    return enemies;
  }

  private List<Posn> legalEnemyMoves(Enemy enemy) {
    List<Posn> moves = new ArrayList<>();
    Posn current = enemy.getPosn();
    for (int[] direction : DIRECTIONS) {
      int newRow = current.getRow() + direction[0];
      int newCol = current.getCol() + direction[1];
      if (!inBounds(newRow, newCol)) {
        continue;
      }
      Piece target = board[newRow][newCol];
      if (!(target instanceof Wall) && !(target instanceof Exit) && !(target instanceof Enemy)) {
        moves.add(new Posn(newRow, newCol));
      }
    }
    return moves;
  }

  @Override
  public CollisionResult moveHero(int drow, int dcol) {
    CollisionResult unchanged = new CollisionResult(0, CollisionResult.Result.CONTINUE);
    if (hero == null || hero.getPosn() == null) {
      return unchanged;
    }
    // Only a single orthogonal step is allowed.
    if (Math.abs(drow) + Math.abs(dcol) != 1) {
      return unchanged;
    }
    Posn oldPos = hero.getPosn();
    int newRow = oldPos.getRow() + drow;
    int newCol = oldPos.getCol() + dcol;
    if (!inBounds(newRow, newCol) || board[newRow][newCol] instanceof Wall) {
      return unchanged; // Illegal player turns do not move enemies.
    }

    Posn nextPos = new Posn(newRow, newCol);
    CollisionResult playerHit = hero.collide(get(nextPos));
    set(hero, nextPos);
    int totalPoints = playerHit.getPoints();
    if (playerHit.getResults() != CollisionResult.Result.CONTINUE) {
      return playerHit; // Reaching exit or dying ends this turn immediately.
    }

    // Take a snapshot: each enemy moves exactly once in this turn.
    for (Enemy enemy : enemiesOnBoard()) {
      if (enemy.getPosn() == null) {
        continue;
      }
      List<Posn> options = legalEnemyMoves(enemy);
      if (options.isEmpty()) {
        continue; // Trapped enemies skip their turn.
      }
      Posn destination = options.get((int) (Math.random() * options.size()));
      CollisionResult enemyHit = enemy.collide(get(destination));
      set(enemy, destination);
      totalPoints += enemyHit.getPoints();
      if (enemyHit.getResults() == CollisionResult.Result.GAME_OVER) {
        return new CollisionResult(totalPoints, CollisionResult.Result.GAME_OVER);
      }
    }
    return new CollisionResult(totalPoints, CollisionResult.Result.CONTINUE);
  }
}
