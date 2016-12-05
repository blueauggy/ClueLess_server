package clueServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.PrintWriter;

@SuppressWarnings("resource")
//TODO: Need to close the Sockets
public class Lobby {

	public static ArrayList<Socket> ConnectionArray=new ArrayList<Socket>();
	public static ArrayList<String> CurrentUsers= new ArrayList<String>();
	
	public Lobby(int PORT) 
		throws Exception 
	{
		System.out.println("Lobby");
		
		try{
			ServerSocket SERVER=new ServerSocket(PORT);
			System.out.println("Waiting for clients...");
			
			while(true){
				Socket SOCK= SERVER.accept();
				ConnectionArray.add(SOCK);
				
				AddUserName(SOCK);
							
				Chat_Server_Return CHAT=new Chat_Server_Return(SOCK);
				Thread th =new Thread(CHAT);
				th.start();
			}
			
		} catch (Exception X) {
			System.out.println(X);
		}
				
	}
	
	
	public static void AddUserName(Socket sock) throws Exception {
		Scanner INPUT= new Scanner(sock.getInputStream());
		String UserName=INPUT.nextLine();
		CurrentUsers.add(UserName);
		
		for(int i=1; i<= Lobby.ConnectionArray.size();i++){
			Socket tmp_sock=(Socket) Lobby.ConnectionArray.get(i-1);
			PrintWriter OUT = new PrintWriter(tmp_sock.getOutputStream());
			OUT.println("----CurrentUsers:"+ CurrentUsers);
			OUT.flush();
		}
		System.out.println(UserName + " has joined the game");
	}
	
	
}
