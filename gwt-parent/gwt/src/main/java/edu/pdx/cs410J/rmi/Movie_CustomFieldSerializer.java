package edu.pdx.cs410J.rmi;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

import java.util.Map;

/**
 * A custom field serializer for the {@link edu.pdx.cs410J.rmi.Movie} class.  This allows us to use
 * <code>Movie</code>s with GWT-RPC services without modifying the <code>Movie</code>
 * class to have a zero-argument constructor
 *
 * http://code.google.com/p/wogwt/wiki/CustomFieldSerializer
 */
public class Movie_CustomFieldSerializer {

  public static void deserialize(SerializationStreamReader reader, Movie movie)
    throws SerializationException {

  }

  public static Movie instantiate(SerializationStreamReader reader)
    throws SerializationException {


    long id = reader.readLong();
    String title = reader.readString();
    int year = reader.readInt();
    Map<String, Long> characters = (Map<String, Long>) reader.readObject();

    Movie movie = new Movie(title, year);
    movie.setId(id);
    for (Map.Entry<String, Long> entry : characters.entrySet()) {
      movie.addCharacter(entry.getKey(), entry.getValue());
    }
    return movie;
  }

  public static void serialize(SerializationStreamWriter writer, Movie movie)
    throws SerializationException {

    writer.writeLong(movie.getId());
    writer.writeString(movie.getTitle());
    writer.writeInt(movie.getYear());
    writer.writeObject(movie.getCharacters());
  }
}
