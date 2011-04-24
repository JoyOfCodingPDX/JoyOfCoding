package edu.pdx.cs410J.security;

/**
 * This interface describes a game.
 */
public interface Game {

  /**
   * Plays the game on a game console
   */
  public void play(GameConsole console);

  /**
   * Returns the name of the game/
   */
  public String getName();
}
