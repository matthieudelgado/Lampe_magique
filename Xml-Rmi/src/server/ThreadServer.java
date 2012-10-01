package server;

import java.io.IOException;
import java.net.Socket;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

import tools.TestEcritureXML;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

public class ThreadServer extends Thread{
	private Socket socket;
	private XMLOutputStream out;
	private XMLInputStream in;

	public ThreadServer(Socket socket) {
		this.socket = socket;
		try {
			this.out = new XMLOutputStream(socket.getOutputStream());
			this.in = new XMLInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		System.out.println("Un client s'est connecte");
		try { 
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
			Document doc = null;

			while(true) 
			{
				in.recive();
				doc = docBuilder.parse(in);
				doTreatement(doc);
			}
		}
		catch (Exception e){ }
		finally 
		{
			try
			{
				System.out.println("Un client s'est deconnecte");
				socket.close(); 
			}
			catch (IOException e){ }
		}
	}

	private void doTreatement(Document doc) {
		TestEcritureXML.afficherDocument(doc);
	}
}
