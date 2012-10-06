package tools;

import javassist.CannotCompileException;

import org.w3c.dom.Document;

public class TestXmlToObject {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ObjectToXML otx = new ObjectToXML();
		
		
		Document doc =  otx.fileToDoc( "data/appelServeur.xml");
		
		XMLToObject xto =  new XMLToObject();
		 Object p;
		try {
			p = xto.createObject(doc);
			System.out.println(p.toString());
			
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
