package tests;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import objets.Point;
import objets.PointContainer;
import objets.PointContainerType;
import objets.ReversibleXY;
import objets.ReversibleXYContainer;
import objets.ReversibleXYContainerImpl;
import objets.Stringable;
import objets.StringableContainer;
import objets.StringableContainerType;
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
	@org.junit.Test 
	public void retBooleanPDate(){

		ArrayList<Object> params = new ArrayList<Object>();

		Date d1 = new Date();
		Date d2 = new Date();
		d2.setTime(0);
		params.add(d1);
		params.add(d2);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"isD1BeforeD2", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("boolean");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), boolean.class);
			assertTrue(Boolean.parseBoolean(o.toString()) == false);


		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
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
	//test avec array double
	@org.junit.Test 
	public void retArrayDoublePArrayDouble(){

		ArrayList<Object> params = new ArrayList<Object>();
		Double[] list = new Double[2];
		list[0] = 1.0; list[1] = 2.0;
		params.add(list);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"inverse", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("array");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), double[].class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), double[].class);//le parametre
			assertTrue(((double[])o)[0] == 2.0);
			assertTrue(((double[])o)[1] == 1.0);
			assertTrue(((double[])o1)[0] == 1.0);
			assertTrue(((double[])o1)[1] == 2.0);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec array String
	@org.junit.Test 
	public void retArrayStringPArrayString(){

		ArrayList<Object> params = new ArrayList<Object>();
		String[] list = new String[2];
		list[0] = "1"; list[1] = "2";
		params.add(list);
		//Creation du document xml a envoyer
		Document doc = ObjectToXML.createAppelClient(null,"inverse", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("array");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), String[].class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), String[].class);//le parametre
			assertTrue(((String[])o)[0].equals("2"));
			assertTrue(((String[])o)[1].equals("1"));
			assertTrue(((String[])o1)[0].equals("1"));
			assertTrue(((String[])o1)[1].equals("2"));

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

			//on recupère les objets des tableaux
			Stringable obj1 = ((Stringable[])o)[0];
			Stringable obj2 = ((Stringable[])o)[1];
			Stringable obj3 = ((Stringable[])o1)[0];
			Stringable obj4 = ((Stringable[])o1)[1];

			assertTrue(obj1.getClass().getDeclaredField("x").getDouble(obj1) == 5); 
			assertTrue(obj1.getClass().getDeclaredField("y").getDouble(obj1) == 0); 
			assertTrue(obj2.getClass().getDeclaredField("x").getDouble(obj2) == 0); 
			assertTrue(obj2.getClass().getDeclaredField("y").getDouble(obj2) == 5); 

			assertTrue(obj3.getClass().getDeclaredField("x").getDouble(obj3) == 0); 
			assertTrue(obj3.getClass().getDeclaredField("y").getDouble(obj3) == 5); 
			assertTrue(obj4.getClass().getDeclaredField("x").getDouble(obj4) == 5); 
			assertTrue(obj4.getClass().getDeclaredField("y").getDouble(obj4) == 0); 

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec struct

	//test avec object avec champs primitifs sans modifier les champs
	@org.junit.Test 
	public void retObjPObjChPrimSsModif(){

		ArrayList<Object> params = new ArrayList<Object>();
		XMLRMISerializable param = new Point(5, 0);
		params.add(param);
		//Creation du document xml a envoyer
		ArrayList<Class<?>> itfs = new ArrayList<Class<?>>();
		itfs.add(Stringable.class);
		Document doc = ObjectToXML.createAppelClient(itfs,"display", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("object");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), Stringable.class);//la valeur de retour

			//on recupère les objets des tableaux
			Stringable obj1 = (Stringable)o;

			assertTrue(obj1.getClass().getDeclaredField("x").getDouble(obj1) == 5); 
			assertTrue(obj1.getClass().getDeclaredField("y").getDouble(obj1) == 0); 


		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

	//test avec object avec champs primitifs en modifiant les champs
	@org.junit.Test 
	public void retObjPObjChPrimAvModif(){

		ArrayList<Object> params = new ArrayList<Object>();
		XMLRMISerializable param = new Point(5, 0);
		params.add(param);
		//Creation du document xml a envoyer
		ArrayList<Class<?>> itfs = new ArrayList<Class<?>>();
		itfs.add(ReversibleXY.class);
		Document doc = ObjectToXML.createAppelClient(itfs,"reverseXY", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("object");
			Object o = XMLToObject.createObjectFromNode(nl.item(0), ReversibleXY.class);//la valeur de retour
			Object o1 = XMLToObject.createObjectFromNode(nl.item(1), ReversibleXY.class);//le parametre

			//on recupère les objets des tableaux
			ReversibleXY obj1 = (ReversibleXY)o;
			ReversibleXY obj2 = (ReversibleXY)o1;

			assertTrue(obj1.getClass().getDeclaredField("x").getDouble(obj1) == 0); 
			assertTrue(obj1.getClass().getDeclaredField("y").getDouble(obj1) == 5); 
			assertTrue(obj2.getClass().getDeclaredField("x").getDouble(obj2) == 0); 
			assertTrue(obj2.getClass().getDeclaredField("y").getDouble(obj2) == 5); 


		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec object avec champs objet sans modifier les champs
	@org.junit.Test 
	public void retObjPObjChObjSsModif(){

		ArrayList<Object> params = new ArrayList<Object>();
		XMLRMISerializable param = new PointContainer(5, 0);
		params.add(param);
		//Creation du document xml a envoyer
		ArrayList<Class<?>> itfs = new ArrayList<Class<?>>();
		itfs.add(StringableContainer.class);
		Document doc = ObjectToXML.createAppelClient(itfs,"displayField", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("object");
			Object o = XMLToObject.createObjectFromNode(nl.item(1), StringableContainer.class);//la valeur de retour

			//on recupère les objets des tableaux
			StringableContainer obj1 = (StringableContainer)o;

			assertTrue(obj1.getClass().getDeclaredField("x").getDouble(obj1) == 5); 
			assertTrue(obj1.getClass().getDeclaredField("y").getDouble(obj1) == 0); 


		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	//test avec object avec champs objet en modifiant les champs
	@org.junit.Test 
	public void retObjPObjChObjAvcModif(){

		ArrayList<Object> params = new ArrayList<Object>();
		XMLRMISerializable param = new ReversibleXYContainerImpl(5, 0);
		params.add(param);
		//Creation du document xml a envoyer
		ArrayList<Class<?>> itfs = new ArrayList<Class<?>>();
		itfs.add(ReversibleXYContainer.class);
		Document doc = ObjectToXML.createAppelClient(itfs,"reverseXYField", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("object");
			Object o = XMLToObject.createObjectFromNode(nl.item(1), ReversibleXYContainer.class);//la valeur de retour

			//on recupère les objets des tableaux
			ReversibleXYContainer obj1 = (ReversibleXYContainer)o;

			assertTrue(obj1.getClass().getDeclaredField("x").getDouble(obj1) == 0); 
			assertTrue(obj1.getClass().getDeclaredField("y").getDouble(obj1) == 5); 


		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	//test avec object avec champs ArrayList<Object> sans update des champs
	@org.junit.Test 
	public void retObjPObjChListObjSsModif(){

		ArrayList<Object> params = new ArrayList<Object>();
		Point[] lpoint = new Point[1];
		
		Point p1 = new Point(1,1);
		lpoint[0]=p1;
		XMLRMISerializable param = new PointContainerType(lpoint);
		params.add(param);
		//Creation du document xml a envoyer
		ArrayList<Class<?>> itfs = new ArrayList<Class<?>>();
		itfs.add(StringableContainerType.class);
		Document doc = ObjectToXML.createAppelClient(itfs,"displayField", params);

		try{
			Document doc2 = sendAndReceive(doc, out, socket);

			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);

			//test du xml retourné
			NodeList nl = doc2.getElementsByTagName("object");
			Object o = XMLToObject.createObjectFromNode(nl.item(1), StringableContainerType.class);//la valeur de retour

			//on recupère les objets des tableaux
			StringableContainerType obj1 = (StringableContainerType)o;

			//assertTrue(obj1.getClass().getDeclaredField("x").getDouble(obj1) == 5); 
			//assertTrue(obj1.getClass().getDeclaredField("y").getDouble(obj1) == 0); 
			assertTrue(true);

		}catch(Exception e){
			e.printStackTrace();
			assertTrue(false);
		}
	}

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
	//test mauvais nom de methode, le serveur ne doit pas planter

}
