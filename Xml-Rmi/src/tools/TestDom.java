package tools;


import java.util.ArrayList;

import javax.xml.parsers.*;



import objets.Point;
import objets.Stringable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDom {
	public static void main(String[] args) {
			
		String methodeAppelante = "methode1";
		Document doc = ObjectToXML.appelClient(methodeAppelante);
		
		//** Parametre de la methode Appelante
		int i=5;
		double d=4.5;
		//**
		
		Element p1=ObjectToXML.getNodePrimitif(i,doc); //on cree un Element contenant un parametre primitif
		ObjectToXML.mergeDocs(doc, p1);				  // on l'ajoute au doc qui va contenir l'ensemble de l'appelClient	
		
		Element p2 = ObjectToXML.getNodePrimitif(d, doc);
		ObjectToXML.mergeDocs(doc, p2);
		
		Element paramObject = doc.createElement("param");
		Point p = new Point(1,2);
		Element obje=p.toXML(Stringable.class,doc);
		paramObject.appendChild(obje);
		
		ObjectToXML.mergeDocs(doc, paramObject);
		
		//ObjectToXML.afficherDocument(doc);
		ObjectToXML.docToFile(doc, "data/"+p.getOid()+".xml");
	
	}
}
