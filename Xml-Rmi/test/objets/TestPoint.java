package objets;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import client.Client;

import tools.ObjectToXML;
import tools.Validateur;
/**
 * 
 * @author marcgregoire
 * @author matthieudelgado
 * 
 * Test de la classe Point
 */
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
	
	@After
	public void afterTest(){
		doc = null;
	}


	@org.junit.Test
	/**
	 * Test de la methode toXML
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
	/**
	 * Test de la methode reverse()
	 */
	public void testReverse()
	{
		double tmpX = p.getX();
		double tmpY = p.getY();
		p.reverse();
		assertTrue(tmpY==p.getX()&&tmpX==p.getY());
	}
	
	//TODO a debugguer!!!
	//@org.junit.Test
	/**
	 * Test de la methode update
	 */
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
