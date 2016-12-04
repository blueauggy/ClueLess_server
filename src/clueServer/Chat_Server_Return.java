package clueServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Chat_Server_Return implements Runnable {

	Socket SOCK;
	private Scanner INPUT;
	//private PrintWriter OUT;
	
	public Chat_Server_Return(Socket X){
		
		this.SOCK=X;
	}
	
	public void CheckConnection() throws Exception{
		if(!SOCK.isConnected()){
			for (int i=1; i<=Lobby.ConnectionArray.size();i++){
				if(Lobby.ConnectionArray.get(i)==SOCK){
					Lobby.ConnectionArray.remove(i);
				}
			}
			for (int i=1; i<=Lobby.ConnectionArray.size();i++){
				Socket tmp_sock=(Socket) Lobby.ConnectionArray.get(i-1);
				PrintWriter tmp_out=new PrintWriter(tmp_sock.getOutputStream());
				tmp_out.println(tmp_sock.getLocalAddress().getHostName() + " is disconnected");
				tmp_out.flush();
				
				System.out.println(tmp_sock.getLocalAddress().getHostName() + " is disconnected");
				
			}
		}
	}
	
	
	public void run(){
		try
		{
			INPUT=new Scanner (SOCK.getInputStream());
			//PrintWriter OUT=new PrintWriter(SOCK.getOutputStream());
			
			while(true)
			{
				CheckConnection();
				if(!INPUT.hasNext())
				{
					return;
				}
				Server server = Server.getInstance();
				String message=INPUT.nextLine();
				//Status Messages
				if(message.startsWith("----"))
				{
					String status = message.substring(4);
					System.out.println("'"+status+"'");
					if(status.equals("GAME START"))
					{
						server.setPlayerCount(Lobby.ConnectionArray.size());
						server.initialize();
		
						System.out.println("Current Users:");
						ArrayList <String> names = server.getPeopleNames();
						//Send cards to each player
						//TODO: Close pw??
						for(int i=0; i<Lobby.ConnectionArray.size(); i++)
						{					
							String plName = names.get(i);							
							Socket tmpSock=(Socket) Lobby.ConnectionArray.get(i);
							PrintWriter pw = new PrintWriter(tmpSock.getOutputStream());
							
							//printBoardState
							pw.println(BoardState.sendBoardState(true));
							
							//Sending player name to each player
							pw.println("----Start_User:"+plName);
							
							//Sending cards for each player
							pw.println(pushCards(server.getCards(plName)));
							pw.println("----Game_Commence");
							
							pw.flush();
					
						}
					}
					else if(status.startsWith("BoardState Turn:"))
					{
						String value = status.split(":")[1];
						server.setTurnCounter(Integer.parseInt(value));
					}
					else if(status.startsWith("BoardState CP:"))
					{
						String value = status.split(":")[1];
						server.setCurPlayer(value);
					}
					else if(status.startsWith("BoardState:"))
					{
						BoardState.recieveBoardState(status);
						System.out.println("Recieved Board State");
						System.out.println(BoardState.sendBoardState(true));
						for(int i=0; i<Lobby.ConnectionArray.size(); i++)
						{					
							Socket tmpSock=(Socket) Lobby.ConnectionArray.get(i);
							PrintWriter pw = new PrintWriter(tmpSock.getOutputStream());
							
							System.out.println("Sending "+i+" copy");
							pw.println(BoardState.sendBoardStateTurn());
							pw.println(BoardState.sendBoardStateCurPlayer());
							pw.println(BoardState.sendBoardState(false));
							pw.flush();
						}						
					}
				}
				else
				{
					System.out.println("CLIENT Said: " + message);
					pushMessage(message);
				}	
			} 		
		} catch (Exception X){
			System.out.println(X);
		}
		
	}
	public void pushMessage(String message) throws IOException
	{
		for(int i=1; i<=Lobby.ConnectionArray.size(); i++)
		{
			Socket tmp_sock=(Socket) Lobby.ConnectionArray.get(i-1);
			PrintWriter tmp_out=new PrintWriter(tmp_sock.getOutputStream());
			tmp_out.println(message);
			tmp_out.flush();
			System.out.println("Sent to: " + tmp_sock.getLocalAddress().getHostName());
		}
	}
	
	public String pushCards(ArrayList<Card> cards)
	{
		String output = "----Start_Cards:";
		for (Card c : cards)
		{
			output = output + c.getCardName() + ","+c.getCardType()+";";
		}
		return output.substring(0, output.length()-1);
	}	
}
