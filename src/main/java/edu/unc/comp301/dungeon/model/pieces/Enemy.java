package edu.unc.comp301.dungeon.model.pieces;

/** An enemy that can collect treasure and defeat the hero. */
public class Enemy extends APiece implements MovablePiece {
  public Enemy() {
    super("Enemy", "images/enemy.png");
  }

  @Override
  public CollisionResult collide(Piece other) {
    if (other instanceof Hero) {
      return new CollisionResult(0, CollisionResult.Result.GAME_OVER);
    }
    // Enemies remove treasure without adding to the player's score.
    return new CollisionResult(0, CollisionResult.Result.CONTINUE);
  }
}
