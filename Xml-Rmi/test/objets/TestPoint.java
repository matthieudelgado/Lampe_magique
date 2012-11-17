package objets;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import client.Client;

import tools.ObjectToXML;
import tools.Validateur;

public class TestPoint {

	private Point p;
	private Document doc;
	

	@Before
	/**
	 * Methode qui crée l'objet Point
	 */
	public void beforeTest()
	{
		Client c =new Client();
		p = new Point(2,2);
		doc = ObjectToXML.creerDocument();
	}


	@org.junit.Test
	/**
	 * 
	 */
	public void testToXML()
	{
		Element e = p.toXML(Stringable.class,doc);
		String rendu = ObjectToXML.docToString(e);
		String expected = "<value >" +
							"<object oid=\"Point_0\" type=\"objets.Stringable\" >" +
									"<fields ><field name=\"x\" >" +
											"<value ><double >2.0</double></value>" +
									"</field><field name=\"y\" >" +
											"<value ><double >2.0</double></value>" +
									"</field><field name=\"mark\" >" +
											"<value ><string >nom de la marque</string></value>" +
									"</field></fields>" +
									"<methods >" +
										"<method language=\"Java\" >public String toString(){return \"x = \"+this.x+ \" y =  \" + this.y+ \" marque = \"+this.mark;}" +
										"</method>" +
									"</methods>" +
								"</object>" +
							"</value>";
		
		assertTrue(expected.equals(rendu));
		String testGrammaire =  ObjectToXML.docToString((Element)e.getFirstChild());
		//TODO A checker
		try {
			Validateur.validateXmlAgainstRnc(testGrammaire, "schemas/object.rnc");
		} catch (SAXException e1) {
			assertTrue(false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@org.junit.Test
	/**
	 * On test ici initOid en verifiant  que la methode modifie l'oid
	 */
	public void testInitOid()
	{
		 p.initOid();
		 String oidAvant = p.getOid();
		 p.initOid();
		 String oidApres = p.getOid();
		 assertTrue(!oidAvant.equals(oidApres));
	}
	
	@org.junit.Test
	public void testReverse()
	{
		double tmpX = p.getX();
		double tmpY = p.getY();
		p.reverse();
		assertTrue(tmpY==p.getX()&&tmpX==p.getY());
	}
	
	//TODO a debugguer!!!
	@org.junit.Test
	public void testUpdate(){
		String t ="<field><object oid=\"Point_0\" type=\"objets.Stringable\" >" +
						"<fields ><field name=\"x\" >" +
							"<value ><double >1.0</double></value>" +
								"</field><field name=\"y\" >" +
							"<value ><double >5.0</double></value>" +
								"</field><field name=\"mark\" >" +
							"<value ><string >nom de la marque</string></value>" +
							"</field></fields>" +
							"<methods >" +
								"<method language=\"Java\" >public String toString(){return \"x = \"+this.x+ \" y =  \" + this.y+ \" marque = \"+this.mark;}" +
							"</method>" +
							"</methods>" +
					"</object></field>";
		doc = ObjectToXML.stringToDoc(t);
		p.updateFromXML((Element)doc.getFirstChild());
		System.out.println(p.getX());
		assertTrue(p.getX()==1.0);
		assertTrue(p.getY()==5.0);
	}
	
}
