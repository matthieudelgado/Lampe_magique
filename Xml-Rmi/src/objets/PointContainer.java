package objets;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import client.Client;

import tools.ObjectToXML;
import xmlrmi.XMLRMIField;

/**
 * 
 * @author marcgregoire
 * @author matthieudelagado
 */
public class PointContainer implements XMLRMISerializable, StringableContainer{
	
	@XMLRMIField(serializationName = "point", serializationType = "objets.Stringable")
	Point p; 


	private String oid = "testC";

	private static Integer compteur = 0;

	
	public PointContainer(double a, double b){
		p = new Point(a,b);
	}
	
	public String getOid(){
		return this.oid;
	}

	@Override
	public Element toXML(Class<?> inter, Document doc) {
		initOid();
		Client.repertoire.put(this.oid, this);
		ArrayList<String> aString= new ArrayList<String>();
		System.err.println("toXML interface : "+inter.getSimpleName());
		if(inter.equals(StringableContainer.class))
		{
			String tostring = "public String toString(){return point.toString();}";
			aString.add(tostring);
		}
		return ObjectToXML.objectToElement(this.getOid(), inter,null, this, aString,doc);
	}

	@Override
	public void updateFromXML(Element theXML) {
		ObjectToXML.updateObjectFromElement(theXML, this);
	}

	@Override
	public void initOid() {
		if(this.oid == "") return;
		synchronized(compteur){
			this.oid =""+this.getClass().getSimpleName()+"_"+compteur;
			compteur++;
		}
	}
	
	@Override
	public String toString(){
		return p.toString();
	}



}
