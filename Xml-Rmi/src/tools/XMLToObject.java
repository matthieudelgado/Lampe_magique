package tools;

import java.util.ArrayList;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

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

	public static Object createObjectFromNode(Node node) throws Exception{
		System.err.println(node.getNodeName());
		if(!node.getParentNode().getNodeName().equalsIgnoreCase("value")) 
			throw new Exception("le pere de l'objet n'est pas value"); 
		if(node.getNodeName().equalsIgnoreCase("int")){
			return Integer.parseInt(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("double")){
			return Double.parseDouble(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("boolean")){
			return Boolean.parseBoolean(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("string")){
			return new String(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("dateTime")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("base64")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("array")){
			ArrayList<Object> array = new ArrayList<Object>();
			NodeList nl = node.getChildNodes();
			for(int i = 0; i < nl.getLength(); i++){
				array.add(createObjectFromNode(nl.item(i)));
			}
			return array;
		} else if(node.getNodeName().equalsIgnoreCase("struct")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("object")){
			System.err.println("hello");
			//on creer une hashmap pour garder la valeur des champs
			HashMap<String, Object> fieldMap = new HashMap<String, Object>();
			//on creer une classe avec pour nom l'oid de l'objet
			String oid = node.getAttributes().getNamedItem("oid").getNodeValue();
			CtClass clazz = ClassPool.getDefault().makeClass(oid);
			NodeList nl = node.getChildNodes(), nl2;
			Node current, n;
			String name;
			for(int i = 0; i< nl.getLength(); i++){
				current = nl.item(i);
				//on parcours les fields et les method
				if(current.getNodeName().equalsIgnoreCase("fields")){
					nl2 = current.getChildNodes();
					for(int j = 0; j< nl2.getLength(); j++){
						n = nl2.item(j);
						//on ajoute chaque field dans la classe et on stoque la valeur dans la hashmap
						if(!n.getNodeName().equals("field")) continue;
						name = n.getAttributes().getNamedItem("name").getNodeValue();
						System.err.println("name = "+name);
						CtField f  = new CtField(CtClass.voidType,name,clazz);
						clazz.addField(f);
						fieldMap.put(name, createObjectFromNode(getFirstGranChild(n)));
					}
				} else if(current.getNodeName().equalsIgnoreCase("methods")){
					//on ajoute la method a la classe
					nl2 = current.getChildNodes();
					for(int j = 0; j< nl2.getLength(); j++){
						n = nl2.item(j);
						if(!n.getNodeName().equals("method"))continue;
						if(!n.getAttributes().getNamedItem("language").getNodeValue().equalsIgnoreCase("java"))continue;
						System.err.println(n.getTextContent());
						CtMethod m = CtNewMethod.make(n.getTextContent(), clazz);
						clazz.addMethod(m);
					}
				}
			}

			//on initialise les champs de l'instance
			Object o = clazz.toClass().newInstance();
			for(String s : fieldMap.keySet()){
				o.getClass().getDeclaredField(s).set(o, fieldMap.get(s));
			}
			return o;
		} 

		return null;
	}

	private static Node getFirstGranChild(Node node){
		System.err.println("node nam : "+node.getNodeName());
		NodeList nl = node.getChildNodes(), nl2;
		for(int i = 0; i< nl.getLength(); i++){
			System.out.println("un fils");

			if(nl.item(i).getNodeType() == 3) continue;
			System.out.println("un fils non vide");
			nl2 = nl.item(i).getChildNodes();
			for(int j = 0; j<nl2.getLength();j++){

				if(nl2.item(j).getNodeType() == 3)continue;
				return nl2.item(j);
			}


		}
		System.err.println("hi!");
		return null;
	}

	public void updateFromXml(Document doc,ArrayList<Object> lo){
		String oid=doc.getElementsByTagName("object").item(0).getAttributes().getNamedItem("oid").getTextContent();
		System.out.println(oid);
		Object objToUpdate;

		// pour tout les objets, recupere la valeur de l'oid dans l'annotation

	}
}
