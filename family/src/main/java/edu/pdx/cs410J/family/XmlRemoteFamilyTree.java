package edu.pdx.cs410J.family;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * This class is a remote family tree whose contents are read from and
 * saved to an XML file.  It extends <code>UnicastRemoteObject</code>
 * because it is going to be bound into the RMI registry.
 */
@SuppressWarnings("serial")
public class XmlRemoteFamilyTree extends UnicastRemoteObject 
  implements RemoteFamilyTree {

  /** The underlying family tree whose contents this remote family
   * tree serves up. */
  private transient FamilyTree tree;

  /** The XML file that serves as the source of this family tree */
  private transient File xmlFile;

  /** The highest id in the family tree */
  private int highestId;

  /** Maps ids to their RemotePerson.  This way there is always a
   * one-to-one correspondence between a Person and a RemotePerson */
  private Map<Integer, RemotePerson> remotePersons = new TreeMap<Integer, RemotePerson>();

  /** Maps a Long that represents the husband and wife to their
   * RemoteMarriage */
  private Map<Long, RemoteMarriage> remoteMarriages = new TreeMap<Long, RemoteMarriage>();

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates a new <code>XmlRemoteFamilyTree</code> that gets its data
   * from a given XML file.
   *
   * @throws FamilyTreeException
   *         A problem occurred while parsing the XML file
   */
  public XmlRemoteFamilyTree(File xmlFile) 
    throws RemoteException, IOException, FamilyTreeException {
    super();   // Register this object with RMI runtime

    if (xmlFile.exists()) {
      out.println("Reading Family Tree from " + xmlFile);
      XmlParser parser = new XmlParser(xmlFile);
      this.tree = parser.parse();

      // Compute the highest id of any person
      Iterator iter = this.tree.getPeople().iterator();
      while (iter.hasNext()) {
        Person person = (Person) iter.next();
        if (person.getId() > this.highestId) {
          this.highestId = person.getId();
        }
      }

    } else {
      this.tree = new FamilyTree();
      this.highestId = 0;
    }

    this.xmlFile = xmlFile;
  }

  //////////////////////  Instance Methods  //////////////////////

  /**
   * Helper method that manages the cache of RemotePersons
   */
  private RemotePerson getRemotePerson(Person person) 
    throws RemoteException {

    if (person == null) {
      return null;
    }

    int id = person.getId();

    RemotePerson rPerson = 
      (RemotePerson) this.remotePersons.get(new Integer(id));
    if (rPerson == null) {
      // Is there a person with that id
      if (!this.tree.containsPerson(id)) {
        return null;
      }

      rPerson = new RemotePersonImpl(this, person);
      this.remotePersons.put(new Integer(person.getId()), rPerson);
    }
    
    return rPerson;
  }

  public RemotePerson createPerson(Person.Gender gender) throws RemoteException {
    Person person = new Person(++this.highestId, gender);
    this.tree.addPerson(person);
    return getRemotePerson(person);
  }

  public RemotePerson getPerson(int id) throws RemoteException {
    return getRemotePerson(this.tree.getPerson(id));
  }

  public RemotePerson getPerson(String firstName, String lastName) 
    throws RemoteException {
    Person person = null;

    Iterator people = this.tree.getPeople().iterator();
    while (people.hasNext()) {
      Person p = (Person) people.next();
      if (p.getFirstName().equals(firstName) &&
          p.getLastName().equals(lastName)) {
        if (person == null) {
          person = p;

        } else {
          String s = "Multiple people named \"" + firstName + " " +
            lastName + " exist: " + p + " AND " + person;
          throw new IllegalArgumentException(s);
        }
      }
    }

    if (person == null) {
      return null;

    } else {
      return getPerson(person.getId());
    }
  }

  public RemoteMarriage getMarriage(int husbandId, int wifeId) 
    throws RemoteException {
    
    // Make sure that both the husband and the wife exist
    if (!this.tree.containsPerson(husbandId)) {
      String s = "Could not find person with id " + husbandId;
      throw new IllegalArgumentException(s);

    } else if (!this.tree.containsPerson(wifeId)) {
      String s = "Could not find person with id " + wifeId;
      throw new IllegalArgumentException(s);
    }

    Person husband = this.tree.getPerson(husbandId);
    Iterator marriages = husband.getMarriages().iterator();
    while (marriages.hasNext()) {
      Marriage marriage = (Marriage) marriages.next();
      if (marriage.getWife().getId() == wifeId) {
        return getRemoteMarriage(marriage);
      }
    }

    return null;
  }

  /**
   * Helper method that maintains the map of RemoteMarriages
   */
  private RemoteMarriage getRemoteMarriage(Marriage marriage) 
    throws RemoteException {

    long key = (((long) marriage.getHusband().getId()) << 32) | 
      ((long) marriage.getWife().getId());

    RemoteMarriage rMarriage =
      (RemoteMarriage) this.remoteMarriages.get(new Long(key));
    if (rMarriage == null) {
      rMarriage = new RemoteMarriageImpl(marriage);
      this.remoteMarriages.put(new Long(key), rMarriage);
    }

    return rMarriage;
  }

  public RemoteMarriage createMarriage(int husbandId, int wifeId) 
    throws RemoteException {

    // Make sure that both the husband and the wife exist
    if (!this.tree.containsPerson(husbandId)) {
      String s = "Could not find person with id " + husbandId;
      throw new IllegalArgumentException(s);

    } else if (!this.tree.containsPerson(wifeId)) {
      String s = "Could not find person with id " + wifeId;
      throw new IllegalArgumentException(s);
    }

    Person husband = this.tree.getPerson(husbandId);
    Person wife = this.tree.getPerson(wifeId);

    Marriage marriage = new Marriage(husband, wife);
    husband.addMarriage(marriage);
    wife.addMarriage(marriage);
    return getRemoteMarriage(marriage);
  }

  public Collection<RemotePerson> getLiving() throws RemoteException {
    Collection<RemotePerson> living = new ArrayList<RemotePerson>();

    Iterator people = this.tree.getPeople().iterator();
    while (people.hasNext()) {
      Person person = (Person) people.next();
      if (person.getDateOfBirth() != null &&
          person.getDateOfDeath() == null) {
        living.add(getRemotePerson(person));
      }
    }

    return living;
  }

  public Collection<RemotePerson> getLiving(Date date) throws RemoteException {
    Collection<RemotePerson> alive = new ArrayList<RemotePerson>();

    Iterator people = this.tree.getPeople().iterator();
    while (people.hasNext()) {
      Person person = (Person) people.next();
      Date dob = person.getDateOfBirth();
      Date dod = person.getDateOfDeath();

      if (dob != null && date.before(dob)) {
        continue;
      }

      if (dod != null && date.after(dod)) {
        continue;
      }

      if (dob == null && dod == null) {
        continue;
      }

      alive.add(getRemotePerson(person));
    }

    return alive;
  } 

  public void shutdown() throws IOException, RemoteException {
    // Write the contents of this family tree back to the XML file
    out.println("Writing family tree to " + this.xmlFile.getPath());
    XmlDumper dumper = new XmlDumper(this.xmlFile.getPath());
    dumper.dump(this.tree);

    UnicastRemoteObject.unexportObject(this, false /* force */);
  }

  //////////////////////////  Main Program  //////////////////////////

  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Prints usage information about this program
   */
  private static void usage(String s) {
    err.println("\n** " + s + "\n");
    err.println("usage: java XmlRemoteFamilyTree [-start xmlFile " +
                "| -stop] familyName host port");
    err.println("");
    err.println("This program creates a new XmlRemoteFamilyTree " +
                "that reads its contents from a given XML file and " +
                "binds it into the RMI registry.  If the XML file " +
                "doesn't exist, a new one will be created");
    System.exit(1);
  }

  public static void main(String[] args) {
    String xmlFileName = null;
    String familyName = null;
    String host = null;
    int port = -1;
    String command = null;

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-start")) {
        if (++i >= args.length) {
          usage("Missing XML file");
        }
        xmlFileName = args[i];

        command = "START";

      } else if (args[i].equals("-stop")) {
        command = "STOP";

      } else if (familyName == null) {
        familyName = args[i];

      } else if (host == null) {
        host = args[i];

      } else if (port == -1) {
        try {
          port = Integer.parseInt(args[i]);

        } catch (NumberFormatException ex) {
          usage("Invalid port: " + args[i]);
        }

      } else {
        usage("Spurious command line: " + args[i]);
      }
    }

    if (command == null) {
      usage("Missing command");
    }

    if (familyName == null) {
      usage("Missing family name");
    }

    if (host == null) {
      usage("Missing host name");
    }

    if (port == -1) {
      usage("Missing port number");
    }

    if (command.equals("START")) {
      // Install an RMISecurityManager, if there is not a
      // SecurityManager already installed
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new RMISecurityManager());
      }

      String name = "rmi://" + host + ":" + port + "/" + familyName;

      try {
        XmlRemoteFamilyTree tree =
          new XmlRemoteFamilyTree(new File(xmlFileName));
        Naming.rebind(name, tree);
        out.println("Successfully bound XmlRemoteFamilyTree");

      } catch (Exception ex) {
        ex.printStackTrace(System.err);
      }


    } else if (command.equals("STOP")) {
      // Install an RMISecurityManager, if there is not a
      // SecurityManager already installed
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new RMISecurityManager());
      }

      String name = "rmi://" + host + ":" + port + "/" + familyName;

      try {
        RemoteFamilyTree tree = 
          (RemoteFamilyTree) Naming.lookup(name);
        tree.shutdown();
        Naming.unbind(name);

      } catch (Exception ex) {
        ex.printStackTrace(err);
      }
      

    } else {
      String s = "Unknown command: " + command;
      throw new IllegalStateException(s);
    }
  }

}
