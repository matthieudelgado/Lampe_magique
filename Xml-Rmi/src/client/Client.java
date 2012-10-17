package client;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import objets.Point;
import objets.XMLRMISerializable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tools.ObjectToXML;
import tools.TestEcritureXML;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

/**
 * 
 * @author marcgregoire
 * @author matthieudelgado
 * Cette methode contruit puis envoi une requete au serveur attend la reponse et enfin traite la 
 * reponse en modifiant l'etat des objets qu'il a envoyé.
 */
public class Client {

	public static HashMap<String,XMLRMISerializable> repertoire = new HashMap<String,XMLRMISerializable>();

	public static void main(String[] args) {
		Socket socket = null;
		try {
			//Creation de la liste des parametres de la methode
			ArrayList<Object> params = new ArrayList<Object>();
			Point p = new Point(1.0,2.0);
			params.add(p);
			//Creation du document xml a envoyer
			Document doc = ObjectToXML.createAppelClient("display", params);

			//envoi du xml
			socket = new Socket("localhost", 5555);
			XMLOutputStream out = new XMLOutputStream(socket.getOutputStream());
			StreamResult sr = new StreamResult(out);
			DOMSource ds = new DOMSource(doc);
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.transform(ds, sr);
			out.send();

			//reception de la reponse du serveur
			XMLInputStream in = new XMLInputStream(socket.getInputStream());
			in.recive();
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
			Document doc2 = docBuilder.parse(in);
			//On affiche le document
			TestEcritureXML.afficherDocument(doc2);
			
			
			//****Mise a jour des objets envoyes par le serveur*****
			
			//recupere tout les oid
			ArrayList<String> listeOID=ObjectToXML.getOidFromXML(doc2);
			//pour chaque oid, je recupere l'objet correpondant et j'update ses fields
			for(int i =0;i<listeOID.size();i++){
				XMLRMISerializable o = Client.repertoire.get(listeOID.get(i));
				Element e = ObjectToXML.getFieldsByOID(doc2, listeOID.get(i));
				o.updateFromXML(e);
			}
			
			System.err.println("p update : "+p.toString());


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
