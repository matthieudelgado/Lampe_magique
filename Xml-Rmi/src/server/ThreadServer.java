package server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import objets.Stringable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import tools.TestEcritureXML;
import tools.XMLToObject;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

public class ThreadServer extends Thread implements IServer{
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
		catch (Exception e){ e.printStackTrace();}
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

	private void doTreatement(Document doc) throws Exception {
		TestEcritureXML.afficherDocument(doc);
		//on commence par verifier l'integrite du doc par rapport a la grammaire
		
		//on recherche la méthode appelée
		Node n = doc.getElementsByTagName("methodeName").item(0).getFirstChild();
		String MethodeName = ((Text)n).getData();
		//on recupère les parametres de la methode dans le xml
		ArrayList<Node> paramList = new ArrayList<Node>();
		NodeList params = doc.getElementsByTagName("value");
		Node current = null;
		for(int i = 0; i < params.getLength() ; i++) {
			current = params.item(i);
			if(current.getParentNode().getNodeName().equalsIgnoreCase("param"))
				paramList.add(current.getFirstChild());
		}
		
		
		//on verfie qu'elle existe dans l'inteface IServer
		Class<IServer> itf = IServer.class;
		Method[] methods = itf.getDeclaredMethods();
		Method calledMethod = null;
		ArrayList<Object> args = new ArrayList<Object>();
		Class<?>[] parameterTypes = null;
		boolean trouve = false;
		for(Method m : methods){
			//on commence par verifier si le nom correspond
			if( !m.getName().equalsIgnoreCase(MethodeName)) continue;
			//on verifie ensuite les arguments
			parameterTypes = m.getParameterTypes();
			//on compare le type des parametres avec le xml
			for(int i = 0 ; i < parameterTypes.length; i++){
				System.err.println(parameterTypes[i].getSimpleName());
				Object o = XMLToObject.createObjectFromNode(paramList.get(i));
				if( ! parameterTypes[i].isInstance(o) ){
					trouve = false;
					break;
				} else {
					args.add(o);
				}
			}
			if(trouve = false){
				args.clear();
				continue;
			} else{
				System.err.println("bla");
				calledMethod = m;
				break;
			}
			
		}
		
		System.err.println(calledMethod.getName());
		System.err.println(args.size());
		Object ret = calledMethod.invoke(this, args.toArray());
		
		

	}
	

	@Override
	public void display(Stringable s) {
		System.out.println("Un point 2D : " + s.toString()) ;
	}
	
	// Construction de l'objet depuis un XML
}
