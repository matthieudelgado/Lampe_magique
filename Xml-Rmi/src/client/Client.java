package client;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import objets.XMLRMISerializable;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tools.ObjectToXML;
import tools.TestEcritureXML;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

public class Client {
	public static HashMap<String,XMLRMISerializable> repertoire = new HashMap<String,XMLRMISerializable>();

	public static void main(String[] args) {
		Socket socket = null;
		try {
			//socket = new Socket("localhost", 5555);

			// Matthieu
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
			Document doc = TestEcritureXML.lireDocument(docBuilder, "data/appelClient.xml");
			//Fin Matthieu
			
			/* Marc */
			//Document doc  =ObjectToXML.stringToDoc(ObjectToXML.fileToString("data/appelClient.xml")); 
			
			//Fin Marc
			
			socket = new Socket("localhost", 5555);
			XMLOutputStream out = new XMLOutputStream(socket.getOutputStream());
			StreamResult sr = new StreamResult(out);
			DOMSource ds = new DOMSource(doc);
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.transform(ds, sr);

			out.send();

			XMLInputStream in = new XMLInputStream(socket.getInputStream());
			in.recive();
			
			doc = docBuilder.parse(in);
			/*NodeList nl = doc.getElementsByTagName("object"), nl2, nl3;
			Node n, n2, n3, n4;
			String newOid;
			NamedNodeMap nnm, nnm2;
			XMLRMISerializable objet;
			for(int i = 0; i< nl.getLength(); i++){
				n = nl.item(i);
				nnm = n.getAttributes();
				n2 = nnm.getNamedItem("oid");
				newOid = n2.getNodeValue();
				System.out.println(newOid);
				objet = Client.repertoire.get("newOid");
				System.out.println(objet == null);
				nl2 = n.getChildNodes();
				for(int j = 0; j< nl2.getLength(); j++){
					n3 = nl2.item(j);
					if(n3.getNodeName().equals("fields")){
						nl3 = n3.getChildNodes();
						for(int k = 0; k< nl3.getLength(); k++){
							n4 = nl3.item(k);
							//ici on a le field
							//il faut trouver son nom
							//le retrouver dans objet
							//le modifier
						}
					} 
				}

			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}/* finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/

	}

}
