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

    dialogue.add(tabs);

    // setsize of dialog
    dialogue.setSize(166, 200);

    // set visibility of dialog
    dialogue.setVisible(true);

  }
  
}
