
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
    dialogue = new JDialog(frame, "Build", Dialog.ModalityType.DOCUMENT_MODAL);
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
        addSettlementButtons();
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

    // if y is even, check whether the roads beside border it
    if (y % 2 == 0) {

      // is there a road to the right?
      if (x + 1 < board.roads[y].length && board.roads[y][x + 1] == player) {
        return true;
      }

      // is there a road to the left?
      if (x > 0 && board.roads[y][x - 1] == player) {
        return true;
      }

      // is there a road below?
      int roundedX = Math.round(x / (float) 2.0);
      if (y + 1 < board.roads.length && roundedX < board.roads[y + 1].length
          && board.roads[y + 1][roundedX] == player) {
        return true;
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
  public void addSettlementButtons() {

    // create buttons for each possible settlement space
    // create a list of how many settlements there are per row
    int[] roadsPerRow = { 3, 4, 5, 4, 3 };
    // how much to offset each row on the x axis
    int[] rowOffset = { 110, 75, 40, 75, 110 };

    for (int y = 0; y < 5; y++) {

      for (int x = 0; x < roadsPerRow[y]; x++) {

        JButton button = new JButton("+");
        button.setBounds(rowOffset[y] + x * 70, 50 + y * 65, 40, 40);
        // set the action command to the coordinates where to build so it may be figured
        // out in the action listener
        button.setActionCommand(x + "," + y);
        button.addActionListener(this);

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
