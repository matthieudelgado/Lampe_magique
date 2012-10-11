package objets;


import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tools.ObjectToXML;
import xmlrmi.XMLRMIField;
import client.Client;

public class Point implements XMLRMISerializable, Stringable {

	// attention construire une partie de l'XML en local pour les champs dans la metthode de contstruction XML


	@XMLRMIField(serializationName = "x", serializationType = "double")
	protected double a;

	@XMLRMIField(serializationName = "y", serializationType = "double")
	protected double b;

	@XMLRMIField(serializationName = "mark", serializationType = "string")
	protected char marque='m';

	private String oid = "a";
	
	private String methode_appelante;

	private static Integer compteur = 0;

	public Point(int x, int y){
		this.a=x;
		this.b=y;
	}

	@Override
	public String toString(){
		return "x = "+this.a+ " y =  " + this.b;
	}

	public String getOid(){
		return this.oid;
	}


	// recuperer de l'interface la classe pour avoir les m�thode a passer dans l'XML
	@Override
	/**
	 * 
	 */
	public String toXML(Class<?> inter) {
		initOid();
		Client.repertoire.put(this.oid, this);
		
		String interString = inter.getName();
		String tostring = "public String toString(){return \"x = \"+this.a+ \" y =  \" + this.b;}";
		ArrayList<String> aString= new ArrayList<String>();
		aString.add(tostring);
		ObjectToXML.docToFile((ObjectToXML.appelClientToDocument(this.getOid(),this, this.getMethode_appelante(), aString)),"data/"+this.getOid()+".xml");
		
		return null;
	}

	@Override
	public void updateFromXML(Element theXML) {
		//on recoit un element du xml (a priori a partir de la balise "value")
		// TODO creer une methode updateFromElement(Element theXML,Object this) dans ObjectToXML
		
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

	public String getMethode_appelante() {
		return methode_appelante;
	}

	public void setMethode_appelante(String methode_appelante) {
		this.methode_appelante = methode_appelante;
	}
}
