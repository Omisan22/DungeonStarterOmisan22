package edu.unc.comp301.dungeon.model.pieces;

/** A collectible worth ten points (change this value if desired). */
public class Treasure extends APiece {
  private static final int VALUE = 10;

  public Treasure() {
    super("Treasure", "images/treasure.png");
  }

  public int getValue() {
    return VALUE;
  }
}
