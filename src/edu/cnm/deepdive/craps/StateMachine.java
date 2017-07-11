/**
 * StateMachine.java
 */
package edu.cnm.deepdive.craps;

import java.util.Random;

/**
 * This class implements a multi-round state machine for a solitaire craps
 * game, with running tally of wins and losses. At each player decision point,
 * an instance of a functional interface callback used to present the current 
 * roll, and another functional interface instance is interrogated to determine 
 * whether play should proceed/continue.
 *  
 * @author Nicholas Bennett
 */
public class StateMachine {

  /**
   * Callback interface for determining whether a round of play should begin.
   */
  public interface Playable {
    
    /**
     * Returns a flag indicating whether the next round of play should begin.
     * 
     * @param wins      Current tally of wins.
     * @param losses    Current tally of losses.
     * @return          Proceed/continue flag.
     */
    boolean playAgain(int wins, int losses);
    
  }
  
  /**
   * Callback interface for determining whether rolling for the current point
   * should continue.  
   */
  public interface Continuable {
    
    /**
     * Returns a flag indicating whether the dice should be rolled for the
     * current point. A {@code false} value indicates not only that the current
     * round should end, but that the entire game should end as well.
     * 
     * @param point     Current point.
     * @return          Continue flag.
     */
    boolean continuePlay(int point);
    
  }
  
  /**
   * Callback interface for advising the class consumer of the current roll.
   */
  public interface Display {
    
    /**
     * Receives an {@code int[]} of length 2, containing the current roll's
     * dice values.
     *  
     * @param roll      Dice values.
     */
    void update(int[] roll);
    
  }
  
  /**
   * Error raised when the {@link StateMachine#play()} method is called before
   * one or more of the necessary callbacks have been set. 
   */
  public static class MissingCallbackError extends Error {

    private static final long serialVersionUID = 2149910247463420445L;
    
    private MissingCallbackError() {
      super("One or more callbacks have not been assigned.");
    }

  }
  
  /**
   * An {@code enum} encapsulating the states of play in one or more rounds of
   * craps.
   */
  public enum PlayState {
    COME_OUT, CONTINUE, DONE;
  }
  
  private PlayState state = PlayState.COME_OUT;
  private int wins = 0;
  private int losses = 0;

  private Playable playable = null;
  private Continuable continuable = null;
  private Display display = null;

  private Random rng = new Random();
  
  /**
   * Starts one or more rounds, tallying the pass line bets won and lost. At
   * each decision point (i.e. where the shooter must make the decision to 
   * proceed or not), and after each roll, a method on the associated callback 
   * instance is invoked for the relevant action/response.  
   */
  public void play() {
    if (playable == null || continuable == null || display == null) {
      throw new MissingCallbackError();
    }
    while (state == PlayState.COME_OUT && playable.playAgain(wins, losses)) {
      int[] roll = {
        rng.nextInt(6) + 1,  
        rng.nextInt(6) + 1  
      };
      int sum = roll[0] + roll[1];
      display.update(roll);
      int point = sum;
      switch (sum) {
        case 2:
        case 3:
        case 12:
          losses++;
          break;
        case 7:
        case 11:
          wins++;
          break;
        default:
          state = PlayState.CONTINUE;
          while (state == PlayState.CONTINUE && continuable.continuePlay(point)) {
            roll = new int[] {
              rng.nextInt(6) + 1,  
              rng.nextInt(6) + 1  
            };
            sum = roll[0] + roll[1];
            display.update(roll);
            if (sum == point) {
              wins++;
              state = PlayState.COME_OUT;
            } else if (sum == 7) {
              losses++;
              state = PlayState.COME_OUT;
            }
          }
          if (state == PlayState.CONTINUE) {
            losses++;
            state = PlayState.DONE;
          }
      }
    }
    state = PlayState.DONE;
  }
  
  /**
   * Returns the current play state.
   *  
   * @return  state
   */
  public PlayState getState() {
    return state;
  }
  
  /**
   * Sets the current play state. This method is intended for use by subclasses
   * that override the {@link #play()} method, to implement a non-standard 
   * variant of craps.
   * 
   * @param state   new state
   */
  protected void setState(PlayState state) {
    this.state = state;
  }
  
  /**
   * Returns the current tally of wins.
   * 
   * @return    wins
   */
  public int getWins() {
    return wins;
  }
  
  /**
   * Sets the current tally of wins. This method is intended for use by 
   * subclasses that override the {@link #play()} method, to implement a 
   * non-standard variant of craps.
   * 
   * @param wins   new wins tally
   */
  protected void setWins(int wins) {
    this.wins = wins;
  }
  
  /**
   * Returns the current tally of wins.
   * 
   * @return   losses
   */
  public int getLosses() {
    return losses;
  }
  
  /**
   * Sets the current tally of losses. This method is intended for use by 
   * subclasses that override the {@link #play()} method, to implement a 
   * non-standard variant of craps.
   * 
   * @param losses    new losses tally
   */
  protected void setLosses(int losses) {
    this.losses = losses;
  }
  
  /**
   * Returns the callback instance used to determine whether a new round of
   * craps should begin.
   *  
   * @return   callback instance
   */
  public Playable getPlayable() {
    return playable;
  }
  
  /**
   * Sets the callback instance used to determine whether a new round of
   * craps should begin. This method must be invoked before {@link #play()}.
   * 
   * @param playable   callback instance
   */
  public void setPlayable(Playable playable) {
    this.playable = playable;
  }
  
  /**
   * Returns the callback instance used to determine whether the dice should
   * be rolled for the current point.
   *  
   * @return    callback instance
   */
  public Continuable getContinuable() {
    return continuable;
  }
  
  /**
   * Sets the callback instance used to determine whether the dice should be
   * rolled for the current point.
   * 
   * @param continuable   callback instance
   */
  public void setContinuable(Continuable continuable) {
    this.continuable = continuable;
  }
  
  /**
   * Returns the callback instance used to present the current dice roll.
   * 
   * @return    callback instance
   */
  public Display getDisplay() {
    return display;
  }
  
  /**
   * Sets the callback instance used to present the current dice roll.
   * 
   * @param display   callback instance
   */
  public void setDisplay(Display display) {
    this.display = display;
  }
  
}
