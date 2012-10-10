package tools;


import javax.xml.parsers.*;



import objets.Point;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDom {
	public static void main(String[] args) {
		
	
		Point p = new Point(1,2);
		ObjectToXML objXml = new ObjectToXML();
		//Document doc = objXml.creerDocument();
		
		Document docAppelClient = objXml.appelClientToDocument(p,"display", null);
		//objXml.afficherDocument(docAppelClient);
		objXml.docToFile(docAppelClient, "data/appelClient.xml");
		
		//Document doc1=objXml.fileToDoc(objXml.getDocBuilder(), "data/copie2.xml");
		//objXml.objectToXML(p, doc1);
		//objXml.docToFile(doc1, "data/copie2.xml");
		
		//objXml.docToFile(doc,"data/premier.xml");
		
		//objXml.docToFile(objXml.fileToDoc(docBuilder, "test.xml"),"data/copie.xml");
		
		//objXml.copyPasteXml("data/test.xml", "data/copie2.xml");
		//System.out.println(objXml.getDocXml().getElementsByTagName("methodCall").item(0));
	}
}
