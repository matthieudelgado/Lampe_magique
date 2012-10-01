package objets;
import xmlrmi.*;

public class Point {
	@XMLRMIField{serializationName="x",serializationType="double"}
	protected double a ;
	@XMLRMIField{serializationName="y",serializationType="double"}
	protected double b ;
	@XMLRMIField{serializationName="mark",serializationType="string"}
	protected char marque ;
}
