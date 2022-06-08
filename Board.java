
/**
 * Keeps track of the game board's state
 * 
 * TILE ID LIST:
 * 
 * 0: Water
 * 1: Plains/Sheep
 * 2: Forest
 * 3: Bricks
 * 4: Wheat
 * 5: Ore
 * 6: Sand
 */
import java.util.Random;

public class Board {

  // constants for the dimensions
  public final int GAME_ROWS = 5;

  // which tiles are where
  int[][] board = {
      { 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0 }
  };

  // which numbers are on each tile
  int[][] numbers = {
      { 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 0, 0, 0 }
  };

  // which settlement spaces are already filled with settlements
  int[][] settlements = {
      { 1, 1, 2, 2, 3, 3, 4 },
      { 2, 2, 3, 3, 4, 4, 1, 1, 2 },
      { 3, 3, 3, 4, 4, 4, 1, 1, 1, 2, 2 },
      { 4, 4, 4, 3, 3, 3, 2, 2, 2, 1, 1 },
      { 1, 1, 2, 2, 3, 3, 4, 4, 1 },
      { 2, 2, 3, 3, 4, 4, 1 }
  };

  // which road spaces are already filled
  int[][] roads = {
      { 0, 0, 0, 0, 0, 1 },
      { 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 0, 0 },
      { 2, 0, 0, 0, 0, 0 }
  };

  /**
   * Generate a new board
   */
  public Board() {
    // board dimensions are 3 then 4 then 5 then 4 then 3 tiles
    // generate a new board!
    generateBoard();
    // now place the numbers
    generateNumbers();

  }

  /**
   * Creates a board. Randomly fills it, but limits the count each tile can have
   */
  private void generateBoard() {

    // the count of how many of each tile are currently placed.
    int[] tilesPlaced = { 0, 0, 0, 0, 0, 0, 0 };
    int[] maxValues = { 0, 4, 4, 3, 4, 3, 1 };
    // create an instance of random for generation
    Random random = new Random();

    // now fill up each space
    for (int y = 0; y < board.length; y++) {

      for (int x = 0; x < board[y].length; x++) {

        // if we're at the centre tile, place the desert
        if (x == 2 && y == 2) {
          board[y][x] = 6;
        } else {

          // otherwise, loop until we generate a valid number to place
          int tileToPlace;
          do {

            // pick a random number between one and five (valid tiles to place)
            // 4 + 1 so we have between 1 and 5
            tileToPlace = random.nextInt(5) + 1;

            // then check if there's still those tiles left to be placed

          } while (tilesPlaced[tileToPlace] >= maxValues[tileToPlace]);

          // finally, we can set the tile and increment the counter
          board[y][x] = tileToPlace;
          tilesPlaced[tileToPlace]++;

        }

      }

    }

  }

  /**
   * Using the same process as generateBoard, fill each space with a roll number
   */
  private void generateNumbers() {

    // the count of how many of each number are currently placed.
    int[] numbersPlaced = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    int[] maxValues = { 0, 0, 1, 2, 2, 2, 2, 0, 2, 2, 2, 2, 1 };
    // create an instance of random for generation
    Random random = new Random();

    // now fill up each space
    for (int y = 0; y < numbers.length; y++) {

      for (int x = 0; x < numbers[y].length; x++) {

        // if we're at the centre tile, place the null tile
        if (x == 2 && y == 2) {
          numbers[y][x] = 0;
        } else {
          // loop until we generate a valid number to place
          int numberToPlace;
          do {

            // pick a random number between zero and 12 (valid tiles to place)
            // anything invalid will not be able to continue so we're good with this
            numberToPlace = random.nextInt(13);

            // then check if there's still those tiles left to be placed

          } while (numbersPlaced[numberToPlace] >= maxValues[numberToPlace]);

          // finally, we can set the number and increment the counter
          numbers[y][x] = numberToPlace;
          numbersPlaced[numberToPlace]++;

        }

      }

    }

  }

  /**
   * Get one row from the board
   * 
   * @param row The index of the row to get
   * @return The row
   */
  public int[] getRow(int row) {

    return board[row];

  }

}
