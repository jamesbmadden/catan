/**
 * the dialogue that pops up when you click on the "trade" button.
 */
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import java.awt.Dialog;
import javax.swing.JRadioButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

public class TradeDialogue implements ActionListener {

  JDialog dialogue;
  Catan parent;
  JPanel[] panels = new JPanel[2];

  JRadioButton sheep;
  JRadioButton wood;
  JRadioButton bricks;
  JRadioButton wheat;
  JRadioButton ore;

  int selectedToGive = 0;
  int selectedPlayer = 0;

  JButton sheepButton;
  JButton woodButton;
  JButton bricksButton;
  JButton wheatButton;
  JButton oreButton;

  JList<String> sheepList;
  JList<String> woodList;
  JList<String> bricksList;
  JList<String> wheatList;
  JList<String> oreList;

  JList<String> p2SheepList;
  JList<String> p2WoodList;
  JList<String> p2BricksList;
  JList<String> p2WheatList;
  JList<String> p2OreList;

  public TradeDialogue (Catan catan) {

    parent = catan;
    dialogue = new JDialog(parent.frame, "Player " + parent.currentTurn + " Trade", Dialog.ModalityType.DOCUMENT_MODAL);
    dialogue.setResizable(false);

    panels[0] = new JPanel();
    panels[1] = new JPanel();

    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("With Bank", panels[0]);
    tabs.addTab("With Player", panels[1]);

    // adds content to the bank tab
    populateBankTab();
    populatePlayerTab();

    dialogue.add(tabs);

    // setsize of dialog
    dialogue.setSize(400, 600);

    // set visibility of dialog
    dialogue.setVisible(true);

  }

  public void populateBankTab () {

    // create a label at the top specifying what trading with the bank means.
    JLabel info = new JLabel("<html>Trading with the bank allows you to swap<br>4 resources of the same type for one resource of<br>another type.</html>");

    panels[0].add(info);

    // options for resources
    ButtonGroup resourcesToLose = new ButtonGroup();

    sheep = new JRadioButton("Sheep");
    // if the player doesn't have four or more, don't let them select it
    sheep.setActionCommand("bank-give-sheep");
    sheep.addActionListener(this);
    sheep.setEnabled(parent.players[parent.currentTurn - 1].sheep >= 4);
    resourcesToLose.add(sheep);
    panels[0].add(sheep);

    wood = new JRadioButton("Wood");
    // if the player doesn't have four or more, don't let them select it
    wood.setActionCommand("bank-give-wood");
    wood.addActionListener(this);
    wood.setEnabled(parent.players[parent.currentTurn - 1].wood >= 4);
    resourcesToLose.add(wood);
    panels[0].add(wood);

    bricks = new JRadioButton("Bricks");
    // if the player doesn't have four or more, don't let them select it
    bricks.setEnabled(parent.players[parent.currentTurn - 1].bricks >= 4);
    bricks.setActionCommand("bank-give-bricks");
    bricks.addActionListener(this);
    resourcesToLose.add(bricks);
    panels[0].add(bricks);
    
    wheat = new JRadioButton("Wheat");
    // if the player doesn't have four or more, don't let them select it
    wheat.setActionCommand("bank-give-wheat");
    wheat.addActionListener(this);
    wheat.setEnabled(parent.players[parent.currentTurn - 1].wheat >= 4);
    resourcesToLose.add(wheat);
    panels[0].add(wheat);

    ore = new JRadioButton("Ore");
    // if the player doesn't have four or more, don't let them select it
    ore.setEnabled(parent.players[parent.currentTurn - 1].ore >= 4);
    ore.setActionCommand("bank-give-ore");
    ore.addActionListener(this);
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
      sheepButton = new JButton("Sheep");
      sheepButton.setActionCommand("bank-take-sheep");
      sheepButton.addActionListener(this);
      sheepButton.setEnabled(false);
      panels[0].add(sheepButton);
      woodButton = new JButton("Wood");
      woodButton.setActionCommand("bank-take-wood");
      woodButton.addActionListener(this);
      woodButton.setEnabled(false);
      panels[0].add(woodButton);
      bricksButton = new JButton("Bricks");
      bricksButton.setActionCommand("bank-take-bricks");
      bricksButton.addActionListener(this);
      bricksButton.setEnabled(false);
      panels[0].add(bricksButton);
      wheatButton = new JButton("Wheat");
      wheatButton.setActionCommand("bank-take-wheat");
      wheatButton.addActionListener(this);
      wheatButton.setEnabled(false);
      panels[0].add(wheatButton);
      oreButton = new JButton("Ore");
      oreButton.setActionCommand("bank-take-ore");
      oreButton.addActionListener(this);
      oreButton.setEnabled(false);
      panels[0].add(oreButton);
    }

  }

  public void populatePlayerTab () {

    JLabel header = new JLabel("Player " + parent.currentTurn + ": What are you offering?");
    header.setPreferredSize(new Dimension(400, 32));
    header.setHorizontalAlignment(SwingConstants.CENTER);
    panels[1].add(header);

    // add dropdown boxes for each resource
    String[] sheepOptions = new String[parent.players[parent.currentTurn - 1].sheep + 1];
    for (int i = 0; i <= parent.players[parent.currentTurn - 1].sheep; i++) {
      sheepOptions[i] = i + " Sheep";
    }
    sheepList = new JList<String>(sheepOptions);
    sheepList.setSelectedIndex(0);
    panels[1].add(sheepList);

    String[] woodOptions = new String[parent.players[parent.currentTurn - 1].wood + 1];
    for (int i = 0; i <= parent.players[parent.currentTurn - 1].wood; i++) {
      woodOptions[i] = i + " Wood";
    }
    woodList = new JList<String>(woodOptions);
    woodList.setSelectedIndex(0);
    panels[1].add(woodList);

    String[] bricksOptions = new String[parent.players[parent.currentTurn - 1].bricks + 1];
    for (int i = 0; i <= parent.players[parent.currentTurn - 1].bricks; i++) {
      bricksOptions[i] = i + " Bricks";
    }
    bricksList = new JList<String>(bricksOptions);
    bricksList.setSelectedIndex(0);
    panels[1].add(bricksList);

    String[] wheatOptions = new String[parent.players[parent.currentTurn - 1].wheat + 1];
    for (int i = 0; i <= parent.players[parent.currentTurn - 1].wheat; i++) {
      wheatOptions[i] = i + " Wheat";
    }
    wheatList = new JList<String>(wheatOptions);
    wheatList.setSelectedIndex(0);
    panels[1].add(wheatList);

    String[] oreOptions = new String[parent.players[parent.currentTurn - 1].ore + 1];
    for (int i = 0; i <= parent.players[parent.currentTurn - 1].ore; i++) {
      oreOptions[i] = i + " Ore";
    }
    oreList = new JList<String>(oreOptions);
    oreList.setSelectedIndex(0);
    panels[1].add(oreList);

    // if there's only one player, there's no need for selecting a player. Pick the one that
    // ISN'T currently the player.
    if (parent.config.playerCount == 2) {

      if (parent.currentTurn == 1) {
        selectedPlayer = 2;
      } else {
        selectedPlayer = 1;
      }

    } else {

      // add a title
      JLabel whoTo = new JLabel("Who to trade with?");
      whoTo.setPreferredSize(new Dimension(400, 32));
      panels[1].add(whoTo);

      ButtonGroup playerToTradeWith = new ButtonGroup();
      // and checkboxes for each player.
      for (int i = 1; i <= parent.config.playerCount; i++) {

        // don't add an option for the current player
        if (i != parent.currentTurn) {
          // otherwise, add an option
          JRadioButton option = new JRadioButton("Player " + i);
          option.setActionCommand("set-player-" + i);
          option.addActionListener(this);
          option.setSelected(i == selectedPlayer);
          playerToTradeWith.add(option);
          panels[1].add(option);
        }

      }

    }

    // continue IF player has been selected
    if (selectedPlayer != 0) {

      JLabel secondHeader = new JLabel("Player " + selectedPlayer + ": What would you like to give?");
      secondHeader.setPreferredSize(new Dimension(400, 32));
      secondHeader.setHorizontalAlignment(SwingConstants.CENTER);
      panels[1].add(secondHeader);

      // add boxes for each of the resources for the selected player
      // add dropdown boxes for each resource
      sheepOptions = new String[parent.players[selectedPlayer - 1].sheep + 1];
      for (int i = 0; i <= parent.players[selectedPlayer - 1].sheep; i++) {
        sheepOptions[i] = i + " Sheep";
      }
      p2SheepList = new JList<String>(sheepOptions);
      p2SheepList.setSelectedIndex(0);
      panels[1].add(p2SheepList);

      woodOptions = new String[parent.players[selectedPlayer - 1].wood + 1];
      for (int i = 0; i <= parent.players[selectedPlayer - 1].wood; i++) {
        woodOptions[i] = i + " Wood";
      }
      p2WoodList = new JList<String>(woodOptions);
      p2WoodList.setSelectedIndex(0);
      panels[1].add(p2WoodList);

      bricksOptions = new String[parent.players[selectedPlayer - 1].bricks + 1];
      for (int i = 0; i <= parent.players[selectedPlayer - 1].bricks; i++) {
        bricksOptions[i] = i + " Bricks";
      }
      p2BricksList = new JList<String>(bricksOptions);
      p2BricksList.setSelectedIndex(0);
      panels[1].add(p2BricksList);

      wheatOptions = new String[parent.players[selectedPlayer - 1].wheat + 1];
      for (int i = 0; i <= parent.players[selectedPlayer - 1].wheat; i++) {
        wheatOptions[i] = i + " Wheat";
      }
      p2WheatList = new JList<String>(wheatOptions);
      p2WheatList.setSelectedIndex(0);
      panels[1].add(p2WheatList);

      oreOptions = new String[parent.players[selectedPlayer - 1].ore + 1];
      for (int i = 0; i <= parent.players[selectedPlayer - 1].ore; i++) {
        oreOptions[i] = i + " Ore";
      }
      p2OreList = new JList<String>(oreOptions);
      p2OreList.setSelectedIndex(0);
      panels[1].add(p2OreList);

      // finally, add a trade button
      JButton trade = new JButton("Trade");
      trade.setActionCommand("make-trade");
      trade.addActionListener(this);
      panels[1].add(trade);

    }

  }

  public void enableButtons () {

    sheepButton.setEnabled(true);
    woodButton.setEnabled(true);
    bricksButton.setEnabled(true);
    wheatButton.setEnabled(true);
    oreButton.setEnabled(true);

  }

  public void makeTrade () {

    // get the count of items each player is giving
    int[] currentPlayerGiving = {
      sheepList.getSelectedIndex(),
      woodList.getSelectedIndex(),
      bricksList.getSelectedIndex(),
      wheatList.getSelectedIndex(),
      oreList.getSelectedIndex()
    };
    // and the other player
    int[] otherPlayerGiving = {
      p2SheepList.getSelectedIndex(),
      p2WoodList.getSelectedIndex(),
      p2BricksList.getSelectedIndex(),
      p2WheatList.getSelectedIndex(),
      p2OreList.getSelectedIndex()
    };

    // subtract all that from each player
    parent.players[parent.currentTurn - 1].sheep -= currentPlayerGiving[0];
    parent.players[parent.currentTurn - 1].wood -= currentPlayerGiving[1];
    parent.players[parent.currentTurn - 1].bricks -= currentPlayerGiving[2];
    parent.players[parent.currentTurn - 1].wheat -= currentPlayerGiving[3];
    parent.players[parent.currentTurn - 1].ore -= currentPlayerGiving[4];

    // and the other player
    parent.players[selectedPlayer - 1].sheep -= otherPlayerGiving[0];
    parent.players[selectedPlayer - 1].wood -= otherPlayerGiving[1];
    parent.players[selectedPlayer - 1].bricks -= otherPlayerGiving[2];
    parent.players[selectedPlayer - 1].wheat -= otherPlayerGiving[3];
    parent.players[selectedPlayer - 1].ore -= otherPlayerGiving[4];

    // and now add to the other
    parent.players[parent.currentTurn - 1].sheep += otherPlayerGiving[0];
    parent.players[parent.currentTurn - 1].wood += otherPlayerGiving[1];
    parent.players[parent.currentTurn - 1].bricks += otherPlayerGiving[2];
    parent.players[parent.currentTurn - 1].wheat += otherPlayerGiving[3];
    parent.players[parent.currentTurn - 1].ore += otherPlayerGiving[4];

    // and the other player
    parent.players[selectedPlayer - 1].sheep += currentPlayerGiving[0];
    parent.players[selectedPlayer - 1].wood += currentPlayerGiving[1];
    parent.players[selectedPlayer - 1].bricks += currentPlayerGiving[2];
    parent.players[selectedPlayer - 1].wheat += currentPlayerGiving[3];
    parent.players[selectedPlayer - 1].ore += currentPlayerGiving[4];

    // now close the dialogue
    dialogue.dispose();

  }

  public void giveResource () {

    // take the selected resource from the player
    switch (selectedToGive) {

      case 1: 
        parent.players[parent.currentTurn - 1].sheep -= 4;
        break;


      case 2: 
        parent.players[parent.currentTurn - 1].wood -= 4;
        break;

      case 3: 
        parent.players[parent.currentTurn - 1].bricks -= 4;
        break;

      case 4: 
        parent.players[parent.currentTurn - 1].wheat -= 4;
        break;

      case 5: 
        parent.players[parent.currentTurn - 1].ore -= 4;
        break;
      
    }

  }

  @Override
  public void actionPerformed (ActionEvent event) {

    String code = event.getActionCommand();

    switch (code) {

      // turn buttons on or off when options are selected for giving
      case "bank-give-sheep":
        selectedToGive = 1;
        enableButtons();
        sheepButton.setEnabled(false);
        break;
    
      case "bank-give-wood":
        selectedToGive = 2;
        enableButtons();
        woodButton.setEnabled(false);
        break;

      case "bank-give-bricks":
        selectedToGive = 3;
        enableButtons();
        bricksButton.setEnabled(false);
        break;

      case "bank-give-wheat":
        selectedToGive = 4;
        enableButtons();
        wheatButton.setEnabled(false);
        break;

      case "bank-give-ore":
        selectedToGive = 5;
        enableButtons();
        oreButton.setEnabled(false);
        break;

      // now the commands for taking from the bank
      case "bank-take-sheep":
        giveResource();
        parent.players[parent.currentTurn - 1].sheep++;
        dialogue.dispose();
        break;
      
      case "bank-take-wood":
        giveResource();
        parent.players[parent.currentTurn - 1].wood++;
        dialogue.dispose();
        break;

      case "bank-take-bricks":
        giveResource();
        parent.players[parent.currentTurn - 1].bricks++;
        dialogue.dispose();
        break;

      case "bank-take-wheat":
        giveResource();
        parent.players[parent.currentTurn - 1].wheat++;
        dialogue.dispose();
        break;

      case "bank-take-ore":
        giveResource();
        parent.players[parent.currentTurn - 1].ore++;
        dialogue.dispose();
        break;

      // set the player to trade with
      case "set-player-1":
        selectedPlayer = 1;
        panels[1].removeAll();
        populatePlayerTab();
        panels[1].revalidate();
        panels[1].repaint();
        break;

      case "set-player-2":
        selectedPlayer = 2;
        panels[1].removeAll();
        populatePlayerTab();
        panels[1].revalidate();
        panels[1].repaint();
        break;

      case "set-player-3":
        selectedPlayer = 3;
        panels[1].removeAll();
        populatePlayerTab();
        panels[1].revalidate();
        panels[1].repaint();
        break;
      
      case "set-player-4":
        selectedPlayer = 4;
        panels[1].removeAll();
        populatePlayerTab();
        panels[1].revalidate();
        panels[1].repaint();
        break;

      // on make trade, do it!!
      case "make-trade":
        makeTrade();
        break;

    }

  }
  
}
