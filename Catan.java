import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.Timer;

public class Catan extends JPanel {

  // the frame we're using
  JFrame frame;

  // dimensions
  static int PWIDTH = 800;
  static int PHEIGHT = 800;

  static int FWIDTH = PWIDTH + 16;
  static int FHEIGHT = PHEIGHT + 38;

  // textures used for rendering
  // numbers correspond with tile type IDs, which can be found listed in
  // Board.java.
  Texture[] tileTextures;
  // and the number textures
  Texture[] numberTextures;
  // and the gradient textures
  Texture[] gradientTextures;
  // the road textures (which there's a lot of)
  Texture[][] roadTextures;
  // the settlement textures
  Texture[] settlementTextures;

  // get the size of the tiles, in pixels
  int tileWidth = 96;
  int tileHeight = (int) (1.15625 * tileWidth);
  // and the size of the numbers on top
  int numberDiameter = 48;

  // create a game board to play on
  Board board = new Board();
  // and the visual components
  Interface components;

  // the options for the game, including player count and points to win
  GameConfig config;

  // current game state stuff
  int currentTurn = 1;
  Player[] players;

  // create a new instance of the game
  public Catan(JFrame window) {

    super(new GridBagLayout());

    frame = window;
    // BEFORE ANYTHING ELSE, get the game configuration
    config = new GameConfig(window);

    // now that the configuration is set, we can initialize the players
    // plus one because the first index should be null
    players = new Player[config.playerCount];
    // actually initialize the players, otherwise it's just a null
    for (int i = 0; i < config.playerCount; i++) {
      players[i] = new Player();
    }

    // setup the UI
    components = new Interface(this);

    // attempt to load the textures. On fail, give up and quit the program
    try {

      // a list of textures. Matches up with the IDs listed in Board.java
      Texture[] _tileTextures = {
          new Texture("img/tiles/water.png"),
          new Texture("img/tiles/sheep.png"),
          new Texture("img/tiles/forest.png"),
          new Texture("img/tiles/bricks.png"),
          new Texture("img/tiles/wheat.png"),
          new Texture("img/tiles/ore.png"),
          new Texture("img/tiles/sand.png")
      };
      Texture[] _numberTextures = {
          new Texture("img/numbers/0.png"),
          null,
          new Texture("img/numbers/2.png"),
          new Texture("img/numbers/3.png"),
          new Texture("img/numbers/4.png"),
          new Texture("img/numbers/5.png"),
          new Texture("img/numbers/6.png"),
          null,
          new Texture("img/numbers/8.png"),
          new Texture("img/numbers/9.png"),
          new Texture("img/numbers/10.png"),
          new Texture("img/numbers/11.png"),
          new Texture("img/numbers/12.png")
      };
      Texture[] _gradientTextures = {
          new Texture("img/topgradient.png"),
          new Texture("img/bottomgradient.png")
      };
      Texture[][] _roadTextures = {
          { null, null, null },
          { new Texture("img/builds/road/p1/left.png"), new Texture("img/builds/road/p1/right.png"),
              new Texture("img/builds/road/p1/down.png") },
          { new Texture("img/builds/road/p2/left.png"), new Texture("img/builds/road/p2/right.png"),
              new Texture("img/builds/road/p2/down.png") },
          { new Texture("img/builds/road/p3/left.png"), new Texture("img/builds/road/p3/right.png"),
              new Texture("img/builds/road/p3/down.png") },
          { new Texture("img/builds/road/p4/left.png"), new Texture("img/builds/road/p4/right.png"),
              new Texture("img/builds/road/p4/down.png") }
      };
      Texture[] _settlementTextures = {
          null,
          new Texture("img/builds/settlement/p1.png"),
          new Texture("img/builds/settlement/p2.png"),
          new Texture("img/builds/settlement/p3.png"),
          new Texture("img/builds/settlement/p4.png")
      };
      // set this list of textures so it can be used
      tileTextures = _tileTextures;
      numberTextures = _numberTextures;
      gradientTextures = _gradientTextures;
      roadTextures = _roadTextures;
      settlementTextures = _settlementTextures;

    } catch (Exception error) {

      System.out.println("Failed to load textures. Are you running this program from the correct directory?");
      // quit
      System.exit(0);

    }

  }

  /**
   * Handle everything for switching to the next turn, including dice rolls.
   * Currently just increments the turn number
   */
  public void nextTurn() {

    // go to the next player's turn
    currentTurn = (currentTurn % config.playerCount) + 1;

  }

  /**
   * Open a dialogue for the position to build
   * 
   * @param mode The type of thing to build
   * @return the coordinates for the build
   */
  public void openBuildDialogue(int mode) {

    int[] buildCoords;

    // try to open a build dialogue to grab the coordinates for the build
    try {
      BuildDialogue dialogue = new BuildDialogue(frame, mode, board, currentTurn);
      buildCoords = dialogue.result;
    } catch (Exception error) {
      System.out.println("Failed! Just giving up");
      return;
    }

    // now we know the coordinates, call the specific function depending on the mode
    switch (mode) {

      // if mode = 0, building road
      case 0:
        board.roads[buildCoords[1]][buildCoords[0]] = currentTurn;
        players[currentTurn - 1].buildRoad(buildCoords[0], buildCoords[1]);
        break;

      // if mode - 1, building settlement
      case 1:
        board.roads[buildCoords[1]][buildCoords[0]] = currentTurn;
        players[currentTurn - 1].buildSettlement(buildCoords[0], buildCoords[1]);
        break;

    }

    // finally, close the popup buttons in the interface
    components.closePopupButtons();

  }

  /**
   * Render the game.
   */
  @Override
  public void paintComponent(Graphics gl) {
    super.paintComponent(gl);
    renderGameBoard(gl);
    renderGradients(gl);
  }

  public void renderGameBoard(Graphics gl) {

    // how many rows and columns can fit on the screen
    int rowsToRender = PHEIGHT / tileHeight * 2 + 1;
    int columnsToRender = PWIDTH / tileWidth + 1;

    // because we want the middle row to be perfectly aligned in the centre,
    // use that to figure out the starting x and y points
    int firstX = PWIDTH / 2 - (columnsToRender / 2 + 1) * tileWidth;
    int firstY = PHEIGHT / 2 - (rowsToRender / 3) * tileHeight;

    // now use that information to calculate where to render the actual game tiles
    // subtract the amount of rows to render so our result is the start not the
    // middle
    int startGameRow = (rowsToRender - board.GAME_ROWS) / 2;

    // loop through each row
    for (int y = -1; y <= rowsToRender; y++) {

      // adjust every other row's spacing
      int startX = firstX + (y % 2) * tileWidth / -2;

      // check whether this row contains game tiles or just decorative water
      boolean renderGameTiles = (y >= startGameRow) && (y < startGameRow + board.GAME_ROWS);
      // find the x position to start rendering game tiles, based on this row of the
      // game
      int[] gameRow = new int[0];
      int startGameColumn = 0;
      if (renderGameTiles) {
        gameRow = board.getRow(y - startGameRow);
        // if the row is adjusted, add 1 to keep it aligned
        startGameColumn = (columnsToRender - gameRow.length) / 2 + 1;
      }

      // loop through each column and draw the tile
      for (int x = 0; x <= columnsToRender; x++) {

        // check whether to draw a game tile or just to render water
        if (renderGameTiles && x >= startGameColumn && x < startGameColumn + gameRow.length) {

          // we are rendering, find which tile to render and do it
          int tileId = gameRow[x - startGameColumn];

          // finally, we can draw the tile
          gl.drawImage(tileTextures[tileId].img, startX + x * tileWidth, firstY + y * (int) (tileHeight / 1.5),
              tileWidth,
              tileHeight,
              null,
              null);

          // and now, in the middle of the tile, we can render the number on it
          int numStartX = startX + x * tileWidth + (tileWidth - numberDiameter) / 2;
          int numStartY = firstY + y * (int) (tileHeight / 1.5) + (tileHeight - numberDiameter) / 2;

          // finally, draw the number
          gl.drawImage(numberTextures[board.numbers[y - startGameRow][x - startGameColumn]].img, numStartX, numStartY,
              numberDiameter,
              numberDiameter,
              null,
              null);

        } else {

          // we're just rendering water
          gl.drawImage(tileTextures[0].img, startX + x * tileWidth, firstY + y * (int) (tileHeight / 1.5), tileWidth,
              tileHeight,
              null,
              null);
        }

        // and draw the roads
        // local x and y are relative to the island
        int localX = x - startGameColumn;
        int localY = y - startGameRow;

        if (y - startGameRow == 5) {
          startGameColumn = (columnsToRender - board.getRow(4).length) / 2 + 1;
        }

        renderRoad(gl, localX, localY, startX + x * tileWidth,
            firstY + y * (int) (tileHeight / 1.5));
        renderSettlement(gl, localX, localY, startX + x * tileWidth, firstY + y * (int) (tileHeight / 1.5));

      }

    }

  }

  /**
   * Draw the little roads onto the board
   */
  public void renderRoad(Graphics gl, int x, int y, int drawX, int drawY) {

    // okay lets get this over with :(
    // first off, define what type of road we need at each spot (left = 0, right =
    // 1, down = 2)
    int[][] typeOfRoad = {
        { 0, 1, 0, 1, 0, 1 },
        { 2, 2, 2, 2 },
        { 0, 1, 0, 1, 0, 1, 0, 1 },
        { 2, 2, 2, 2, 2 },
        { 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 },
        { 2, 2, 2, 2, 2, 2 },
        { 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 },
        { 2, 2, 2, 2, 2 },
        { 1, 0, 1, 0, 1, 0, 1, 0 },
        { 2, 2, 2, 2 },
        { 1, 0, 1, 0, 1, 0 }
    };
    // and the road dimensions, based off of tile dimsnions
    int[][] roadDimensions = {
        { tileWidth / 2, tileHeight / 4 },
        { tileWidth / 2, tileHeight / 4 },
        { tileWidth / 10, tileHeight / 2 }
    };
    // finally, the offset based on tile position for the roads
    int[][] roadOffset = {
        { 0, 0 },
        { tileWidth / 2, 0 },
        { -tileWidth / 20, tileHeight / 4 }
    };

    // find coordinates for rendering the roads
    int roadY = y * 2;
    int roadX = x * 2;

    // IF we're within the road dimensions, render
    if (0 <= roadY && roadY < typeOfRoad.length && 0 <= roadX && roadX < typeOfRoad[roadY].length) {

      // render first row of roads: that's the wide one two on top per tile
      int roadColour = board.roads[roadY][roadX];
      int roadType = typeOfRoad[roadY][roadX];

      // if we're on the bottom half, offset this type of road by -1 tiles to make
      // sure it renders in the right place
      if (roadType == 1 && y > 2) {
        drawX -= tileWidth;
      }

      // check if there's actually a road there
      if (roadColour > 0) {
        // draw it according to all the properties defined earlier
        // roadTextures goes player number, then road type (defined in typeOfRoad above)
        gl.drawImage(roadTextures[roadColour][roadType].img, drawX + roadOffset[roadType][0],
            drawY + roadOffset[roadType][1], roadDimensions[roadType][0],
            roadDimensions[roadType][1],
            null,
            null);

      }

      // set the drawX back to what it was
      if (roadType == 1 && y > 2) {
        drawX += tileWidth;
      }
    }

    // same thing but x plus one!
    roadX++;

    // check to make sure this road is real too
    if (0 <= roadY && roadY < typeOfRoad.length && 0 <= roadX && roadX < typeOfRoad[roadY].length) {
      int roadColour = board.roads[roadY][roadX];
      int roadType = typeOfRoad[roadY][roadX];

      // check if there's actually a road there
      if (roadColour > 0) {
        // draw it according to all the properties defined earlier
        // roadTextures goes player number, then road type (defined in typeOfRoad above)
        gl.drawImage(roadTextures[roadColour][roadType].img, drawX + roadOffset[roadType][0],
            drawY + roadOffset[roadType][1], roadDimensions[roadType][0],
            roadDimensions[roadType][1],
            null,
            null);

      }
    }

    // FINALLY, the one on the next line
    roadY++;
    roadX = x;

    if (0 <= roadY && roadY < typeOfRoad.length && 0 <= roadX && roadX < typeOfRoad[roadY].length) {
      int roadColour = board.roads[roadY][roadX];
      int roadType = typeOfRoad[roadY][roadX];

      // check if there's actually a road there
      if (roadColour > 0) {
        // draw it according to all the properties defined earlier
        // roadTextures goes player number, then road type (defined in typeOfRoad above)
        gl.drawImage(roadTextures[roadColour][roadType].img, drawX + roadOffset[roadType][0],
            drawY + roadOffset[roadType][1], roadDimensions[roadType][0],
            roadDimensions[roadType][1],
            null,
            null);

      }

    }

  }

  /**
   * here we go again :(
   */
  public void renderSettlement(Graphics gl, int x, int y, int drawX, int drawY) {

    // ok so each tile should draw the top-left and top settlements
    // lets find those coordinates
    int settlementX = x * 2;
    int settlementY = y;

    // check whether we're within the settlement realm
    if (settlementY >= 0 && settlementY < 6 && settlementX >= 0
        && settlementX < board.settlements[settlementY].length) {

      // adjust x position on the lower half of the board to make sure everything
      // aligns properly
      if (y > 2) {
        settlementX++;
        if (settlementX == board.settlements[settlementY].length) {
          return;
        }
      }

      // grab the first settlement
      int settlementType = board.settlements[settlementY][settlementX];

      // if it's not empty, render!
      if (settlementType != 0) {

        // this is the FIRST settlement, so render using the left coordinates
        int startX = -10;
        int startY = 13;

        gl.drawImage(settlementTextures[settlementType].img, drawX + startX,
            drawY + startY, 20,
            26,
            null,
            null);
      }

      settlementX++;
      if (settlementX < board.settlements[settlementY].length) {

        // it exists, so check the settlement type
        settlementType = board.settlements[settlementY][settlementX];
        if (settlementType != 0) {

          // this is the TOP settlement, so render using the top coordinates
          int startX = tileWidth / 2 - 10;
          int startY = -13;

          gl.drawImage(settlementTextures[settlementType].img, drawX + startX,
              drawY + startY, 20,
              26,
              null,
              null);

        }

      }

      // OR, if we're at -1 on the x but it's the second half of the board, we should
      // render the first one
    } else if (y > 2 && y < board.settlements.length && x == -1) {

      // it exists, so check the settlement type
      int settlementType = board.settlements[settlementY][0];
      if (settlementType != 0) {

        // this is the TOP settlement, so render using the top coordinates
        int startX = tileWidth / 2 - 10;
        int startY = -13;

        gl.drawImage(settlementTextures[settlementType].img, drawX + startX,
            drawY + startY, 20,
            26,
            null,
            null);

      }

    }

  }

  /**
   * Render the gradients at the top and bottom of the screen for readability
   */
  public void renderGradients(Graphics gl) {

    // calculate the height of the gradient
    int gradHeight = (int) (PWIDTH * 0.237954);

    // now draw it
    gl.drawImage(gradientTextures[0].img, 0, 0, PWIDTH, gradHeight,
        null,
        null);
    gl.drawImage(gradientTextures[1].img, 0, PHEIGHT - gradHeight, PWIDTH, gradHeight,
        null,
        null);

  }

  public static void main(String[] args) {

    JFrame window = new JFrame("Catan");

    Catan panel = new Catan(window);
    panel.setOpaque(true);
    panel.setBackground(new Color(246, 242, 238));

    window.setContentPane(panel);
    // and size
    window.setSize(FWIDTH, FHEIGHT);
    // make sure the program ends on close
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // and open the window
    window.setVisible(true);

    // when the window resizes, change Catan's properties
    Timer timer = new Timer(0, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        Dimension size = window.getBounds().getSize();
        FWIDTH = size.width;
        FHEIGHT = size.height;
        PWIDTH = size.width - 16;
        PHEIGHT = size.height - 38;
        panel.revalidate();
        panel.repaint();
      }
    });

    timer.setRepeats(true);
    // Aprox. 60 FPS
    timer.setDelay(17);
    timer.start();

  }

}
