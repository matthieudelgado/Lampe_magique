package tools;

import java.util.ArrayList;

import org.w3c.dom.Document;

import objets.Point;

public class TestReponseServeur {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Point p1 = new Point(2,3);
	//	p1.setOid("http://www.jm.fr/1000000");
		
		Point p2 = new Point(4,4);
		
		
		ArrayList<Object> listObj = new ArrayList<Object>();
		listObj.add(p1);
		listObj.add(p2);
		
		ObjectToXML otx = new ObjectToXML();
		XMLToObject xto = new XMLToObject();
		
		Document doc = otx.fileToDoc("data/reponseServeur.xml");
		xto.updateFromXml(doc, listObj);
		
	}

}
