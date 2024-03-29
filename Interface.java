import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;

public class Interface implements ActionListener {

  // set the dimensions for our grid
  int gridWidth = 10;
  int gridHeight = 20;

  // the JPanel the components are being added to
  Catan parent;
  // and all the components we need
  JLabel header;
  JButton buildButton;
  JButton tradeButton;
  JButton endTurnButton;

  // whether popup buttons are open
  boolean popupButtonsOpen = false;

  // the buttons for building
  JButton[] popupButtons = new JButton[3];

  // resource display components
  JLabel sheep;
  JLabel wood;
  JLabel bricks;
  JLabel wheat;
  JLabel ore;

  // components for displaying the scores
  JLabel scoreHeader;
  JLabel[] scores;

  // and the grid layout constraints
  GridBagConstraints constraints = new GridBagConstraints();

  public Interface(Catan panel) {

    // set the parent
    parent = panel;
    // and create the components
    addComponents();

  }

  /**
   * Adds the buttons and labels to the interface, and sets up the layout.
   */
  private void addComponents() {

    // we're using a grid bag layout for the desired flexibility and layout
    // set up a 10x10 grid of equal sizes
    setupGrid();

    // create the player turn text
    header = new JLabel("Player 1's Turn");
    addComponentToGrid(header, 0, 0, 10, 1);
    // set the style
    header.setHorizontalAlignment(SwingConstants.CENTER);
    // set the header to the current player's colour
    header.setForeground(parent.playerColours[parent.currentTurn - 1]);
    header.setFont(new Font("Serif", Font.PLAIN, 32));

    constraints.insets = new Insets(0, 0, 4, 4);

    // create the build button
    buildButton = new JButton("Build");
    buildButton.setActionCommand("build");
    buildButton.addActionListener(this);
    buildButton.setMnemonic(KeyEvent.VK_B);
    addComponentToGrid(buildButton, 9, 17, 10, 18);

    // create the build button
    tradeButton = new JButton("Trade");
    tradeButton.setActionCommand("trade");
    tradeButton.addActionListener(this);
    tradeButton.setMnemonic(KeyEvent.VK_T);
    addComponentToGrid(tradeButton, 9, 18, 10, 19);

    // create the end turn button
    endTurnButton = new JButton("End Turn");
    endTurnButton.setActionCommand("endturn");
    endTurnButton.addActionListener(this);
    endTurnButton.setMnemonic(KeyEvent.VK_E);
    addComponentToGrid(endTurnButton, 9, 19, 10, 20);

    // create the resource lists
    sheep = new JLabel("Sheep: " + parent.players[parent.currentTurn - 1].sheep);
    sheep.setForeground(Color.WHITE);
    sheep.setFont(new Font("Serif", Font.PLAIN, 24));
    addComponentToGrid(sheep, 0, 15, 1, 16);
    wood = new JLabel("Wood: " + parent.players[parent.currentTurn - 1].wood);
    wood.setForeground(Color.WHITE);
    wood.setFont(new Font("Serif", Font.PLAIN, 24));
    addComponentToGrid(wood, 0, 16, 1, 17);
    bricks = new JLabel("Bricks: " + parent.players[parent.currentTurn - 1].bricks);
    bricks.setForeground(Color.WHITE);
    bricks.setFont(new Font("Serif", Font.PLAIN, 24));
    addComponentToGrid(bricks, 0, 17, 1, 18);
    wheat = new JLabel("Wheat: " + parent.players[parent.currentTurn - 1].wheat);
    wheat.setForeground(Color.WHITE);
    wheat.setFont(new Font("Serif", Font.PLAIN, 24));
    addComponentToGrid(wheat, 0, 18, 1, 19);
    ore = new JLabel("Ore: " + parent.players[parent.currentTurn - 1].ore);
    ore.setForeground(Color.WHITE);
    ore.setFont(new Font("Serif", Font.PLAIN, 24));
    addComponentToGrid(ore, 0, 19, 1, 20);

    // create components for the scores
    scoreHeader = new JLabel("Scores:");
    scoreHeader.setFont(new Font("Serif", Font.PLAIN, 24));
    scoreHeader.setForeground(Color.WHITE);
    addComponentToGrid(scoreHeader, 0, 1, 1, 2);

    // create the score labels
    scores = new JLabel[parent.config.playerCount];
    for (int i = 0; i < parent.config.playerCount; i++) {

      // establish the scoreboard
      scores[i] = new JLabel("" + parent.players[i].score);
      scores[i].setForeground(parent.playerColours[i]);
      scores[i].setFont(new Font("Serif", Font.BOLD, 20));
      addComponentToGrid(scores[i], 0, 2 + i, 1, 3 + i);

    }

  }

  private void setupGrid() {

    // enable insets on the constraints
    constraints.insets = new Insets(1, 1, 0, 0);
    constraints.weightx = 1.0;
    constraints.weighty = 1.0;

    // loop through rows and columns
    for (int y = 0; y < gridHeight; y++) {

      for (int x = 0; x < gridWidth; x++) {

        // fill every slot with a dummy panel for sizing
        JPanel dummyPanel = new JPanel();
        dummyPanel.setPreferredSize(new Dimension(0, 0));
        dummyPanel.setOpaque(false);
        addComponentToGrid(dummyPanel, x, y, x + 1, y + 1);

      }

    }

    // now turn off the insets
    constraints.insets = new Insets(0, 0, 0, 0);

  }

  /**
   * Add the build buttons to the screen
   */
  public void addBuildButtons() {

    // turn the build button into a close button
    buildButton.setText("Cancel");
    buildButton.setActionCommand("cancelpopup");
    buildButton.setMnemonic(KeyEvent.VK_C);
    // and disable the other two
    tradeButton.setEnabled(false);
    endTurnButton.setEnabled(false);

    // get whether the player will be able to build things
    boolean[] canBuild = parent.players[parent.currentTurn - 1].canBuild();

    // now make the buttons
    // first, road building
    popupButtons[0] = new JButton("Build Road");
    popupButtons[0].setActionCommand("buildroad");
    popupButtons[0].addActionListener(this);
    // disable the button if the player doesn't have the resources
    if (!canBuild[0]) {
      popupButtons[0].setEnabled(false);
    }
    addComponentToGrid(popupButtons[0], 8, 17, 9, 18);

    // now settlement
    popupButtons[1] = new JButton("Build Settlement");
    popupButtons[1].setActionCommand("buildsettlement");
    popupButtons[1].addActionListener(this);
    // disable the button if the player doesn't have the resources
    if (!canBuild[1]) {
      popupButtons[1].setEnabled(false);
    }
    addComponentToGrid(popupButtons[1], 8, 18, 9, 19);

    // and city
    popupButtons[2] = new JButton("Build City");
    popupButtons[2].setActionCommand("buildcity");
    popupButtons[2].addActionListener(this);
    // disable the button if the player doesn't have the resources
    if (!canBuild[2]) {
      popupButtons[2].setEnabled(false);
    }
    addComponentToGrid(popupButtons[2], 8, 19, 9, 20);

    // build buttons are here, set the boolean
    popupButtonsOpen = true;

  }

  public void closePopupButtons() {

    // if buildbuttonsOpen is false, there's nothing to remove, so stop now
    if (!popupButtonsOpen) {
      return;
    }

    // remove the first popup button
    parent.remove(popupButtons[0]);
    parent.remove(popupButtons[1]);
    parent.remove(popupButtons[2]);

    // and reset all the main buttons that may have been changed
    buildButton.setText("Build");
    buildButton.setActionCommand("build");
    buildButton.setEnabled(true);
    buildButton.setMnemonic(KeyEvent.VK_B);

    tradeButton.setText("Trade");
    tradeButton.setActionCommand("trade");
    tradeButton.setEnabled(true);
    tradeButton.setMnemonic(KeyEvent.VK_T);

    // create the end turn button
    endTurnButton.setText("End Turn");
    endTurnButton.setActionCommand("endturn");
    endTurnButton.setEnabled(true);
    endTurnButton.setMnemonic(KeyEvent.VK_E);

    // set build buttons open to false now that they've been removed
    popupButtonsOpen = false;

    // update the display
    update();

  }

  /**
   * Put the component in the position we want it
   * 
   * @param component the component to insert
   * @param startX    Which x to start it at
   * @param startY    Which y to start it at
   * @param endX      Which x position to end it at
   * @param endY      Which y position to end it at
   */
  private void addComponentToGrid(Component component, int startX, int startY, int endX, int endY) {

    // set the constraints
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridx = startX;
    constraints.gridy = startY;
    constraints.gridwidth = endX - startX;
    constraints.gridheight = endY - startY;

    parent.add(component, constraints);

  }

  /**
   * Update the display, whether it's a new turn or a change in resource count
   */
  public void update() {

    // first, update the header
    header.setText("Player " + parent.currentTurn + "'s Turn");
    // set the header to the current player's colour
    header.setForeground(parent.playerColours[parent.currentTurn - 1]);

    // now the resource counts
    sheep.setText("Sheep: " + parent.players[parent.currentTurn - 1].sheep);
    wood.setText("Wood: " + parent.players[parent.currentTurn - 1].wood);
    bricks.setText("Bricks: " + parent.players[parent.currentTurn - 1].bricks);
    wheat.setText("Wheat: " + parent.players[parent.currentTurn - 1].wheat);
    ore.setText("Ore: " + parent.players[parent.currentTurn - 1].ore);

    // update the scoreboard
    for (int i = 0; i < parent.config.playerCount; i++) {

      scores[i].setText("" + parent.players[i].score);

    }

  }

  /**
   * Handle button presses
   */
  @Override
  public void actionPerformed(ActionEvent event) {

    // get the action we're responding to
    String command = event.getActionCommand();

    switch (command) {

      // the end turn button is pressed: go to the next turn and update the UI
      case "endturn":
        boolean confirmed = JOptionPane.showConfirmDialog(parent, "Are you sure you want to end your turn?") == 0;
        if (confirmed) {
          parent.nextTurn();
        }
        break;
      case "build":
        addBuildButtons();
        break;
      case "cancelpopup":
        closePopupButtons();
        break;
      case "buildroad":
        parent.openBuildDialogue(0);
        break;
      case "buildsettlement":
        parent.openBuildDialogue(1);
        break;
      case "buildcity":
        parent.openBuildDialogue(6);
        break;

      // trade is pressed: open the dialogue
      case "trade":
        new TradeDialogue(parent);
        update();

    }

  }

}
