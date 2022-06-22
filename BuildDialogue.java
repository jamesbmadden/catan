
/**
 * Open a Dialogue menu to select where the user wants to build
 */
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Dimension;
import java.io.IOException;

public class BuildDialogue extends JPanel implements ActionListener {

  JDialog dialogue;
  int mode;
  Texture background = new Texture("img/buildbackground.png");
  Board board;
  // which player is building
  int player;
  // the result will be saved here so wherever this class was created from can
  // read it
  int[] result = { -1, -1 };

  public BuildDialogue(JFrame frame, int _mode, Board _board, int _player) throws IOException {

    super();

    // set mode to whatever was provided
    mode = _mode;
    // and the reference to the board
    board = _board;
    // and the player number
    player = _player;
    // create the dialogue for configuring
    dialogue = new JDialog(frame, "Player " + player + ": Build", Dialog.ModalityType.DOCUMENT_MODAL);
    dialogue.setSize(416, 438);
    // add this panel to the dialogue to draw to
    dialogue.add(this);
    // make the dialogue NOT resizable so the positioning works
    dialogue.setResizable(false);
    setOpaque(true);
    setPreferredSize(new Dimension(200, 200));

    // set layout manager to NONE so that buttons can be absolutely positioned
    setLayout(null);

    // depending on the mode, set different button positions
    switch (mode) {

      // if mode is zero, add road positions
      case 0:
        addRoadButtons();
        break;

      // if mode is one, add settlement buttons
      case 1:
        addSettlementButtons(false);
        break;

      // if mode is two, add settlement buttons but it's cities this time
      case 2:
        addSettlementButtons(false);
        break;

      // case three is initializing the settlements, so use free build mode
      case 3:
        addSettlementButtons(true);
        break;

    }

    dialogue.setVisible(true);

  }

  /**
   * check to see if the road can be built, based on whether or not it's bordering
   * another row
   */
  public boolean isRoadBuildable(int x, int y, int player) {

    // IMMEDIATELY, if the road is already built on, return false
    if (board.roads[y][x] != 0) {
      return false;
    }

    if (y % 2 == 0) {

      // if y is even, check whether the roads beside border it,
      // and the roads above or below at either one of its intersections

      // is there a road to the right?
      if (x + 1 < board.roads[y].length && board.roads[y][x + 1] == player) {
        return true;
      }

      // is there a road to the left?
      if (x > 0 && board.roads[y][x - 1] == player) {
        return true;
      }

      // is there a road below?
      int roundedX;
      // it needs to be rounded differently depending on whether it's above or below
      // halfway
      if (y < 6) {
        roundedX = Math.round(x / (float) 2.0);
      } else {
        // this is the same as flooring the result, integer division just ignores the
        // decimal
        roundedX = x / 2;
      }

      if (y + 1 < board.roads.length && roundedX < board.roads[y + 1].length
          && board.roads[y + 1][roundedX] == player) {
        return true;
      }

      // how about above?
      // it needs to be rounded differently depending on whether it's above or below
      // halfway
      if (y > 5) {
        roundedX = Math.round(x / (float) 2.0);
      } else {
        // this is the same as flooring the result, integer division just ignores the
        // decimal
        roundedX = x / 2;
      }

      if (y > 0 && roundedX < board.roads[y - 1].length
          && board.roads[y - 1][roundedX] == player) {
        return true;
      }

    } else {

      // for odd rows, we're looking at the vertical roads
      // possible locations for borders are top left, top right,
      // bottom left, and bottom right.

      // first, check whether the two above have roads
      // the formula differs for the top half and bottom half because they have
      // different offsets
      int roundedX;
      if (y < 6) {
        roundedX = x * 2 - 1;
      } else {
        roundedX = x * 2;
      }

      // now check top left
      if (y > 0 && roundedX >= 0 && roundedX < board.roads[y - 1].length
          && board.roads[y - 1][roundedX] == player) {
        return true;
      }
      // and top right
      if (y > 0 && roundedX + 1 < board.roads[y - 1].length
          && board.roads[y - 1][roundedX + 1] == player) {
        return true;
      }

      // finally, check whether there are roads below
      if (y >= 5) {
        roundedX = x * 2 - 1;
      } else {
        roundedX = x * 2;
      }

      // check bottom left
      if (y > 0 && roundedX >= 0 && roundedX < board.roads[y + 1].length
          && board.roads[y + 1][roundedX] == player) {
        return true;
      }
      // and bottom right
      if (y > 0 && roundedX + 1 < board.roads[y + 1].length
          && board.roads[y + 1][roundedX + 1] == player) {
        return true;
      }

    }
    return false;
  }

  /**
   * check whether that position already has a settlement, and if it's a valid
   * place for this player to build
   * 
   * Free Build refers to the start of the game when a settlement is able to be
   * placed anywhere. During most gameplay, free build is disabled.
   */
  public boolean isSettlementBuildable(int x, int y, int player, boolean freeBuild) {

    // check whether this space is already filled
    if (board.settlements[y][x] != 0) {
      return false;
    }
    // now check whether any of the bordering spaces are already taken, in which
    // case it can't be built there
    // check right
    if (x + 1 < board.settlements[y].length && board.settlements[y][x + 1] != 0) {
      return false;
    }
    // and left
    if (x > 0 && board.settlements[y][x - 1] != 0) {
      return false;
    }
    // and above IF above is connected
    if (y > 0) {
      // different methods depending on whether we're in the top or bottom halves
      if (x % 2 == 1 && y < 3) {

        // top half so the row above has two less settlements
        if (x != 0 && x - 1 < board.settlements[y - 1].length && board.settlements[y - 1][x - 1] != 0) {
          return false;
        }

      } else if (x % 2 == 0 && y == 3) {

        // middle row so row above is the same length
        if (x < board.settlements[y - 1].length && board.settlements[y - 1][x] != 0) {
          return false;
        }

      } else if (x % 2 == 0 && y > 3 && y < 6) {

        // bottom half so the row above has two more settlements
        if (x + 1 < board.settlements[y - 1].length && board.settlements[y - 1][x + 1] != 0) {
          return false;
        }

      }
    }

    // FINALLY, check below IF connected
    if (y + 1 < board.settlements.length) {

      // different methods depending on whether we're in the top or bottom halves
      if (x % 2 == 0 && y < 2 && y >= 0) {
        // top half so the row below has two more settlements
        if (x + 1 < board.settlements[y + 1].length && board.settlements[y + 1][x + 1] != 0) {
          return false;
        }

      } else if (x % 2 == 0 && y == 2) {

        // middle row so row below is the same length
        if (x < board.settlements[y + 1].length && board.settlements[y + 1][x] != 0) {
          return false;
        }

      } else if (x % 2 == 1 && y > 2) {

        // bottom half so the row below has two less settlements
        if (x != 0 && x - 1 < board.settlements[y + 1].length && board.settlements[y + 1][x - 1] != 0) {
          return false;
        }

      }

    }

    // now that we've made sure each settlement is at least 2 away from the other,
    // make sure it's connected to a road. BUT ONLY IF free build is disabled.
    if (freeBuild) {
      // if free build mode is on and the settlement is at least 2 away from any
      // other, we're good to go this is a valid space
      return true;
    }

    // if we're still going here, free build is NOT on and we have to make sure
    // roads are connected to the settlement
    // ahhh!!!!!!!!
    // :((((
    if (y >= 0 && y < 6) {

      // check to the left
      if (x > 0 && x < board.roads[y * 2].length && board.roads[y * 2][x - 1] == player) {
        return true;
      }
      // check to the right
      if (x >= 0 && x < board.roads[y * 2].length && board.roads[y * 2][x] == player) {
        return true;
      }

      // now the part that depends on top or bottom half
      // IF the spot is connected by a road on the bottom half, check it
      if (y < 3 && x % 2 == 0) {

        // it is in fact connected by a road
        if (x >= 0 && x < board.settlements[y].length && board.roads[y * 2 + 1][(int) Math.ceil(x / 2.0)] == player) {
          return true;
        }

      }
      if (y >= 3 && x % 2 == 1 && y < 5) {

        if (x >= 0 && x < board.settlements[y].length && board.roads[y * 2 + 1][x / 2] == player) {
          return true;
        }

      }

      // ok now if it's connected by a road above!!
      if (y > 0 && y < 3 && x % 2 == 1) {

        if (x >= 0 && x < board.settlements[y].length && board.roads[y * 2 - 1][x / 2] == player) {
          return true;
        }

      }
      if (y >= 3 && x % 2 == 0) {

        if (x >= 0 && x < board.settlements[y].length && board.roads[y * 2 - 1][(int) Math.ceil(x / 2.0)] == player) {
          return true;
        }

      }

    }

    return false;

  }

  /**
   * Add buttons for building roads
   */
  public void addRoadButtons() {

    // create buttons for each possible road space
    // create a list of how many roads there are per row
    int[] roadsPerRow = { 6, 4, 8, 5, 10, 6, 10, 5, 8, 4, 6 };
    // how much to offset each row on the x axis
    int[] rowOffset = { 100, 85, 70, 50, 35, 15, 35, 50, 70, 85, 100 };

    for (int y = 0; y < 11; y++) {

      for (int x = 0; x < roadsPerRow[y]; x++) {

        JButton button = new JButton("+");
        button.setBounds(rowOffset[y] + x * (35 + y % 2 * 35), 40 + y * 30, 20, 20);
        // set the action command to the coordinates where to build so it may be figured
        // out in the action listener
        button.setActionCommand(x + "," + y);
        button.addActionListener(this);

        // determine whether this is a valid space to build on
        button.setEnabled(isRoadBuildable(x, y, player));

        add(button);

      }

    }

  }

  /**
   * Add buttons for building a road or settlement
   */
  public void addSettlementButtons(boolean freeBuild) {

    // create buttons for each possible settlement space
    // create a list of how many settlements there are per row
    int[] settlementsPerRow = { 7, 9, 11, 11, 9, 7 };
    // how much to offset each row on the x axis
    int[] rowOffset = { 90, 55, 20, 20, 55, 90 };

    for (int y = 0; y < 6; y++) {

      for (int x = 0; x < settlementsPerRow[y]; x++) {

        JButton button = new JButton("+");
        // if the column is odd, offset the y position
        // if y > 2, the offset should be flipped
        int yOffset;
        if (y < 3) {
          yOffset = x % 2 * -20;
        } else {
          yOffset = (x + 1) % 2 * -20;
        }
        button.setBounds(rowOffset[y] + x * 35, 40 + yOffset + y * 65, 20, 20);
        // set the action command to the coordinates where to build so it may be figured
        // out in the action listener
        button.setActionCommand(x + "," + y);
        button.addActionListener(this);
        button.setEnabled(isSettlementBuildable(x, y, player, freeBuild));

        add(button);

      }

    }

  }

  @Override
  public void paintComponent(Graphics gl) {

    // when its painted, just draw the picture
    gl.drawImage(background.img, 0, 0, getWidth(), getHeight(), null, null);

  }

  /**
   * Handle button presses
   */
  @Override
  public void actionPerformed(ActionEvent event) {

    // a button has been pressed! Figure out its x and y positions
    String[] coordStrings = event.getActionCommand().split(",");

    // now get the actual coordinates
    int x = Integer.parseInt(coordStrings[0]);
    int y = Integer.parseInt(coordStrings[1]);

    result[0] = x;
    result[1] = y;

    // now we can close the dialogue :)
    dialogue.dispose();

  }

}
