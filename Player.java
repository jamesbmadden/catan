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

  }

  public void add(int type, int count) {

    System.out.println("Adding " + count + " of " + type + " for whichever player this is lol");

    // depending on the resource's number, add it
    switch (type) {
      // 0 = water, undefined
      case 0:
        break;
      // 1 = plains, add sheep
      case 1:
        sheep++;
        break;
      // 2 = forest, add wood
      case 2:
        wood++;
        break;
      // 3 = bricks, add bricks
      case 3:
        bricks++;
        break;
      // 4 = wheat, add wheat
      case 4:
        wheat++;
        break;
      // 5 = ore, add ore
      case 5:
        ore++;
        break;
      // 6 = sand, undefined
      case 6:
        break;
    }

  }

}
