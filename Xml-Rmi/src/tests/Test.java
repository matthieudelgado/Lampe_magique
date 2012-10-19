package tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import objets.Movable;
import objets.Point;
import objets.Stringable;
import objets.XMLRMISerializable;

import org.junit.After;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import tools.ObjectToXML;
import tools.TestEcritureXML;
import tools.XMLToObject;
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
	private static String serverAdresse = "localhost";
	private static int serverPort = 5555;
	
	public static Document sendAndReceive(Document doc, XMLOutputStream out, Socket socket) throws Exception{
		StreamResult sr = new StreamResult(out);
		DOMSource ds = new DOMSource(doc);
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.transform(ds, sr);
		out.send();

		//reception de la reponse du serveur
		XMLInputStream in = new XMLInputStream(socket.getInputStream());
		in.receive();
		DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
		Document doc2 = docBuilder.parse(in);
		return doc2;
	}

	@Before
	/**
	 * Cette methode initialise la socket pour les tests a suivre
	 */
	public void beforeTest(){
		try {
			socket = new Socket(serverAdresse, serverPort);
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
	//Un seul parametre

	//avec retour
	//test avec un int

	@org.junit.Test 
	public void retIntPInt(){
		ArrayList<Object> params = new ArrayList<Object>();
		Integer inte = 4;
		params.add(inte);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"increment", params);
		try{
			
			Document doc2 = sendAndReceive(doc, out, socket);

			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("int");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), int.class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), int.class);//le parametre

			assertTrue(Integer.parseInt(o.toString()) == 5);
			assertTrue(Integer.parseInt(o1.toString()) == 4);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec un double
	@org.junit.Test 
	public void retDouclePDouble(){
		ArrayList<Object> params = new ArrayList<Object>();
		Double inte = 4.0;
		params.add(inte);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"increment", params);
		try{

			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("double");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), double.class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), double.class);//le parametre

			assertTrue(Double.parseDouble(o.toString()) == 5.0);
			assertTrue(Double.parseDouble(o1.toString()) == 4.0);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec boolean
	@org.junit.Test 
	public void retBoolPBool(){

		ArrayList<Object> params = new ArrayList<Object>();
		Boolean b = false;
		params.add(b);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"opposite", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("boolean");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), boolean.class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), boolean.class);//le parametre
			assertTrue(Boolean.parseBoolean(o.toString()) == true);
			assertTrue(Boolean.parseBoolean(o1.toString()) == false);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec string
	@org.junit.Test 
	public void retStringPString(){

		ArrayList<Object> params = new ArrayList<Object>();
		String s = "Hello";
		params.add(s);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"concatWorld", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("string");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), String.class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), String.class);//le parametre
			assertTrue(o.toString().equals("HelloWorld"));
			assertTrue(o1.toString().equals("Hello"));

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec datetime
	
	//test avec base64	
	//test avec array de int
	@org.junit.Test 
	public void retArrayIntPArrayInt(){

		ArrayList<Object> params = new ArrayList<Object>();
		Integer[] list = new Integer[2];
		list[0] = 1; list[1] = 2;
		params.add(list);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"inverse", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("array");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), int[].class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), int[].class);//le parametre
			assertTrue(((int[])o)[0] == 2);
			assertTrue(((int[])o)[1] == 1);
			assertTrue(((int[])o1)[0] == 1);
			assertTrue(((int[])o1)[1] == 2);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec array d'objet
	@org.junit.Test 
	public void retArrayObjPArrayObj(){

		ArrayList<Object> params = new ArrayList<Object>();
		XMLRMISerializable[] list = new XMLRMISerializable[2];
		list[0] = new Point(0, 5); list[1] = new Point(5, 0);
		params.add(list);
		//Creation du document xml a envoyer
		ArrayList<Class<?>> itfs = new ArrayList<Class<?>>();
		itfs.add(Stringable.class);
		itfs.add(Stringable.class);
		Document doc = ObjectToXML.createAppelClient(itfs,"inverse", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("array");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), Stringable[].class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), Stringable[].class);//le parametre
			assertTrue(((int[])o)[0] == 2);
			assertTrue(((int[])o)[1] == 1);
			assertTrue(((int[])o1)[0] == 1);
			assertTrue(((int[])o1)[1] == 2);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec struct
	//test avec object avec champs primitifs sans modifier les champs
	//test avec object avec champs primitifs en modifiant les champs
	//test avec object avec champs objet en modifiant les champs
	//test avec object avec champs objet en modifiant les champs

	//sans retour
	//test avec un int
	//test avec un objet

	//plusieurs parametre
	//avec retour
	//int et double
	//bool et array
	//struct et objet
	//objet et objet	

	//test malformation


}
