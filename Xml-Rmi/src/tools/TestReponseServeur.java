package tools;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import objets.Point;

public class TestReponseServeur {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Point p1 = new Point(2,3);
		System.out.println(p1.toString());
	//	p1.setOid("http://www.jm.fr/1000000");		
		
//		ArrayList<Object> listObj = new ArrayList<Object>();
//		listObj.add(p1);
//		listObj.add(p2);
		Document doc  =ObjectToXML.stringToDoc(ObjectToXML.fileToString("data/reponseServeur.xml")); 

		Element e= (Element) doc.getElementsByTagName("fields").item(0); 
		p1.updateFromXML(e);
		
		System.out.println(p1.toString());
		
	}

}
