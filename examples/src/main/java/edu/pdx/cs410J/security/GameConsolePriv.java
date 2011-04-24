package edu.pdx.cs410J.security;

import java.security.*;

/**
 * This class represents a game console that can play many games.
 */
public class GameConsolePriv extends GameConsole {

  /**
   * Note use of doPrivileged.  This says that we trust this code.
   */
  public boolean writePreferences(final Game game, 
				  final String prefs) {
    Boolean b =
      (Boolean) AccessController.doPrivileged(new PrivilegedAction() {
	  public Object run() {
	    boolean b = 
	      GameConsolePriv.super.writePreferences(game, prefs);
	    return new Boolean(b);
	  }
	});
    return b.booleanValue();
  }

  public String readPreferences(final Game game) {
    String prefs = 
      (String) AccessController.doPrivileged(new PrivilegedAction() {
	  public Object run() {
	    return GameConsolePriv.super.readPreferences(game);
	  }
	});
					     
    return prefs;
  }


  /**
   * The command line contains the name of the game and a URL from
   * where to load it.
   */
  public static void main(String[] args) {
    String gameName = args[0];
    String gameURL = args[1];

    GameConsole console = new GameConsolePriv();

    Game game = null;
    try {
      game = console.loadGame(gameName, gameURL);

    } catch (Exception ex) {
      System.err.println("** Could not load game " + gameName + 
			 " from " + gameURL);
      System.err.println("** " + ex);
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    game.play(console);
  }
}
