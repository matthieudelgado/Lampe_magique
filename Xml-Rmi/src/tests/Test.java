package tests;

import java.io.IOException;
import java.net.Socket;
import org.junit.*;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette class lance les tests de l'application
 */
public class Test{
	private XMLOutputStream out;
	private XMLInputStream in;
	private Socket socket;
	
	@Before
	/**
	 * Cette methode initialise la socket pour les tests a suivre
	 */
	public void beforeTest(){
		try {
			socket = new Socket("localhost", 5555);
			out = new XMLOutputStream(socket.getOutputStream());
			in = new XMLInputStream(socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	/**
	 * Cette methode ferme le socket en fin de test
	 */
	public void afterTest(){
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
