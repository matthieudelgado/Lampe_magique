package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette classe permet de lancer le serveur de l'application
 */
public class Server {

	public static void main(String[] args){
		
		try {
			//TODO numero de port en parametre
			ServerSocket ss = new ServerSocket(5555);
			
			while(true){
				Socket socket = ss.accept();
				new ThreadServer(socket).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
