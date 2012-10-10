package objets;


import org.w3c.dom.Element;

import xmlrmi.*;

public class Point implements XMLRMISerializable {

	// attention construire une partie de l'XML en local pour les champs dans la metthode de contstruction XML
	
	
	@XMLRMIField(serializationName = "x", serializationType = "double")
	protected double a;
	
	@XMLRMIField(serializationName = "y", serializationType = "double")
	protected double b;

	@XMLRMIField(serializationName = "mark", serializationType = "string")
	protected char marque='m';
	
	private String oid=this.getOid();
	
	public Point(int x, int y){
		this.a=x;
		this.b=y;
	}
	
	public String toString(){
		return "x = "+this.a+ " y =  " + this.b;
	}
	
	public String getOid(){
		return "oidTest 007";
	}
	public void setOid(String oid){
		this.oid=oid;
	}

	// recuperer de l'interface la classe pour avoir les méthode a passer dans l'XML
	@Override
	public String toXML(Class<?> inter) {
		// TODO Auto-generated method stub
		
		
		String s = "public String toString(){return \"x = \"+this.a+ \" y =  \" + this.b;}";
		
		return null;
	}

	@Override
	public void updateFromXML(Element theXML) {
		// TODO Auto-generated method stub
		
	}
}
