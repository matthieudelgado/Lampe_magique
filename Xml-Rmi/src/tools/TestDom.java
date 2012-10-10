package tools;


import java.util.ArrayList;

import javax.xml.parsers.*;



import objets.Point;
import objets.Stringable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDom {
	public static void main(String[] args) {
		
	
		Point p = new Point(1,2);
		ObjectToXML objXml = new ObjectToXML();
		ArrayList<String> lMeth = new ArrayList<String>();
		
		lMeth.add(p.toXML(Stringable.class));
		
		Document docAppelClient = objXml.appelClientToDocument(p,"display", lMeth);
		
		objXml.docToFile(docAppelClient, "data/appelClient.xml");
		
		
	}
}
