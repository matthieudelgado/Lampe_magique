package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Before;

import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

public class TestThreadServer {

	private ServerSocket serveurSocket;
	private Socket socket;
	private XMLOutputStream out;
	private XMLInputStream in;
	private static int serveurPort = 5555;
	private ThreadServer threadServ;
	
	@Before
	public void beforeTest()
	{
		try 
		{
			ServerSocket serveurSocket = new ServerSocket(serveurPort);
			threadServ = new ThreadServer(socket);
			threadServ.start();
		}catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
