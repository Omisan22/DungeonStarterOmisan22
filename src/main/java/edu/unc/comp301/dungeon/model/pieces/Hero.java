package edu.unc.comp301.dungeon.model.pieces;

/** The player-controlled piece. */
public class Hero extends APiece implements MovablePiece {
  public Hero() {
    super("Hero", "images/hero.png");
  }

  @Override
  public CollisionResult collide(Piece other) {
    if (other instanceof Treasure treasure) {
      return new CollisionResult(treasure.getValue(), CollisionResult.Result.CONTINUE);
    }
    if (other instanceof Enemy) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }
    if (other instanceof Exit) {
      return new CollisionResult(0, CollisionResult.Result.NEXT_LEVEL);
    }
    return new CollisionResult(0, CollisionResult.Result.CONTINUE);
  }
}
