package objets;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tools.ObjectToXML;
import xmlrmi.XMLRMIField;
import client.Client;

public class ReversibleXYContainerImpl implements XMLRMISerializable, ReversibleXYContainer {
	@XMLRMIField(serializationName = "point", serializationType = "objets.ReversibleXY")
	Point p; 


	private String oid = "testC";

	private static Integer compteur = 0;

	
	public ReversibleXYContainerImpl(double a, double b){
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
		if(inter.equals(ReversibleXYContainer.class))
		{
			String tostring = "	public void reverse() {point.reverse();}";
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
	public void reverse() {
		p.reverse();

	}

}
