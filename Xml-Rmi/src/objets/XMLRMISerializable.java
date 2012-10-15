package objets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XMLRMISerializable {
	
	public Element toXML(Class<?> inter,Document doc) ; 
	public void updateFromXML(org.w3c.dom.Element theXML) ;
	public void initOid();
}
