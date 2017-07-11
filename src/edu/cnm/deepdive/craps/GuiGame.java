package edu.cnm.deepdive.craps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * @author 
 */
public class GuiGame 
  implements StateMachine.Playable, StateMachine.Continuable, StateMachine.Display {

  private static final String WINS = "Wins = %d";
  private static final String LOSSES = "Losses = %d";
  private static final String POINT = "Point = %d";
  private static final String ROLL = "Roll = %d";
  
  private ImageIcon[] dieFaces;
  private ImageIcon rollIcon;
  private ImageIcon haltIcon;
  private ImageIcon blankIcon;
  private JLabel roll1;
  private JLabel roll2;
  private JButton play;
  private JButton stop;
  private JLabel wins;
  private JLabel losses;
  private JLabel point;
  private JLabel roll;
  
  private boolean uiSetup = false;
  private boolean playClicked = false;
  private boolean stopClicked = false;
  
  private Random rng = new Random();

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    GuiGame game = new GuiGame();
    SwingUtilities.invokeLater(() -> game.createAndShowGui());
    game.play();
    System.exit(0);
  }

  private static ImageIcon createImageIcon(String path) {
    URL imgURL = GuiGame.class.getClassLoader().getResource(path);
    return new ImageIcon(imgURL);
  }

  private void createAndShowGui() {
    JFrame frame = new JFrame("Simple Time-Wasting Craps Game");
    JPanel dicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel textPanel = new JPanel(new GridLayout(1, 4));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    dieFaces = new ImageIcon[6];
    for (int i = 0; i < 6; i++) {
      dieFaces[i] = createImageIcon(String.format("images/%d.png", i + 1));
    }
    rollIcon = createImageIcon("images/roll.png");
    haltIcon = createImageIcon("images/halt.png");
    blankIcon = createImageIcon("images/blank.png");
    roll1 = new JLabel(blankIcon);
    roll2 = new JLabel(blankIcon);
    dicePanel.add(roll1);
    dicePanel.add(roll2);
    play = new JButton(rollIcon);
    stop = new JButton(haltIcon);
    disableButtons();
    play.addActionListener((evt) -> {
      disableButtons();
      resumePlay();
    });
    stop.addActionListener((evt) -> {
      disableButtons();
      stopPlay();
    });
    buttonPanel.add(play);
    buttonPanel.add(stop);
    wins = new JLabel(String.format(WINS, 0));
    losses = new JLabel(String.format(LOSSES, 0));
    point = new JLabel(String.format(POINT, 0));
    roll = new JLabel(String.format(ROLL, 0));
    //textPanel.setMinimumSize(new Dimension(400, 100));
    textPanel.add(wins);
    textPanel.add(losses);
    textPanel.add(point);
    textPanel.add(roll);
    point.setVisible(false);
    roll.setVisible(false);
    frame.add(dicePanel, BorderLayout.NORTH);
    frame.add(textPanel, BorderLayout.CENTER);
    frame.add(buttonPanel, BorderLayout.SOUTH);
    frame.pack();
    frame.setVisible(true);
    synchronized (this) {
      uiSetup = true;
      notify();
    }
  }
  
  private synchronized void play() {
    while (!uiSetup) {
      try {
        wait();
      } catch (InterruptedException ex) {
        // Do nothing.
      }
    }
    StateMachine croupier = new StateMachine();
    croupier.setDisplay(this);
    croupier.setPlayable(this);
    croupier.setContinuable(this);
    croupier.play();
  }

  @Override
  public void update(int[] roll) {
    JLabel rollLabel = this.roll;
    synchronized (this) {
      for (int i = 0; i < 6; i ++) {
        SwingUtilities.invokeLater(new Runnable() {
  
          @Override
          public void run() {
            roll1.setIcon(dieFaces[rng.nextInt(6)]);
            roll2.setIcon(dieFaces[rng.nextInt(6)]);
          }
      
        });
        try {
          wait(150);
        } catch (InterruptedException ex) {
          // do nothing.
        }
      }
    }
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        roll1.setIcon(dieFaces[roll[0] - 1]);
        roll2.setIcon(dieFaces[roll[1] - 1]);
        rollLabel.setText(String.format(ROLL, roll[0] + roll[1]));
        rollLabel.setVisible(true);
      }
    });
  }

  @Override
  public boolean continuePlay(int point) {
    JLabel pointLabel = this.point;
    SwingUtilities.invokeLater(() -> {
      pointLabel.setText(String.format(POINT, point));
      pointLabel.setVisible(true);
    });
    return getUserResponse();
  }

  @Override
  public boolean playAgain(int wins, int losses) {
    JLabel pointLabel = this.point;
    JLabel winLabel = this.wins;
    JLabel lossLabel = this.losses;
    SwingUtilities.invokeLater(() -> {
      pointLabel.setVisible(false);
      winLabel.setText(String.format(WINS, wins));
      lossLabel.setText(String.format(LOSSES, losses));
      winLabel.setVisible(true);
      lossLabel.setVisible(true);
    });
    return getUserResponse();
  }
  
  private synchronized boolean getUserResponse() {
    SwingUtilities.invokeLater(() -> enableButtons());
    while (!playClicked && !stopClicked) {
      try {
        wait();
      } catch (InterruptedException ex) {
        // Do nothing.
      }
    }
    boolean result = playClicked;
    playClicked = false;
    return result;
  }
  
  private void enableButtons() {
    play.setEnabled(true);
    stop.setEnabled(true);
  }
  
  private void disableButtons() {
    play.setEnabled(false);
    stop.setEnabled(false);    
  }
  
  private synchronized void resumePlay() {
    playClicked = true;
    notify();
  }
  
  private synchronized void stopPlay() {
    stopClicked = true;
    notify();
  }
  
}





