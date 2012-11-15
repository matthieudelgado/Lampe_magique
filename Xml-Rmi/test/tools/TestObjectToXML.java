package tools;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;

import objets.Point;
import objets.Stringable;
import objets.XMLRMISerializable;

import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestObjectToXML {

	private Document doc;

	@Before
	public void beforeTest(){
		doc = ObjectToXML.creerDocument();
	}

	@org.junit.Test
	/**
	 * Test du merge d'un element avec un  document.
	 */
	public void testMergeDoc()
	{
		Element params = doc.createElement("params");
		doc.appendChild(params);
		Element elem = doc.createElement("test");
		ObjectToXML.mergeDocs(doc, elem);
		assertTrue(doc.getElementsByTagName("params").item(0).getFirstChild().getNodeName().equals("test"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifInt()
	{
		int a = 3;
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("int"));
		assertTrue(e.getFirstChild().getTextContent().equals("3"));

	}

	@org.junit.Test
	public void testGetContenuNodePrimitifDouble()
	{
		double a = 3.0;
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("double"));
		assertTrue(e.getFirstChild().getTextContent().equals("3.0"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifBoolean()
	{
		boolean a = true;
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("boolean"));
		assertTrue(e.getFirstChild().getTextContent().equals("1"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifString()
	{
		String a = "Hello";
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("string"));
		assertTrue(e.getFirstChild().getTextContent().equals("Hello"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifDate()
	{
		Date a = new Date();
		a.setTime(0);
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("datetime"));
		assertTrue(e.getFirstChild().getTextContent().equals("1970-01-01T01:00:00"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifArrayInt()
	{
		Integer[] la = new Integer[1];
		int a = 3;
		la[0] = a;
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("array"));
		assertTrue(e.getFirstChild().getFirstChild().getNodeName().equals("value"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("int"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getTextContent().equals("3"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifArrayDouble()
	{
		Double[] la = new Double[1];
		double a = 3.0;
		la[0] = a;
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("array"));
		assertTrue(e.getFirstChild().getFirstChild().getNodeName().equals("value"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("double"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getTextContent().equals("3.0"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifArrayBoolean()
	{
		Boolean[] la = new Boolean[1];
		boolean a = true;
		la[0] = a;
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("array"));
		assertTrue(e.getFirstChild().getFirstChild().getNodeName().equals("value"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("boolean"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getTextContent().equals("1"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifArrayString()
	{
		String[] la = new String[1];
		String a = "Hello";
		la[0] = a;
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("array"));
		assertTrue(e.getFirstChild().getFirstChild().getNodeName().equals("value"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("string"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getTextContent().equals("Hello"));
	}

	@org.junit.Test
	public void testGetContenuNodePrimitifArrayDate()
	{
		Date[] la = new Date[1];
		Date a = new Date();
		a.setTime(0);
		la[0] = a;
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("array"));
		assertTrue(e.getFirstChild().getFirstChild().getNodeName().equals("value"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("datetime"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getTextContent().equals("1970-01-01T01:00:00"));
	}

	//TODO
//	@org.junit.Test
//	public void testGetContenuNodePrimitifArrayXMLRMI()
//	{
//		XMLRMISerializable[] la = new XMLRMISerializable[1] ;
//		la[0] = new Point(1,1);
//		ArrayList<Class<?>> liter =new ArrayList<Class <?>>();
//		liter.add(XMLRMISerializable.class);
//		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, liter, null);
//		assertTrue(e.getFirstChild().getNodeName().equals("object"));
//	}
	
	//test  dateToDateTime
	@org.junit.Test
	public void testDateToDateTime()
	{
		Date d = new Date();
		d.setTime(0);
		String date = ObjectToXML.dateToDateTime(d);
		assertTrue(date.equals("1970-01-01T01:00:00"));
	}

	@org.junit.Test
	public void testAppelClient(){
		Document doc = ObjectToXML.appelClient("test");
		String retour= ObjectToXML.docToString(doc);
		String expected ="<methodCall >" +
							"<methodeName >test</methodeName>" +
							"<params ></params>" +
						"</methodCall>";
		assertTrue(retour.equals(expected));
	}
	
	@org.junit.Test
	public void testObjectToElement(){
		Stringable p = new Point(1,2);
		String meth ="public void toString (){return \"hello\";";
		ArrayList<String>listeMethode = new ArrayList<String>();
		listeMethode.add(meth);
		Element e = ObjectToXML.objectToElement("1", Stringable.class, null, p, listeMethode, doc);
		doc.appendChild(e);
		String expected = "<value >"+
							"<object oid=\"1\" type=\"objets.Stringable\" >"+
								"<fields >"+
									"<field name=\"x\" >"+
										"<value >"+
											"<double >1.0</double>"+
										"</value>"+
									"</field>"+
									"<field name=\"y\" >"+
										"<value >"+
											"<double >2.0</double>"+
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
										"public void toString (){return \"hello\";</method>"+
								"</methods>"+
							"</object>"+
						"</value>";
		assertTrue(expected.equals(ObjectToXML.docToString(doc)));
	}
	
	public void testGetOidFromXML()
	{
		Stringable p = new Point(1,2);
		String meth ="public void toString (){return \"hello\";";
		ArrayList<String>listeMethode = new ArrayList<String>();
		listeMethode.add(meth);
		Element e = ObjectToXML.objectToElement("1", Stringable.class, null, p, listeMethode, doc);
		doc.appendChild(e);
		String oidTest = ObjectToXML.getOidFromXML(doc).get(0);
		assertTrue(oidTest.equals("1"));
	}
	
	//TODO
	public void testGetFieldsByOID()
	{
		Stringable p = new Point(1,2);
		String meth ="public void toString (){return \"hello\";";
		ArrayList<String>listeMethode = new ArrayList<String>();
		listeMethode.add(meth);
		Element e = ObjectToXML.objectToElement("1", Stringable.class, null, p, listeMethode, doc);
		doc.appendChild(e);
		String expected = "<object oid=\"1\" type=\"objets.Stringable\" >"+
				"<fields >"+
				"<field name=\"x\" >"+
					"<value >"+
						"<double >1.0</double>"+
					"</value>"+
				"</field>"+
				"<field name=\"y\" >"+
					"<value >"+
						"<double >2.0</double>"+
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
					"public void toString (){return \"hello\";</method>"+
			"</methods>"+
		"</object>";
		assertTrue(ObjectToXML.docToString(doc).equals(expected));
	}
	
	public void testUpdateObjectFromElement()
	{
		Point p = new Point(0,0);
		String stringDoc = "<object oid=\"1\" type=\"objets.Stringable\" >"+
				"<fields >"+
				"<field name=\"x\" >"+
					"<value >"+
						"<double >1.0</double>"+
					"</value>"+
				"</field>"+
				"<field name=\"y\" >"+
					"<value >"+
						"<double >2.0</double>"+
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
					"public void toString (){return \"hello\";</method>"+
			"</methods>"+
		"</object>";
		
		Document d = ObjectToXML.stringToDoc(stringDoc);
		ObjectToXML.updateObjectFromElement((Element)d.getFirstChild(),p);
		assertTrue(p.getX()==1.0);
		assertTrue(p.getY()==2.0);
		
	}
	
	//TODO  updateFieldByTypeDouble
	public void testUpdateFieldByTypeDouble(){
		String type = "double";
		Point p = new Point(0,0);
		
		assertTrue(true);
	}
	
	public void testCreateAppelClient()
	{
		int a =2 ;
		Stringable p = new Point (1,3);
		ArrayList<Class<?>>inters =new ArrayList<Class<?>>();
		ArrayList<Object> params = new ArrayList<Object>();
		
	}

}
