package tools;

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
		
		System.out.println(corpsMethode);
		
			
			 CtClass point = ClassPool.getDefault().makeClass("Point");
			
			CtField f  = new CtField(CtClass.intType,"x",point);
			point.addField(f,"0");
			CtField f1  = new CtField(CtClass.charType,"mark",point);
			point.addField(f1);
			CtField f2  = new CtField(CtClass.intType,"y",point);
			point.addField(f2,"3");
			
			CtMethod m = CtNewMethod.make(corpsMethode, point);
			point.addMethod(m);
		
	
			Object p1 =point.toClass().newInstance();
		
		return p1;
	}
}
