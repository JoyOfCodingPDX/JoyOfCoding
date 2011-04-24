package edu.pdx.cs410J.security;

import java.io.*;

/**
 * This is a little guessing game that counts the number of times it
 * takes the user to guess a number between one and ten.
 */
public class GuessingGame implements Game {

  private PrintStream out = System.out;
  private BufferedReader in = 
    new BufferedReader(new InputStreamReader(System.in));

  public String getName() {
    return "GuessingGame";
  }

  /**
   * Plays the game
   */
  public void play(GameConsole console) {
    int number = ((int) (Math.random() * 10.0)) + 1;

    out.println("I'm thinking of a number between 1 and 10");

    // First get the preferences
    String prefs = console.readPreferences(this);
    if (prefs == null) {
      System.err.println("** Couldn't read preferences");
      return;
    }

    int highScore = -1;
    try {
      highScore = Integer.parseInt(prefs.trim());
      out.println("The high score is: " + highScore);
    } catch (NumberFormatException ex) {
      // Ignore
    }

    // Guess the number
    int guesses = 1;
    while (true) {
      out.print("Your guess: ");
      int guess = getGuess();
      if ((guess < 1) || (guess > 10)) {
	out.println("Guess a number between 1 and 10");

      } else if (guess > number) {
	out.println("Too high!");

      } else if (guess < number) {
	out.println("Too low!");

      } else {
	out.println("You guessed right!");
	break;
      }

      guesses++;
    }

    out.println("It took you " + guesses + " guesses");
    
    if ((highScore == -1) || (guesses < highScore)) {
      out.println("A new high score!");
      prefs = guesses + "";
      if (!console.writePreferences(this, prefs)) {
	System.err.println("** Couldn't write preferences");
      }
    }

    out.println("Thanks for playing");
  }

  /**
   * Reads an <code>int</code> from System.in
   */
  private int getGuess() {
    try {
      return Integer.parseInt(in.readLine());

    } catch (NumberFormatException ex) {
      return -1;

    } catch (IOException ex) {
      return -1;
    }
  }
  
  /**
   * Test program
   */
  public static void main(String[] args) {
    GameConsole console = new GameConsole();
    Game game = new GuessingGame();
    game.play(console);
  }

}

