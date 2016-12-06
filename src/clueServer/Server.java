package clueServer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * The majority of the logic of what I have made so far.
 *
 */
public class Server
{

  public String boardConfigFile; 
  private Map<Character, String> legend = new HashMap<Character, String>();
  private ArrayList<String> peopleNames;
  private ArrayList<String> weaponNames;
  private ArrayList<String> roomNames;
  public ArrayList<Player> players = new ArrayList<Player>();
  private ArrayList<Card> cards = new ArrayList<Card>();
  private Guess Solution;
  private int turnCounter;
  private static Server theInstance = new Server();
  private int playerCount;
  private String curPlayer;
  
 /**
  * 
  * Returns the instance of the board
  */
  public static Server getInstance()
  {
    return theInstance;
  }
  
  /**
   * Starts the game, finds your possible neighbors for moves and deals cards.
 * @throws Exception 
   */
  public void initialize()
  {
	this.turnCounter=0;
    loadConfigFiles();
    pickSolution();
    deal();
  }
  
  /**
   * Load people, weapon configurations and errors if there is a failure to load.
   */
  public void loadConfigFiles()
  {
    try
    {      
      loadPeopleConfig();
      loadWeaponConfig();
      loadRoomConfig();
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
  
  /**
   * Loads player config with list of players and has populated playerCount for number of players that joined the game
   * Add players down the list until playerCount=0
   */
  public void loadPeopleConfig() throws FileNotFoundException
  {
    this.peopleNames = new ArrayList<String>();
    InputStream is = getClass().getResourceAsStream("/data/CluePeople.txt");
    Scanner peopleConfig = new Scanner(is);
    while (peopleConfig.hasNextLine() && playerCount > 0)
    {
      String line = peopleConfig.nextLine();
      Player player = new Player();

      player.updateAttributes(line);
      
      this.cards.add(new Card(player.getName(), Card.CardType.PERSON));
      this.peopleNames.add(player.getName());
            
      this.players.add(player);
      playerCount--;
    }
    peopleConfig.close();
  }
  
  /**
   * Basic error checking on weapon configurations; loads weapons. 
   */
  public void loadWeaponConfig()
    throws FileNotFoundException
  {
    this.weaponNames = new ArrayList<String>();
    
    InputStream is = getClass().getResourceAsStream("/data/ClueWeapons.txt");
    Scanner weaponsConfig = new Scanner(is);
    while (weaponsConfig.hasNextLine())
    {
      String line = weaponsConfig.nextLine();
      this.cards.add(new Card(line.trim(), Card.CardType.WEAPON));
      this.weaponNames.add(line.trim());
    }
    weaponsConfig.close();
  }

  /**
   * Loads Rooms and adds the rooms to the current card deck
   * @throws FileNotFoundException
   */
  public void loadRoomConfig() throws FileNotFoundException
  {
    InputStream is = getClass().getResourceAsStream("/data/CR_ClueLegend.txt");
    Scanner roomConfig = new Scanner(is);
    this.roomNames = new ArrayList<String>();
    while (roomConfig.hasNextLine())
    {
      String line = roomConfig.nextLine();
      String[] tokens = line.split(",");
      if (tokens.length != 3)
      {
        roomConfig.close();
        System.err.println("Room file format incorrect " + line);
      }
      Character key = new Character(tokens[0].charAt(0));
      String roomName = tokens[1].trim();
      this.legend.put(key, roomName);
      
      String roomType = tokens[2].trim();
      if ((!roomType.equals("Card")) && (!roomType.equals("Other")))
      {
        roomConfig.close();
        System.err.println("Room file format incorrect " + line);
      }
      if (roomType.equals("Card"))
      {
        this.cards.add(new Card(tokens[1].trim(), Card.CardType.ROOM));
        this.roomNames.add(roomName);
      }
    }
    roomConfig.close();
  }

  /** 
   *  Chooses the final answer (solution) for the game and stores the variable. 
   *  Should only be known to the server
   */
  public void pickSolution()
  {
    this.Solution = new Guess();
    Collections.shuffle(this.cards);
    for (Card card : this.cards)
    {
      if ((this.Solution.person != null) && (this.Solution.weapon != null) && (this.Solution.room != null)) {
        break;
      }
      if ((card.getCardType() == Card.CardType.PERSON) && (this.Solution.person == null)) {
        this.Solution.person = card.getCardName();
      } else if ((card.getCardType() == Card.CardType.ROOM) && (this.Solution.room == null)) {
        this.Solution.room = card.getCardName();
      } else if ((card.getCardType() == Card.CardType.WEAPON) && (this.Solution.weapon == null)) {
        this.Solution.weapon = card.getCardName();
      }
    }
    System.out.println("The solution is: "+this.Solution.person+" - "+this.Solution.weapon+" - "+this.Solution.room);
  }
  
  
  /**
   * Check accusation against final solution. Return false if all three cards don't match.
   */
  public boolean checkAccusation(String person, String weapon, String room)
  {
    return (person.equalsIgnoreCase(this.Solution.person)) && 
      (weapon.equalsIgnoreCase(this.Solution.weapon)) && 
      (room.equalsIgnoreCase(this.Solution.room));
  }
  
  /**
   * Deal initial cards to players.  Card cannot match any portion of the solution.
   * This deals all remaining cards not just three.  Is this correct?
   */
  public void deal()
  {
    int playerNum = 0;
    for (Card card : this.cards) {
      if ((!card.getCardName().equals(this.Solution.person)) && 
        (!card.getCardName().equals(this.Solution.room)) && 
        (!card.getCardName().equals(this.Solution.weapon)))
      {
        playerNum = (playerNum + 1) % this.players.size();
        Player player = (Player)this.players.get(playerNum);
        
        player.addCard(card);
      }
    }
  }
  /**
   * Put code here that establishes the server's connection as a listener on IP:port
 * @throws Exception 
   */
  public void setUpListener(int port) throws Exception
  {
	  new Lobby(port);
  }
  
  
  /**
   * Returns string for name of room from input initial.  Defined by data/CR_ClueLegend.
   */
  public String getRoomName(char initial)
  {
    return (String)this.legend.get(Character.valueOf(initial));
  }
  
  /**
   * Returns list of the rooms names
   */
  public ArrayList<String> getRoomNames()
  {
    return this.roomNames;
  }
  
  /**
   * Returns list of the weapons names
   */
  public ArrayList<String> getWeaponNames()
  {
    return this.weaponNames;
  }
  
  /**
   * Returns list of the people names
   */
  public ArrayList<String> getPeopleNames()
  {
    return this.peopleNames;
  }
  
  /**
   * Returns cards for a given player
   */
  public ArrayList<Card> getCards(String playerName)
  {
	for (Player p : players)
	{
		if (p.getName().equals(playerName))
			return p.getCards();
	}
	return null;
  }
  
  public void setPlayerCount(int count)
  {
	  this.playerCount = count;
  }
  
  public void setTurnCounter(int count)
  {
	  this.turnCounter = count;
  }
  
  public Integer getTurnCounter()
  {
	  return this.turnCounter;
  }
  
  public void setCurPlayer(String player)
  {
	  this.curPlayer = player;
  }
  
  public String getCurPlayer()
  {
	  return this.curPlayer;
  }
  
  public Player retPlayer(String pl)
  {
	  for (Player p : players)
	  {
		  if (p.getName().equals(pl))
		  {
			  return p;
		  }
	  }
	  return null;
  }
  
  /*
   * Also looks for dead players and skips accordingly
   */
  public String retNextPlayer(String prevPlayer)
  {
	  String nextPlayer="";
	  for (int i=0; i<players.size(); i++)
	  {
		  if (players.get(i).getName().equals(prevPlayer))
		  {
			  System.out.println("Names match "+prevPlayer);
			  int index = (i+1) % players.size();
			  while(players.get(index).getIsDead())
			  {
				   System.out.println("Player "+players.get(index).getName()+" is dead...skipping");
				   index = (index+1) % players.size();
			  }
			  nextPlayer = players.get(index).getName();
			  break;
		  }
	  }
	  return nextPlayer;
  }
  
}
