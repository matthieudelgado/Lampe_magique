package objets;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javassist.ClassPool;
import javassist.CtField;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tools.ObjectToXML;
import xmlrmi.XMLRMIField;
import client.Client;
/**
 * 
 * @author marcgregoire
 * @author matthieudelgado
 */
public class Point implements XMLRMISerializable, Stringable {

	// attention construire une partie de l'XML en local pour les champs dans la metthode de contstruction XML


	@XMLRMIField(serializationName = "x", serializationType = "double")
	protected double a;

	@XMLRMIField(serializationName = "y", serializationType = "double")
	protected double b;

	@XMLRMIField(serializationName = "mark", serializationType = "string")
	protected String marque="nom de la marque";

	private String oid = "testC";
	

	private static Integer compteur = 0;

	public Point(double x, double y){
		this.a=x;
		this.b=y;
	}

	@Override
	public String toString(){
		return "x = "+this.a+ " y =  " + this.b+" mark = "+this.marque;
	}

	public String getOid(){
		return this.oid;
	}


	// recuperer de l'interface la classe pour avoir les m�thode a passer dans l'XML
	@Override
	public Element toXML(Class<?> inter,Document doc) {
		//initOid();
		Client.repertoire.put(this.oid, this);
		
		String interString = inter.getName();
		String tostring = "public String toString(){return \"x = \"+this.x+ \" y =  \" + this.y+ \" marque = \"+this.mark;}";
		ArrayList<String> aString= new ArrayList<String>();
		aString.add(tostring);
		return ObjectToXML.appelClientToDocument(this.getOid(),this, aString,doc);
	}

	@Override
	public void updateFromXML(Element theXML) {
		ObjectToXML.updateObjectFromElement(theXML, this);
	}

	@Override
	/**
	 * Initialise l'oid de l'objet
	 */
	public void initOid() {
		if(this.oid == "") return;
		synchronized(compteur){
			this.oid =""+this.getClass().getName()+"_"+compteur;
			compteur++;
		}
	}

	
}
