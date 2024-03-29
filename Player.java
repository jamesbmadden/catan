/**
 * This class represents a player. It keeps track of their settlements,
 * resources, etc.
 * On a roll, it should add the resources they gain.
 */

public class Player {

  // resources
  int wood = 0;
  int bricks = 0;
  int wheat = 0;
  int ore = 0;
  int sheep = 0;

  // keep track of how many points this player has
  int score = 0;
  
  Catan parent;

  // coordinates of the latest build
  int[] latestBuild = { -1, -1 };

  // which spaces this player has settlements
  // 1 is a settlement, 2 is a city
  int[][] settlements = {
      { 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0 }
  };

  // where the player has roads
  boolean[][] roads = {
      { false, false, false, false, false, false },
      { false, false, false, false },
      { false, false, false, false, false, false, false, false },
      { false, false, false, false, false },
      { false, false, false, false, false, false, false, false, false, false },
      { false, false, false, false, false, false },
      { false, false, false, false, false, false, false, false, false, false },
      { false, false, false, false, false },
      { false, false, false, false, false, false, false, false },
      { false, false, false, false },
      { false, false, false, false, false, false }
  };

  public Player (Catan catan) {

    parent = catan;

  }

  /**
   * Check what types of things this player is able to build
   * The order is:
   * [ road, settlement, city, resource card IF THOSE GET IMPLEMENTED ]
   */
  public boolean[] canBuild() {

    boolean[] canBeBuilt = { false, false, false, false };

    // check if a road can be built
    if (bricks >= 1 && wood >= 1) {
      canBeBuilt[0] = true;
    }
    // settlement
    if (bricks >= 1 && wood >= 1 && sheep >= 1 && wheat >= 1) {
      canBeBuilt[1] = true;
    }
    // city
    if (wheat >= 2 && ore >= 3) {
      canBeBuilt[2] = true;
    }
    // development card
    if (ore >= 1 && wheat >= 1 && sheep >= 1) {
      canBeBuilt[3] = true;
    }

    return canBeBuilt;

  }

  /**
   * Build a road at the specified coordinates, and remove resources if not at the start of the game.
   * @param x position to build
   * @param y position to build
   * @param free whether to use resources
   */
  public void buildRoad(int x, int y, boolean free) {

    // set the coordinate in roads
    roads[y][x] = true;
    latestBuild = new int[] { x, y };
    // and then subtract the resources IF this build isn't free
    if (!free) {
      bricks--;
      wood--;
    }

  }

  /**
   * Build a settlement at the specified coordinates, and remove resources if not at the start of the game.
   * @param x position to build
   * @param y position to build
   * @param free whether to use resources
   */
  public void buildSettlement(int x, int y, boolean free) {

    // set the coordinate in roads
    settlements[y][x] = 1;
    latestBuild = new int[] { x, y };
    // and then subtract the resources
    if (!free) {
      bricks--;
      wood--;
      wheat--;
      sheep--;
    }

    // you get a point!
    incrementScore();

  }

    /**
   * Build a city at the specified coordinates.
   * @param x position to build
   * @param y position to build
   */
  public void buildCity(int x, int y) {

    settlements[y][x] = 2;
    latestBuild = new int[] { x, y };

    wheat -= 2;
    ore -= 3;

    // you get a point!
    incrementScore();

  }

  /**
   * increment the resource of whichever type.
   * @param type an integer representing a different type of tile.
   * @param count the amount of that resource to add
   */
  public void add(int type, int count) {

    // depending on the resource's number, add it
    switch (type) {
      // 0 = water, undefined
      case 0:
        break;
      // 1 = plains, add sheep
      case 1:
        sheep += count;
        break;
      // 2 = forest, add wood
      case 2:
        wood += count;
        break;
      // 3 = bricks, add bricks
      case 3:
        bricks += count;
        break;
      // 4 = wheat, add wheat
      case 4:
        wheat += count;
        break;
      // 5 = ore, add ore
      case 5:
        ore += count;
        break;
      // 6 = sand, undefined
      case 6:
        break;
    }

  }

  /**
   * Increase the player's score, and check if they've won!
   */
  public void incrementScore () {

    score++;
    
    if (score >= parent.config.pointsToWin) {
      parent.victory();
    }

  }

}
