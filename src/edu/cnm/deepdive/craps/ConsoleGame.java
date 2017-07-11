/**
 * ConsoleGame.java
 */
package edu.cnm.deepdive.craps;

import java.util.Arrays;

/**
 * Simple program that presents the functionality of the {@link StateMachine} 
 * class via a console-based UI to play a simple solitaire craps game.  
 * 
 * @author Nicholas Bennett
 */
public class ConsoleGame {

  private static final String ROLL_DISPLAY = "Roll: %s = %d%n"; 
  private static final String PLAY_PROMPT = "Wins = %d; losses = %d; net = %d. Play again? ([y]/n) ";
  private static final String POINT_PROMPT = "Point = %d. Continue play? ([y]/n) ";
  private static final String SUMMARY = "wins = %d; losses = %d; net = %d; return = %3.2f%%%n";
  
  private boolean firstPlay = true;
  
  /**
   * Presents a simple, console-based, solitaire craps game, supporting 
   * (essentially) unit pass line bets only. 
   * <p> 
   * The program employs the {@link StateMachine} class, configured with
   * simple lambda callbacks for presenting the current game status and
   * obtaining user input to continue or stop play. 
   * 
   * @param args  Command-line arguments (ignored).
   */
  public static void main(String[] args) {
    new ConsoleGame().play();
  }

  private void play() {
    StateMachine croupier = new StateMachine();
    croupier.setDisplay((roll)-> 
        System.out.printf(ROLL_DISPLAY, Arrays.toString(roll), roll[0] + roll[1]));
    croupier.setPlayable((wins, losses) -> {
      boolean proceed = firstPlay || proceed(PLAY_PROMPT, wins, losses, wins - losses);
      firstPlay = false;
      return proceed;
    });
    croupier.setContinuable((point) -> proceed(POINT_PROMPT, point));
    croupier.play();
    int wins = croupier.getWins();
    int losses = croupier.getLosses();
    int net = wins - losses;
    float returnRate = (float) net / (wins + losses); 
    System.out.printf(SUMMARY, wins, losses, net, 100 * returnRate);
  }
  
  private static boolean proceed(String format, Object... args) {
    char choice;
    do {
      String input = System.console().readLine(format, args).trim();
      choice = (input.length() > 0) ? input.toLowerCase().charAt(0) : 'y';
    } while (choice != 'y' && choice != 'n');
    return (choice == 'y');
  }
  
}
