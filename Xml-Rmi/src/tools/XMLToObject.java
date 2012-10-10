package tools;

import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.w3c.dom.Document;

public class XMLToObject {

	private Document doc;
	
	public XMLToObject() {
		this.doc=null;
	}
	
	public Object createObject(Document doc) throws CannotCompileException, InstantiationException, IllegalAccessException{
	
		// on recupere le contenu de la balise method
		String corpsMethode= doc.getElementsByTagName("method").item(0).getTextContent();
		//String corpsMethode = "public String toString(){return \"r\";}";
		String x = doc.getElementsByTagName("double").item(0).getTextContent();
		String y = doc.getElementsByTagName("double").item(1).getTextContent();
		
		
		System.out.println(corpsMethode);
		
			
			 CtClass point = ClassPool.getDefault().makeClass("Point");
			
			CtField f  = new CtField(CtClass.doubleType,"x",point);
			point.addField(f,x);
			CtField f1  = new CtField(CtClass.charType,"mark",point);
			point.addField(f1);
			CtField f2  = new CtField(CtClass.doubleType,"y",point);
			point.addField(f2,y);
			
			CtMethod m = CtNewMethod.make(corpsMethode, point);
			point.addMethod(m);
		
	
			Object p1 =point.toClass().newInstance();
		
		return p1;
	}
	
	public void updateFromXml(Document doc,ArrayList<Object> lo){
		String oid=doc.getElementsByTagName("object").item(0).getAttributes().getNamedItem("oid").getTextContent();
		System.out.println(oid);
		Object objToUpdate;
		
		// pour tout les objets, recupere la valeur de l'oid dans l'annotation
		
	}
}
