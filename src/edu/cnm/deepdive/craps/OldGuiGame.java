/**
 * 
 */
package edu.cnm.deepdive.craps;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Sky Link
 *
 */
public class OldGuiGame {
  
  JFrame frame;
  ImageIcon[] dieFaces;
  ImageIcon blank;
  ImageIcon roll;
  ImageIcon halt;
  JButton roll1;
  JButton roll2;
  JButton rollAction;
  JButton haltAction;
  JPanel content;


  private void createAndShowGui() {
    frame = new JFrame("Craps Game");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    dieFaces = new ImageIcon[6];
    for ( int i = 0; i < 6; i++) {
      dieFaces[i] = createImageIcon(String.format("images/%d.png", i + 1));
    }
    blank = createImageIcon("images/blank.png");
    roll = createImageIcon("images/roll.png");
    halt = createImageIcon("images/halt.png");
    roll1 = new JButton(dieFaces[5]);
    roll2 = new JButton(blank);
    rollAction = new JButton(roll);
    haltAction = new JButton(halt);
    roll1.setEnabled(false);
    roll2.setEnabled(false);
    rollAction.setEnabled(false);
    haltAction.setEnabled(false);
    content = new JPanel();
    content.add(roll1);
    content.add(roll2);
    content.add(rollAction);
    content.add(haltAction);
    content.setOpaque(true);
    frame.setContentPane(content);
    frame.pack();
    frame.setVisible(true);
  }
  
  private void play() {
    OldStateMachine croupier = new OldStateMachine();
    croupier.setDisplay(new OldStateMachine.Display() {
      
      @Override
      public void update(int[] roll) {
        roll1.setIcon(dieFaces[roll[0] - 1]);
        roll2.setIcon(dieFaces[roll[1] - 1]);
      }
    });
    croupier.setPlayable(new OldStateMachine.Playable() {
      
      @Override
      public boolean playAgain(int wins, int losses) {
        return false;
      }
    });
    croupier.setContinuable(new OldStateMachine.Continuable() {
      
      @Override
      public boolean continuePlay(int wins, int losses, int point) {
        return false;
      }
    });
  }
  
  private static ImageIcon createImageIcon(String path) {
    URL imgURL = OldGuiGame.class.getClassLoader().getResource(path);
    return new ImageIcon(imgURL);
  }
  /**
   * @param args
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        OldGuiGame game = new OldGuiGame();
        game.createAndShowGui();
      }
      
    });
  }

}
