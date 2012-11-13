package objets;

import static org.junit.Assert.assertTrue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestPoint {

	private Point p;
	private Document doc;

	@Before
	/**
	 * Methode qui crée l'objet Point
	 */
	public void beforeTest()
	{
		p = new Point(2,2);
		DocumentBuilder docB = null;
		try {
			docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {

		}
		doc = docB.newDocument();
	}


	@org.junit.Test
	/**
	 * 
	 */
	public void testToXML()
	{
		Element e = p.toXML(Stringable.class,doc);
		assertTrue(true);
		// ajouter un test de la grammaire et en faire une verif
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
	
}
