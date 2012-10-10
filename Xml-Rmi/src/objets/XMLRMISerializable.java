package objets;

public interface XMLRMISerializable {
	
	public String toXML(Class<?> inter) ; 
	public void updateFromXML(org.w3c.dom.Element theXML) ;
	public void initOid();
}
