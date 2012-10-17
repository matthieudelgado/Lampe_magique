package objets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette interface definie les methodes d'un objet serialisable du point de 
 * vue de l'application.
 *
 */
public interface XMLRMISerializable {
	
	public Element toXML(Class<?> inter,Document doc) ; 
	public void updateFromXML(org.w3c.dom.Element theXML) ;
	public void initOid();
}
