/**
 * 
 */
package edu.cnm.deepdive.craps;

import java.util.Random;

/**
 * @author Sky Link
 *
 */
public class StateMachine {
  
  public enum PlayState {
    COME_OUT, CONTINUE, DONE;
  }
  
  public enum ComeOutResult {
    NATURAL, CRAP_OUT, POINT;
  }
  
  public interface Playable {
    
    boolean playAgain(int wins, int losses);
    
  }
  
  public interface Continuable {
    
    boolean continuePlay(int wins, int losses, int point);
    
  }
  
  public interface Display {
    
    void update(int[] roll);
    
  }
  
  private PlayState state = PlayState.COME_OUT;
  private int wins = 0;
  private int losses = 0 ;
  
  private Playable playable;
  private Continuable continuable;
  private Display display;
  
  private Random rng = new Random();
  
  /**
   * 
   */
  public void play() {
    while (state == PlayState.COME_OUT && playable.playAgain(wins, losses)) {
      int[] roll = {
          rng.nextInt(6) + 1,
          rng.nextInt(6) + 1
      };
      int sum = roll[0] + roll[1];
      display.update(roll);
      ComeOutResult result = ComeOutResult.POINT;
      int point = sum;
      switch (sum) {
        case 2:
        case 3:
        case 12:
          result = ComeOutResult.CRAP_OUT;
          losses++;
          break;
        case 7:
        case 11:
          result = ComeOutResult.NATURAL;
          wins++;
          break;
        default:
          state = PlayState.CONTINUE;
          while (state == PlayState.CONTINUE && continuable.continuePlay(wins, losses, point)) {
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
   * @return the state
   */
  public PlayState getState() {
    return state;
  }
  /**
   * @param state the state to set
   */
  protected void setState(PlayState state) {
    this.state = state;
  }
  /**
   * @return the wins
   */
  public int getWins() {
    return wins;
  }
  /**
   * @param wins the wins to set
   */
  protected void setWins(int wins) {
    this.wins = wins;
  }
  /**
   * @return the losses
   */
  public int getLosses() {
    return losses;
  }
  /**
   * @param losses the losses to set
   */
  protected void setLosses(int losses) {
    this.losses = losses;
  }
  /**
   * @return the playable
   */
  public Playable getPlayable() {
    return playable;
  }
  /**
   * @param playable the playable to set
   */
  public void setPlayable(Playable playable) {
    this.playable = playable;
  }
  /**
   * @return the continuable
   */
  public Continuable getContinuable() {
    return continuable;
  }
  /**
   * @param continuable the continuable to set
   */
  public void setContinuable(Continuable continuable) {
    this.continuable = continuable;
  }
  /**
   * @return the display
   */
  public Display getDisplay() {
    return display;
  }
  /**
   * @param display the display to set
   */
  public void setDisplay(Display display) {
    this.display = display;
  }
  
}
