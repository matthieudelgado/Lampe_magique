package tools;

import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.Document;
/**
 * 
 * @author marcgregoire
 * @author matthieudelagado
 *
 *Classe de test de XMLToObject
 */
public class TestXMLToObject {

	
	
	@Test
	/**
	 * Test qui cree un objet a partir d'un xml
	 */
	public void testCreateObjectFromNodeObject(){
		String appelClient ="<value >"+
								"<object oid=\"Point_0\" type=\"objets.Stringable\" >"+
									"<fields >"+
										"<field name=\"x\" >"+
											"<value >"+
												"<double >1.0</double>"+
											"</value>"+
										"</field>"+
										"<field name=\"y\" >"+
											"<value >"+
												"<double >3.0</double>"+
											"</value>"+
										"</field>"+
										"<field name=\"mark\" >"+
											"<value >"+
												"<string >nom de la marque</string>"+
											"</value>"+
										"</field>"+
									"</fields>"+
									"<methods >"+
										"<method language=\"Java\" >"+
											"public String toString(){return \"x = \"+this.x+ \" y =  \" + this.y+ \" marque = \"+this.mark;}</method>"+
										"</methods>"+
								"</object>"+
							"</value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), objets.Stringable.class);
			assertTrue(o.getClass().getSimpleName().equals("Point_0"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Test qui cree un entier a partir d'un xml
	 */
	public void testCreateObjectFromNodeInt(){
		String appelClient ="<value ><int >1</int></value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object  o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), Integer.class);
			assertTrue(o.getClass().getSimpleName().equals("Integer"));
			assertTrue(o.toString().equals("1"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Test qui cree un double a partir d'un xml
	 */
	public void testCreateObjectFromNodeDouble(){
		String appelClient ="<value ><double >1.0</double></value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object  o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), Integer.class);
			assertTrue(o.getClass().getSimpleName().equals("Double"));
			assertTrue(o.toString().equals("1.0"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Test qui cree un boolean a partir d'un xml
	 */
	public void testCreateObjectFromNodeBoolean(){
		String appelClient ="<value ><boolean >1</boolean></value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object  o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), Integer.class);
			assertTrue(o.getClass().getSimpleName().equals("Boolean"));
			assertTrue(o.toString().equals("true"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Test qui cree un String a partir  d'un xml
	 */
	public void testCreateObjectFromNodeString(){
		String appelClient ="<value ><string >Hello</string></value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object  o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), Integer.class);
			assertTrue(o.getClass().getSimpleName().equals("String"));
			assertTrue(o.toString().equals("Hello"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Test qui cree un DateTime a partir d'un xml
	 */
	public void testCreateObjectFromNodeDateTime(){
		String appelClient ="<value ><dateTime.iso8601 >1970-01-01T01:00:00</dateTime.iso8601></value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object  o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), Integer.class);
			assertTrue(o.getClass().getSimpleName().equals("Date"));
			assertTrue(o.toString().equals("Thu Jan 01 01:00:00 CET 1970"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	@Test
	/**
	 * Test qui cree une liste d'entier a partir d'un xml
	 */
	public void testCreateObjectFromNodeArray(){
		String appelClient ="<value >"+
								"<array >" +
									"<value >"+
										"<int >1</int>" +
									"</value>"+
									"<value >"+
										"<int >2</int>" +
									"</value>"+
								"</array>"+
							"</value>";
		Document d = ObjectToXML.stringToDoc(appelClient);
		try {
			Object  o = XMLToObject.createObjectFromNode(d.getFirstChild().getFirstChild(), Integer.class);
			assertTrue(o.getClass().getSimpleName().equals("int[]"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
		
	
	
}
