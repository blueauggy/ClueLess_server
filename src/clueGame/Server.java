package clueGame;

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

  public static final String peopleConfigFile = "CluePeople.txt";
  public static final String weaponConfigFile = "ClueWeapons.txt";
  private Map<Character, String> legend = new HashMap<Character, String>();
  private ArrayList<String> peopleNames;
  private ArrayList<String> weaponNames;
  private ArrayList<String> roomNames;
  public ArrayList<Player> players = new ArrayList<Player>();
  private ArrayList<Card> cards = new ArrayList<Card>();
  private Guess Solution;
  private int turnCounter;
  private static Server theInstance = new Server();
  
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
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
  
  /**
   * Error checking with people list. Loads people config.
   */
  public void loadPeopleConfig() throws FileNotFoundException
  {
    this.peopleNames = new ArrayList<String>();
    InputStream is = getClass().getResourceAsStream("/data/CluePeople.txt");
    Scanner peopleConfig = new Scanner(is);
    while (peopleConfig.hasNextLine())
    {
      String line = peopleConfig.nextLine();
      Player player = new Player();

      player.updateAttributes(line);
      
      this.cards.add(new Card(player.getName(), Card.CardType.PERSON));
      this.peopleNames.add(player.getName());
      
      this.players.add(player);
    }
    peopleConfig.close();
  }
  
  /**
   * Basic error checking on weapon configurations; loads weapons. 
   * @throws FileNotFoundException
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

  /** Chooses
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
  }
  
  
  /**
   * Check accusation against final solution. Return false if all three cards don't match.
   */
  public boolean checkAccusation(Guess guess)
  {
    return (guess.person.equalsIgnoreCase(this.Solution.person)) && 
      (guess.weapon.equalsIgnoreCase(this.Solution.weapon)) && 
      (guess.room.equalsIgnoreCase(this.Solution.room));
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
  
  /**
   * Returns player class for whoseTurn it is currently
   */
  public Player getwhoseTurn()
  {
	  int playerNum=0;
	  playerNum = (this.turnCounter + 1) % this.players.size();
      return (Player)this.players.get(playerNum);
      
  }
  
  /**
   * Passed a BoardState, it updates the appropriate Player classes and their positions on the server
   * @param state
   */
  public void updatePosition(BoardState state)
  {
	  if (BoardState.validateBoardState(state))
	  {
		  System.err.println("Error updating position from GameState");
		  return;
	  }
	  ArrayList<String> persons = state.getPersons();
	  ArrayList<Integer> stateRows = state.getRows();
	  ArrayList<Integer> stateCols = state.getCols();
	  
	  for(int i=0; i<persons.size(); i++)
	  {
		  for (Player p : players)
		  {
			  if (persons.get(i).equals(p.getName()))
			  {
				  p.setRow(stateRows.get(i));
				  p.setColumn(stateCols.get(i));
			  }
		  }
	  }
  }
  
}
