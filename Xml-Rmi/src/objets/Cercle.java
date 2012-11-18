package objets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xmlrmi.XMLRMIField;

/**
 * 
 * @author marcgregoire
 * @author matthieudelagado
 */
public class Cercle implements XMLRMISerializable, Stringable{

	@XMLRMIField(serializationName="c", serializationType="Point")
	protected Point centre;
	@XMLRMIField(serializationName="r", serializationType="double")
	protected double rayon;
	
	
	public Cercle(Point centre, double rayon){
		this.centre= centre;
		this.rayon=rayon;
	}
	
	public String toString(){
		return "Cercle de centre = "+centre.toString()+" et de rayon = "+rayon;
	}
	
	@Override
	public Element toXML(Class<?> inter, Document doc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateFromXML(Element theXML) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initOid() {
		// TODO Auto-generated method stub
		
	}

}
