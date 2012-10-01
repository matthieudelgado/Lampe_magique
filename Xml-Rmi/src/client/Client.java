package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import tools.TestEcritureXML;
import xmlrmi.XMLOutputStream;

public class Client {

	public static void main(String[] args) {
		Socket socket = null;
		try {
			socket = new Socket("localhost", 5555);

			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
			Document doc = TestEcritureXML.lireDocument(docBuilder, "data/test.xml");

			XMLOutputStream out = new XMLOutputStream(socket.getOutputStream());
			StreamResult sr = new StreamResult(out);
			DOMSource ds = new DOMSource(doc);
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.transform(ds, sr);
			
			out.send();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
