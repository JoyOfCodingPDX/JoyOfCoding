package edu.pdx.cs410J.security;

import java.io.*;
import java.net.*;

/**
 * This class represents a game console that can play many games.
 */
public class GameConsole {

  /**
   * Called from the Game to write a game's preferences file.
   *
   * @return <code>true</code> if the preferences were sucessfully
   * written (note that we don't want the name of the preferences file
   * escaping!)
   */
  public boolean writePreferences(Game game, String prefs) {
    // Write to a file in the ${user.home} preferences directory
    String home = System.getProperty("user.home");
    try {
      File dir = new File(home, ".games");
      if (!dir.exists()) {
	dir.mkdirs();
      }

      File file = new File(dir, game.getName());
      FileWriter fw = new FileWriter(file);
      fw.write(prefs);
      fw.flush();
      fw.close();
    } catch (IOException ex) {
      return false;
    }

    return true;
  }

  /**
   * Called from the Game to read a game's preferences file.
   *
   * @return <code>null</code> if the preferences could not be read
   */
  public String readPreferences(Game game) {
    String home = System.getProperty("user.home");
    try {
      File dir = new File(home, ".games");
      if (!dir.exists()) {
	dir.mkdirs();
      }

      File file = new File(dir, game.getName());
      if (!file.exists()) {
	// No preferences
	return("");

      } else if (file.isDirectory()) {
	return(null);
      }

      StringBuffer sb = new StringBuffer();
      char[] arr = new char[1024];
      FileReader fr = new FileReader(file);
      while (fr.ready()) {
	fr.read(arr, 0, arr.length);
	sb.append(arr);
      }

      return sb.toString();

    } catch (IOException ex) {
      return null;
    }
  }


  /**
   * Loads a <code>Game</code> from a given URL.
   */
  public Game loadGame(String gameName, String gameURL) 
    throws Exception {

    // Use a URLClassLoader to load the Game class
    URL[] urls = { new URL(gameURL) };
    URLClassLoader loader = new URLClassLoader(urls);
    Class gameClass = loader.loadClass(gameName);

    // Make an instance of the Game class and return it
    Game game = (Game) gameClass.newInstance();
    return game;
  }

  /**
   * The command line contains the name of the game and a URL from
   * where to load it.
   */
  public static void main(String[] args) {
    String gameName = args[0];
    String gameURL = args[1];

    GameConsole console = new GameConsole();

    try {
      Game game = console.loadGame(gameName, gameURL);
      game.play(console);

    } catch (Exception ex) {
      System.err.println("** Could not load game " + gameName + 
			 " from " + gameURL);
      System.err.println("** " + ex);
    }
  }
}
