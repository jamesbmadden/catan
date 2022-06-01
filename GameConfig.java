
/**
 * Open a Dialogue menu to set the options for the game
 */
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.Dialog;
import javax.swing.JRadioButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameConfig implements ActionListener {

  // game properties
  public int playerCount = 2;
  public int pointsToWin = 10;

  JDialog dialogue;

  public GameConfig(JFrame frame) {

    // create the dialogue for configuring
    dialogue = new JDialog(frame, "Configure Game", Dialog.ModalityType.DOCUMENT_MODAL);
    // create a panel to add things to
    JPanel panel = new JPanel();
    dialogue.add(panel);

    // first, get the amount of players
    panel.add(new JLabel("How many players?"));
    ButtonGroup playerCount = new ButtonGroup();

    // create the radio buttons
    JRadioButton twoPlayers = new JRadioButton("2");
    twoPlayers.setActionCommand("setplayer2");
    twoPlayers.setSelected(true);
    twoPlayers.addActionListener(this);
    playerCount.add(twoPlayers);
    panel.add(twoPlayers);

    JRadioButton threePlayers = new JRadioButton("3");
    threePlayers.setActionCommand("setplayer3");
    threePlayers.addActionListener(this);
    playerCount.add(threePlayers);
    panel.add(threePlayers);

    JRadioButton fourPlayers = new JRadioButton("4");
    fourPlayers.setActionCommand("setplayer4");
    fourPlayers.addActionListener(this);
    playerCount.add(fourPlayers);
    panel.add(fourPlayers);

    // now the point count
    panel.add(new JLabel("How many points to win?"));
    ButtonGroup pointsToWin = new ButtonGroup();

    // create the radio buttons
    JRadioButton fivePointsToWin = new JRadioButton("5");
    fivePointsToWin.setActionCommand("setmaxpoints5");
    fivePointsToWin.addActionListener(this);
    pointsToWin.add(fivePointsToWin);
    panel.add(fivePointsToWin);

    JRadioButton sixPointsToWin = new JRadioButton("6");
    sixPointsToWin.setActionCommand("setmaxpoints6");
    sixPointsToWin.addActionListener(this);
    pointsToWin.add(sixPointsToWin);
    panel.add(sixPointsToWin);

    JRadioButton sevenPointsToWin = new JRadioButton("7");
    sevenPointsToWin.setActionCommand("setmaxpoints7");
    sevenPointsToWin.addActionListener(this);
    pointsToWin.add(sevenPointsToWin);
    panel.add(sevenPointsToWin);

    JRadioButton eightPointsToWin = new JRadioButton("8");
    eightPointsToWin.setActionCommand("setmaxpoints8");
    eightPointsToWin.addActionListener(this);
    pointsToWin.add(eightPointsToWin);
    panel.add(eightPointsToWin);

    JRadioButton ninePointsToWin = new JRadioButton("9");
    ninePointsToWin.setActionCommand("setmaxpoints9");
    ninePointsToWin.addActionListener(this);
    pointsToWin.add(ninePointsToWin);
    panel.add(ninePointsToWin);

    JRadioButton tenPointsToWin = new JRadioButton("10");
    tenPointsToWin.setActionCommand("setmaxpoints10");
    tenPointsToWin.setSelected(true);
    tenPointsToWin.addActionListener(this);
    pointsToWin.add(tenPointsToWin);
    panel.add(tenPointsToWin);

    // add a button for closing the window
    JButton closeButton = new JButton("Start Game!");
    closeButton.setActionCommand("startgame");
    closeButton.addActionListener(this);
    panel.add(closeButton);

    // setsize of dialog
    dialogue.setSize(166, 200);

    // set visibility of dialog
    dialogue.setVisible(true);

  }

  /**
   * Action listener so that the dialogue can respond to the change in radio
   * button state
   */
  @Override
  public void actionPerformed(ActionEvent event) {

    // figure out which radio was selected
    String command = event.getActionCommand();

    // now switch based on whichever it is
    switch (command) {
      // cases for setting the player count
      case "setplayer2":
        playerCount = 2;
        break;
      case "setplayer3":
        playerCount = 3;
        break;
      case "setplayer4":
        playerCount = 4;
        break;

      // cases for setting the points to win
      case "setmaxpoints5":
        pointsToWin = 5;
        break;
      case "setmaxpoints6":
        pointsToWin = 6;
        break;
      case "setmaxpoints7":
        pointsToWin = 7;
        break;
      case "setmaxpoints8":
        pointsToWin = 8;
        break;
      case "setmaxpoints9":
        pointsToWin = 9;
        break;
      case "setmaxpoints10":
        pointsToWin = 10;
        break;

      // button for closing the dialogue
      case "startgame":
        dialogue.dispose();
        break;
    }

  }

}
