/**
 * the dialogue that pops up when you click on the "trade" button.
 */
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.Dialog;
import javax.swing.JRadioButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TradeDialogue {

  JDialog dialogue;
  Catan parent;
  JPanel[] panels = new JPanel[2];

  public TradeDialogue (Catan catan) {

    parent = catan;
    dialogue = new JDialog(parent.frame, "Player " + parent.currentTurn + " Trade", Dialog.ModalityType.DOCUMENT_MODAL);

    panels[0] = new JPanel();
    panels[1] = new JPanel();

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("With Bank", panels[0]);
    tabs.addTab("With Player", panels[1]);

    // adds content to the bank tab
    populateBankTab();

    dialogue.add(tabs);

    // setsize of dialog
    dialogue.setSize(400, 300);

    // set visibility of dialog
    dialogue.setVisible(true);

  }

  public void populateBankTab () {

    // create a label at the top specifying what trading with the bank means.
    JLabel info = new JLabel("<html>Trading with the bank allows you to swap<br>4 resources of the same type for one resource of<br>another type.</html>");

    panels[0].add(info);

    // options for resources
    ButtonGroup resourcesToLose = new ButtonGroup();

    JRadioButton sheep = new JRadioButton("Sheep");
    // if the player doesn't have four or more, don't let them select it
    sheep.setEnabled(parent.players[parent.currentTurn - 1].sheep >= 4);
    resourcesToLose.add(sheep);
    panels[0].add(sheep);

    JRadioButton wood = new JRadioButton("Wood");
    // if the player doesn't have four or more, don't let them select it
    wood.setEnabled(parent.players[parent.currentTurn - 1].wood >= 4);
    resourcesToLose.add(wood);
    panels[0].add(wood);

    JRadioButton bricks = new JRadioButton("Bricks");
    // if the player doesn't have four or more, don't let them select it
    bricks.setEnabled(parent.players[parent.currentTurn - 1].bricks >= 4);
    resourcesToLose.add(bricks);
    panels[0].add(bricks);
    
    JRadioButton wheat = new JRadioButton("Wheat");
    // if the player doesn't have four or more, don't let them select it
    wheat.setEnabled(parent.players[parent.currentTurn - 1].wheat >= 4);
    resourcesToLose.add(wheat);
    panels[0].add(wheat);

    JRadioButton ore = new JRadioButton("Ore");
    // if the player doesn't have four or more, don't let them select it
    ore.setEnabled(parent.players[parent.currentTurn - 1].ore >= 4);
    resourcesToLose.add(ore);
    panels[0].add(ore);

    // if the player has not enough of any resources, send them a condolence message
    boolean playerHasNothing = parent.players[parent.currentTurn - 1].sheep < 4 && 
    parent.players[parent.currentTurn - 1].wood < 4 && 
    parent.players[parent.currentTurn - 1].bricks < 4 && 
    parent.players[parent.currentTurn - 1].wheat < 4 && 
    parent.players[parent.currentTurn - 1].ore < 4;

    if (playerHasNothing) {

      JLabel sorry = new JLabel("<html>Sorry, but you don't have enough<br>of anything to trade with the bank.</html>");
      panels[0].add(sorry);

    } else {

      JLabel tradeTitle = new JLabel("Trade for...");
      panels[0].add(tradeTitle);

      // player has something to trade, so give them buttons for what they want
      JButton wheatButton = new JButton("Wheat");
      panels[0].add(wheatButton);
    }

  }
  
}
