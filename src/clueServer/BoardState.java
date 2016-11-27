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
   * Recieves boardState from Client
   */
  public String recieveBoardState(BoardState state)
  {
	  
	  return null;
  }
  /**
   * Sends board state to client
   * @return
   */
  public String sendBoardState()
  {
	  //hook into existing communications
	  //ArrayList<String> a = generateState();
	  return null;
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
