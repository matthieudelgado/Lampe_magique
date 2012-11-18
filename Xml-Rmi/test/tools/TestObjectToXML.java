package tools;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import objets.Point;
import objets.Stringable;
import objets.XMLRMISerializable;

import org.junit.After;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * 
 * @author marcgregoire
 * @author matthieudelagado
 * 
 * Classe de Test de la classe ObjectToXML
 */
public class TestObjectToXML {

	
	private Document doc;

	@Before
	public void beforeTest(){
		doc = ObjectToXML.creerDocument();
	}

	@After
	public void afterTest(){
		//doc= null;
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
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'un entier construit l'element correspondant
	 */
	public void testGetContenuNodePrimitifInt()
	{
		int a = 3;
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("int"));
		assertTrue(e.getFirstChild().getTextContent().equals("3"));

	}

	@org.junit.Test
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'un double construit l'element correspondant
	 */
	public void testGetContenuNodePrimitifDouble()
	{
		double a = 3.0;
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("double"));
		assertTrue(e.getFirstChild().getTextContent().equals("3.0"));
	}

	@org.junit.Test
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'un booleen construit l'element correspondant
	 */
	public void testGetContenuNodePrimitifBoolean()
	{
		boolean a = true;
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("boolean"));
		assertTrue(e.getFirstChild().getTextContent().equals("1"));
	}

	@org.junit.Test
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'un string construit l'element correspondant
	 */
	public void testGetContenuNodePrimitifString()
	{
		String a = "Hello";
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("string"));
		assertTrue(e.getFirstChild().getTextContent().equals("Hello"));
	}

	@org.junit.Test
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'une date construit l'element correspondant
	 */
	public void testGetContenuNodePrimitifDate()
	{
		Date a = new Date();
		a.setTime(0);
		Element e = ObjectToXML.getContenuNodePrimitif(a, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("dateTime.iso8601"));
		assertTrue(e.getFirstChild().getTextContent().equals("19700101T01:00:00"));
	}

	@org.junit.Test
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'une liste d'entier construit l'element correspondant
	 */
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
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'un liste de double construit l'element correspondant
	 */
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
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'une liste de booleen construit l'element correspondant
	 */
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
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'une liste de string construit l'element correspondant
	 */
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
	/**
	 * /**
	 * Test de la methode getContenuPrimitif qui a partir d'une liste de Date construit l'element correspondant
	 */
	 
	public void testGetContenuNodePrimitifArrayDate()
	{
		Date[] la = new Date[1];
		Date a = new Date();
		a.setTime(0);
		la[0] = a;
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, null, null);
		assertTrue(e.getFirstChild().getNodeName().equals("array"));
		assertTrue(e.getFirstChild().getFirstChild().getNodeName().equals("value"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getNodeName().equals("dateTime.iso8601"));
		assertTrue(e.getFirstChild().getFirstChild().getFirstChild().getTextContent().equals("19700101T01:00:00"));
	}

	@org.junit.Test
	/**
	 * Test de la methode getContenuPrimitif qui a partir d'une liste d'objets serializable construit l'element correspondant
	 */
	public void testGetContenuNodePrimitifArrayXMLRMI()
	{
		XMLRMISerializable[] la = new XMLRMISerializable[1] ;
		la[0] = new Point(1,1);
		ArrayList<Class<?>> liter =new ArrayList<Class <?>>();
		liter.add(XMLRMISerializable.class);
		Element e = ObjectToXML.getContenuNodePrimitif(la, doc, liter, 0);
		doc.appendChild(e);
		ObjectToXML.afficherDocument(doc);
		String expected = "<value ><array ><value >"+
				"<object oid=\"Point_0\" type=\"objets.XMLRMISerializable\" >"+
				"<fields >"+
				"<field name=\"x\" >"+
				"<value >"+
				"<double >1.0</double>"+
				"</value>"+
				"</field>"+
				"<field name=\"y\" >"+
				"<value >"+
				"<double >1.0</double>"+
				"</value>"+
				"</field>"+
				"<field name=\"mark\" >"+
				"<value >"+
				"<string >nom de la marque</string>"+
				"</value>"+
				"</field>"+
				"</fields>"+
				"<methods >"+
				"</methods>"+
				"</object>"+
				"</value></array></value>";
		assertTrue(expected.equals(ObjectToXML.docToString(doc)));
	}

	@org.junit.Test
	/**
	 * Test de la conversion d'une Date en dateTime.iso8601
	 */
	public void testDateToDateTime()
	{
		Date d = new Date();
		d.setTime(0);
		String date = ObjectToXML.dateToDateTime(d);
		assertTrue(date.equals("19700101T01:00:00"));
	}

	@org.junit.Test
	/**
	 * Test de la methode appelClient
	 */
	public void testAppelClient(){
		Document doc = ObjectToXML.appelClient("test");
		String retour= ObjectToXML.docToString(doc);
		String expected ="<methodCall >" +
				"<methodName >test</methodName>" +
				"<params ></params>" +
				"</methodCall>";
		assertTrue(retour.equals(expected));
	}

	@org.junit.Test
	/**
	 * Test de serialization d'un objet
	 */
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

	@org.junit.Test
	/**
	 * Test qui retourne l'oid d'un object dans un XML 
	 */
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


	@org.junit.Test
	/**
	 * Test qui retourne les fields d'un objet en fonction de son OID
	 */
	public void testGetFieldsByOID()
	{
		Stringable p = new Point(1.0,2.0);
		String meth ="public void toString (){return \"hello\";";
		ArrayList<String>listeMethode = new ArrayList<String>();
		listeMethode.add(meth);
		Element e = ObjectToXML.objectToElement("1", Stringable.class, null, p, listeMethode, doc);
		doc.appendChild(e);

		Element o = ObjectToXML.getFieldsByOID(doc, "1");

		String expected = 
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
						"</fields>";
		assertTrue(ObjectToXML.docToString(o).equals(expected));
	}


	@org.junit.Test
	/**
	 * Test qui met a jour un objet en fonction d'un element
	 */
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


		ObjectToXML.updateObjectFromElement((Element)d.getFirstChild().getFirstChild(),p);
		assertTrue(p.getX()==1.0);
		assertTrue(p.getY()==2.0);

	}

	//TODO  updateFieldByTypeDouble
	@org.junit.Test
	public void testUpdateFieldByTypeDouble(){
		String type = "double";
		Point p = new Point(0,0);

		assertTrue(true);
	}

	@org.junit.Test
	/**
	 * Test de la methode createAppelClient
	 */
	public void testCreateAppelClient()
	{
		int a =2 ;
		Stringable p = new Point (1.0,3.0);
		ArrayList<Class<?>>inters =new ArrayList<Class<?>>();
		inters.add(Stringable.class);
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(a);
		params.add(p);
		doc = ObjectToXML.createAppelClient(inters, "display", params);
		String ex ="<methodCall >"+
						"<methodName >display</methodName>"+
						"<params >"+
							"<param >"+
								"<value >"+
									"<int >2</int>"+
								"</value>"+
							"</param>"+
							"<param >"+
								"<value >"+
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
								"</value>"+
							"</param>"+
						"</params>"+
				"</methodCall>";

		String rend = ObjectToXML.docToString(doc);
		assertTrue(ex.equals(rend));
		try {
			Validateur.validateXmlAgainstRnc(rend, "schemas/xml-rmi.rnc");
		} catch (SAXException e) {
			assertTrue(false);
			return ;
		} catch (IOException e) {
			System.out.println("File non trouve");
		}
		
	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre un entier
	 */
	public void TestObjectWithoutAnnotationsToElementInt(){
		int a  = 2;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, a, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<int >2</int>" +
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre un double
	 */
	public void TestObjectWithoutAnnotationsToElementDouble(){
		double a  = 2.0;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, a, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<double >2.0</double>" +
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre un element null
	 */
	public void TestObjectWithoutAnnotationsToElementVoid(){
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, null, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"void" +
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre un boolean
	 */
	public void TestObjectWithoutAnnotationsToElementBoolean(){
		boolean a = true;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, a, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<boolean >1</boolean>" +
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre un string
	 */
	public void TestObjectWithoutAnnotationsToElementString(){
		String  a = "Hello" ;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, a, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<string >Hello</string>" +
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre une date
	 */
	public void TestObjectWithoutAnnotationsToElementDate(){
		Date a = new Date();
		a.setTime(0);
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, a, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<dateTime.iso8601 >19700101T01:00:00</dateTime.iso8601>" +
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre une liste d'entier
	 */
	public void TestObjectWithoutAnnotationsToElementArrayInt(){
		int a = 1;
		int b = 2;
		int[] liste = new int[2];
		liste[0]=a;
		liste[1]=b;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, liste, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<array >" +
				"<value >"+
				"<int >1</int>" +
				"</value>"+
				"<value >"+
				"<int >2</int>" +
				"</value>"+
				"</array>"+
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre d'une liste de double
	 */
	public void TestObjectWithoutAnnotationsToElementArrayDouble(){
		double a = 1.0;
		double b = 2.0;
		double[] liste = new double[2];
		liste[0]=a;
		liste[1]=b;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, liste, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<array >" +
				"<value >"+
				"<double >1.0</double>" +
				"</value>"+
				"<value >"+
				"<double >2.0</double>" +
				"</value>"+
				"</array>"+
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}

	@org.junit.Test
	/**
	 * Test de la methode objectWithoutAnnotationToElement en lui passant en parametre une liste de boolean
	 */
	public void TestObjectWithoutAnnotationsToElementArrayBoolean(){
		boolean a = true;
		boolean b = false;
		boolean[] liste = new boolean[2];
		liste[0]=a;
		liste[1]=b;
		try {
			Element e = ObjectToXML.objectWithoutAnnotationsToElement(null, null, liste, null, doc);
			doc.appendChild(e);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		String expected = "<value >"+
				"<array >" +
				"<value >"+
				"<boolean >1</boolean>" +
				"</value>"+
				"<value >"+
				"<boolean >0</boolean>" +
				"</value>"+
				"</array>"+
				"</value>";

		assertTrue(expected.equals(ObjectToXML.docToString(doc)));

	}



}
