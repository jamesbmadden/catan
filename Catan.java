import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
  // the city textures
  Texture[] cityTextures;
  // rober texture
  Texture robberTexture;

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

  // location of the robber
  int[] robberLocation = { 2, 2 };

  // an array of colours for each player, that is used for any graphical interface
  // elements (like player turn title)
  // these match the colours used in the textures for roads and settlements
  Color[] playerColours = {
      new Color(229, 57, 53),
      new Color(142, 36, 170),
      new Color(0, 137, 123),
      new Color(109, 76, 65)
  };

  // create a new instance of the game
  public Catan(JFrame window) {

    super(new GridBagLayout());

    frame = window;

    // show a welcome to catan message!
    JOptionPane.showMessageDialog(this, "Welcome to Catan!\nThe goal of Catan is to build settlements and cities" +
        " in order to accumulate points. \nA settlement is worth one point, and upgrading it to a city gives you another.\n"
        +
        " Settlements must be spaced out by two spaces and always connected by roads.\n" +
        " At the start of the game, each player gets to build two settlements for free,\n and from then on must earn" +
        " resources either by recieving them from your settlements or trading with other players.\n" +
        " You recieve resources from your settlements when the number on any adjacent tile is rolled.\n You recieve" +
        " one of the resource from a settlement, and two from a city.\n" +
        " If a seven is rolled, the 'robber' is moved to cover a tile and prevent earning any resources from that tile.\n\n"
        +
        " Good luck!");

    // BEFORE ANYTHING ELSE, get the game configuration
    config = new GameConfig(window);

    // now that the configuration is set, we can initialize the players
    // plus one because the first index should be null
    players = new Player[config.playerCount];
    // actually initialize the players, otherwise it's just a null
    for (int i = 0; i < config.playerCount; i++) {
      players[i] = new Player(this);
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
      Texture[] _cityTextures = {
        null,
        new Texture("img/builds/city/p1.png"),
        new Texture("img/builds/city/p2.png"),
        new Texture("img/builds/city/p3.png"),
        new Texture("img/builds/city/p4.png")
      };
      // set this list of textures so it can be used
      tileTextures = _tileTextures;
      numberTextures = _numberTextures;
      gradientTextures = _gradientTextures;
      roadTextures = _roadTextures;
      settlementTextures = _settlementTextures;
      cityTextures = _cityTextures;
      robberTexture = new Texture("img/robber.png");

    } catch (Exception error) {

      System.out.println("Failed to load textures. Are you running this program from the correct directory?");
      // quit
      System.exit(0);

    }

    // the window doesn't show if initGame is run right away, so use a timer to
    // delay it slightly
    Timer timer = new Timer(50, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {

        // now, initialize the game with the process of building the first two
        // settlements per player
        initGame();

      }
    });
    timer.setRepeats(false);
    timer.start();

  }

  /**
   * Handle everything for switching to the next turn, including dice rolls.
   * Currently just increments the turn number
   */
  public void nextTurn() {

    // go to the next player's turn
    currentTurn = (currentTurn % config.playerCount) + 1;

    // update the interface to show the new player in the header before the roll
    components.update();

    // now roll the die again
    roll();

    // update the interface to reflect the new turn
    components.update();

  }

  /**
   * Move to the next turn by rolling the die
   */
  public void roll() {

    Random random = new Random();
    // create the rolls
    int roll1 = random.nextInt(6) + 1;
    int roll2 = random.nextInt(6) + 1;

    int total = roll1 + roll2;

    JOptionPane.showMessageDialog(this, "You rolled " + roll1 + " and " + roll2 + ", totalling " + total);

    // if the roll is a 7, move the robber
    if (total == 7) {
      openBuildDialogue(5);
      // seven isn't a valid number for resources, so we can stop now
      return;
    }

    // find each tile with this number so resources can be added
    for (int y = 0; y < board.numbers.length; y++) {
      for (int x = 0; x < board.numbers[y].length; x++) {

        // check if the number equals the roll
        if (board.numbers[y][x] == total) {

          // if the robber is on this tile, DO NOT increment resources.
          if (robberLocation[0] == x && robberLocation[1] == y) {
            return;
          }

          // check if there are settlements bordering this tile and if so, increment their
          // resources
          // the first settlement is offset differently depending on whether it's the top
          // half or bottom half of the board, so adjust for that
          int offset = 0;
          if (y > 2) {
            offset = 1;
          }
          // first settlement: top left
          int settlement = board.settlements[y][x * 2 + offset];
          if (settlement != 0) {
            // player owning that settlement needs the appropriate resource incremented
            players[settlement - 1].add(board.board[y][x], 1);
          }
          // top
          settlement = board.settlements[y][x * 2 + 1 + offset];
          if (settlement != 0) {
            // player owning that settlement needs the appropriate resource incremented
            players[settlement - 1].add(board.board[y][x], 1);
          }
          // top right
          settlement = board.settlements[y][x * 2 + 2 + offset];
          if (settlement != 0) {
            // player owning that settlement needs the appropriate resource incremented
            players[settlement - 1].add(board.board[y][x], 1);
          }
          // now the row below
          // the first settlement is offset differently depending on whether it's the top
          // half or bottom half of the board, so adjust for that
          offset = 1;
          if (y > 1) {
            offset = 0;
          }

          // bottom left
          settlement = board.settlements[y + 1][x * 2 + offset];
          if (settlement != 0) {
            // player owning that settlement needs the appropriate resource incremented
            players[settlement - 1].add(board.board[y][x], 1);
          }
          // bottom
          settlement = board.settlements[y + 1][x * 2 + offset + 1];
          if (settlement != 0) {
            // player owning that settlement needs the appropriate resource incremented
            players[settlement - 1].add(board.board[y][x], 1);
          }
          // bottom right
          settlement = board.settlements[y + 1][x * 2 + offset + 2];
          if (settlement != 0) {
            // player owning that settlement needs the appropriate resource incremented
            players[settlement - 1].add(board.board[y][x], 1);
          }

        }

      }
    }

  }

  /**
   * When the player moves a robber, steal a random resource from an adjacent
   * settlement.
   */
  public void steal(int x, int y) {

    // first get a list of all the adjacent settlements
    // same process for settlement coords as above
    int bottomOffset = 1;
    if (y > 1) {
      bottomOffset = 0;
    }

    int topOffset = 0;
    if (y > 2) {
      topOffset = 1;
    }

    int[][] settlementOptions = {
        { x * 2 + topOffset, y },
        { x * 2 + 1 + topOffset, y },
        { x * 2 + 2 + topOffset, y },
        { x * 2 + bottomOffset, y + 1 },
        { x * 2 + 1 + bottomOffset, y + 1 },
        { x * 2 + 2 + bottomOffset, y + 1 },

    };

    // catchall if none of the settlements are built
    if (board.settlements[settlementOptions[0][1]][settlementOptions[0][0]] == 0 &&
        board.settlements[settlementOptions[1][1]][settlementOptions[1][0]] == 0 &&
        board.settlements[settlementOptions[2][1]][settlementOptions[2][0]] == 0 &&
        board.settlements[settlementOptions[3][1]][settlementOptions[3][0]] == 0 &&
        board.settlements[settlementOptions[4][1]][settlementOptions[4][0]] == 0 &&
        board.settlements[settlementOptions[5][1]][settlementOptions[5][0]] == 0) {

      // nothing to steal, return :(
      JOptionPane.showMessageDialog(this,
          "The robber found no settlement to steal from.");
      return;

    }

    // now that we have that list, randomly pick one. If it is null (no settlement),
    // pick again.
    Random random = new Random();
    int settlement = random.nextInt(6);
    while (board.settlements[settlementOptions[settlement][1]][settlementOptions[settlement][0]] == 0) {
      settlement = random.nextInt(6);
    }

    // player that we're stealing from
    int player = board.settlements[settlementOptions[settlement][1]][settlementOptions[settlement][0]];
    Player playerClass = players[player - 1];

    // if the player has no resources, end it here
    if (playerClass.sheep == 0 &&
        playerClass.wood == 0 &&
        playerClass.bricks == 0 &&
        playerClass.wheat == 0 &&
        playerClass.ore == 0) {
      JOptionPane.showMessageDialog(this,
          "The robber tried to steal from player " + player + " but they were too poor and had nothing to take :(");
      return;
    }

    // now pick a resource to take (but make sure it's a resource they really have)
    int resourceType = random.nextInt(5);
    int countOfType = 0;
    do {

      resourceType = random.nextInt(5);
      countOfType = 0;
      switch (resourceType) {
        case 0:
          countOfType = playerClass.sheep;
          break;
        case 1:
          countOfType = playerClass.wood;
          break;
        case 2:
          countOfType = playerClass.bricks;
          break;
        case 3:
          countOfType = playerClass.wheat;
          break;
        case 4:
          countOfType = playerClass.ore;
          break;
      }

    } while (countOfType == 0);

    // finally, we can steal!!
    // for the message box
    String resourceName = "";
    switch (resourceType) {
      case 0:
        players[player - 1].sheep--;
        players[currentTurn - 1].sheep++;
        resourceName = "Sheep";
        break;
      case 1:
        players[player - 1].wood--;
        players[currentTurn - 1].wood++;
        resourceName = "Wood";
        break;
      case 2:
        players[player - 1].bricks--;
        players[currentTurn - 1].bricks++;
        resourceName = "Bricks";
        break;
      case 3:
        players[player - 1].wheat--;
        players[currentTurn - 1].wheat++;
        resourceName = "Wheat";
        break;
      case 4:
        players[player - 1].ore--;
        players[currentTurn - 1].ore++;
        resourceName = "Ore";
        break;
    }

    // show the dialogue
    JOptionPane.showMessageDialog(this,
        "The robber stole " + resourceName + " from player " + player + " and gave it to player " + currentTurn);

  }

  /**
   * When a player wins
   */
  public void victory() {

    // open up a message box saying congratulations
    JOptionPane.showMessageDialog(this, "Congratulations Player " + currentTurn + "! You won Catan!");
    System.exit(0);

  }

  /**
   * run through each player letting them build two settlements each
   */
  public void initGame() {

    // for each player, let them build their settlements and roads
    // each player should get two settlements
    for (int i = 0; i < 2; i++) {
      // run through each player
      for (int p = 1; p <= config.playerCount; p++) {
        // build a settlement in free build mode
        // current player should be whatever player is up to build
        currentTurn = p;
        // open the settlement build dialogue
        openBuildDialogue(3);
        // now do this again ig
        openBuildDialogue(4);

      }
    }

    // set current turn back to 1 and roll
    nextTurn();

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
      BuildDialogue dialogue = new BuildDialogue(frame, mode, board, currentTurn, this);
      buildCoords = dialogue.result;
    } catch (Exception error) {
      System.out.println("Failed! Just giving up");
      return;
    }

    // now we know the coordinates, call the specific function depending on the mode
    switch (mode) {

      // if mode = 0, building road
      // if mode = 4, free build road!
      case 0:
        board.roads[buildCoords[1]][buildCoords[0]] = currentTurn;
        players[currentTurn - 1].buildRoad(buildCoords[0], buildCoords[1], false);
        break;

      case 4:
        board.roads[buildCoords[1]][buildCoords[0]] = currentTurn;
        players[currentTurn - 1].buildRoad(buildCoords[0], buildCoords[1], true);
        break;

      // if mode = 1, building settlement with resource consumption
      // if mode = 3, free build!
      case 1:
        board.settlements[buildCoords[1]][buildCoords[0]] = currentTurn;
        players[currentTurn - 1].buildSettlement(buildCoords[0], buildCoords[1], false);
        break;

      case 3:
        board.settlements[buildCoords[1]][buildCoords[0]] = currentTurn;
        players[currentTurn - 1].buildSettlement(buildCoords[0], buildCoords[1], true);
        break;

      // case 5 is moving the robber
      case 5:
        robberLocation[0] = buildCoords[0];
        robberLocation[1] = buildCoords[1];
        steal(robberLocation[0], robberLocation[1]);
        // and finally update the interface
        components.update();
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

          // IF we're at the robber's location, render that too
          if (y - startGameRow == robberLocation[1] && x - startGameColumn == robberLocation[0]) {
            gl.drawImage(robberTexture.img, numStartX, numStartY,
                numberDiameter,
                numberDiameter,
                null,
                null);
          }

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
      // and if there's a city in the same place
      int cityType = board.cities[settlementY][settlementX];

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
      if (cityType != 0) {

        // this is the FIRST settlement, so render using the left coordinates
        int startX = -10;
        int startY = 13;

        gl.drawImage(cityTextures[cityType].img, drawX + startX,
            drawY + startY, 20,
            26,
            null,
            null);
      }

      settlementX++;
      if (settlementX < board.settlements[settlementY].length) {

        // it exists, so check the settlement type
        settlementType = board.settlements[settlementY][settlementX];
        cityType = board.cities[settlementY][settlementX];
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
        if (cityType != 0) {

          // this is the TOP settlement, so render using the top coordinates
          int startX = tileWidth / 2 - 10;
          int startY = -13;

          gl.drawImage(cityTextures[cityType].img, drawX + startX,
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
      int cityType = board.cities[settlementY][0];
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
      if (cityType != 0) {

        // this is the TOP settlement, so render using the top coordinates
        int startX = tileWidth / 2 - 10;
        int startY = -13;

        gl.drawImage(cityTextures[cityType].img, drawX + startX,
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
    // Aprox. 60 FPS
    Timer timer = new Timer(17, new ActionListener() {
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
    timer.start();

  }

}
