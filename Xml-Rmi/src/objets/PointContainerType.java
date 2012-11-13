package objets;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import client.Client;

import tools.ObjectToXML;
import xmlrmi.XMLRMIField;

public class PointContainerType implements XMLRMISerializable, StringableContainerType{

	@XMLRMIField(serializationName="lpoint",serializationType="array")
	Stringable[] listePoint ;

	private String oid = "PointContainerType";

	private static Integer compteur = 0;

	public PointContainerType(Stringable[] listePoint){
		this.listePoint=listePoint;
	}
	
	public String getOid(){
		return oid;
	}

	@Override
	public Element toXML(Class<?> inter, Document doc) {
		initOid();
		Client.repertoire.put(this.oid, this);
		ArrayList<String> aString= new ArrayList<String>();
		if(inter.equals(StringableContainerType.class))
		{
			String tostring = "public String toString(){return listePoint.toString();}";
			aString.add(tostring);
		}
		return ObjectToXML.appelClientToDocument(this.getOid(), inter, this, aString,doc);
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
		String s ="[ ";
		for(int i = 0; i<this.listePoint.length;i++){
			s=s+this.listePoint[i].toString()+" , ";
		}
		return s+" ]";
	}
	
	

}
