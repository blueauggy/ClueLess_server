package clueServer;

import java.util.ArrayList;

/**
 * 
 * Board State that is passed around from client to server
 * 
 */
public class BoardState
{
  private ArrayList<String> persons;	
  private ArrayList<Integer> rows;
  private ArrayList<Integer> columns;
  
  /**
   * Parses the string/json that is passed from client to server and populates the BoardState object
   * TODO: Needs to be finished.
   */
  public BoardState parseBoardState(String input)
  {
	  return null;
  }
  
  /**
   * Receives boardState from Client
   */
  public static void recieveBoardState(String state)
  {
	  Server server = Server.getInstance();
	  String [] positions = state.split(":")[1].split(";");
	  for (String pos : positions)
	  {
		  String[] parts = pos.split(",");
		  String name = parts[0];
		  int row = Integer.parseInt(parts[1]);
		  int col = Integer.parseInt(parts[2]);
		  for (Player p :server.players)
		  {
			  if(p.getName().equals(name))
			  {
				  p.setColumn(col);
				  p.setRow(row);
			  }
		  }
	  }
  }
  /**
   * Sends board state to client
   * Boolean initial is whether or not it is the initial board state at the beginning of the game
   * @return
   */
  public static String sendBoardState(Boolean initial)
  {
	  String output="";
	  
	  if (initial)
	  {
		  output += "----Start_Board:";
	  }
	  else
	  {
		  output += "----BoardState:";
	  }
	  Server server = Server.getInstance();
	  for (Player p : server.players)
	  {
		  if(initial)
		  {
			  output = output + p.getName()+","
					  +p.getRow()+","
					  +p.getColumn()+","
					  +Integer.toString(p.getColor().getRGB())+";";
		  }
		  else
		  {
			  output = output + p.getName()+","+p.getRow()+","+p.getColumn()+";";
		  }
	  }
	  return output.substring(0, output.length()-1);
  }
  
  public static String sendBoardStateTurn()
  {
	System.out.println("sendBoardStateTurn:" +Server.getInstance().getTurnCounter());
	return "----BoardState Turn:"+Server.getInstance().getTurnCounter();  
  }
  
  public static String sendBoardStateCurPlayer()
  {
	  return "----BoardState CP:"+Server.getInstance().getCurPlayer();
  }
  
  /*
   * Form PlayerGuess:<Orig Player>:<Guess Player>,<Guess Weapon>,<Guess Room>
   */
  public static String parseGuess(String message)
  {
	  Server serv = Server.getInstance();
	  Player origPlayer = serv.retPlayer(message.split(":")[1]);
	  String [] guess = message.split(":")[2].split(",");
	  Card guessPlayer = new Card(guess[0], Card.CardType.PERSON);
	  Card guestWeapon = new Card (guess[1], Card.CardType.WEAPON);
	  Card guestRoom = new Card (guess[2], Card.CardType.ROOM);
	  System.out.println("Recieved Guess ");
	  
	  return getGuessAnswer(origPlayer, guessPlayer, guestWeapon, guestRoom);
  }
  
  public static String getGuessAnswer(Player origPlayer, Card guessPlayer, Card guessWeapon, Card guessRoom)
  {
	  Server serv = Server.getInstance();
	  int index = -1;
	  for (int i=0; i<serv.players.size(); i++)
	  {
		  if (serv.players.get(i).equals(origPlayer))
		  {
			  index=i;
		  }
	  }
	  if (index==-1)
	  {
		  return "";
	  }
	  int playersTried =0;
	  int numPlayers = serv.players.size();
	  while(playersTried < numPlayers)
	  {
		  if(serv.players.get(index).equals(origPlayer))
		  {
			  index = (index+1) % numPlayers;
			  playersTried = playersTried +1;
			  continue;
		  }
		  ArrayList<Card> plCards = serv.players.get(index).getCards();
		  for(Card c : plCards)
		  {
			  if(c.equals(guessPlayer) || c.equals(guessWeapon) || c.equals(guessRoom))
			  {
				  return "----Guess Result:"+c.getCardName();
			  }
		  }
		  index = (index+1) % numPlayers;
		  playersTried = playersTried +1;
	  }
	  return "----Guess Result:NONE";
  }
  
  public ArrayList<String> getPersons()
  {
	  return this.persons;
  }
  
  public ArrayList<Integer> getRows()
  {
	  return this.rows;
  }
  
  public ArrayList<Integer> getCols()
  {
	  return this.columns;
  }
  
  public static Boolean validateBoardState(BoardState state)
  {
	  if ((state.persons.size() != state.rows.size()) &&  (state.rows.size() != state.columns.size()))
		  return false;
	  return true;
  }
  

  
}
