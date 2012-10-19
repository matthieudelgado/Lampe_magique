package tools;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtField.Initializer;
import javassist.bytecode.Bytecode;
import javassist.compiler.Javac;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XMLToObject {


	/**
	 * TODO a redefinir en static
	 * TODO a renomer
	 * @param doc
	 * @return
	 * @throws CannotCompileException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws NotFoundException
	 */
	public Object createObject(Document doc) throws CannotCompileException, InstantiationException, IllegalAccessException, NotFoundException{

		// on recupere le contenu de la balise method
		String corpsMethode= doc.getElementsByTagName("method").item(0).getTextContent();
		//String corpsMethode = "public String toString(){return \"r\";}";
		String x = doc.getElementsByTagName("double").item(0).getTextContent();
		String y = doc.getElementsByTagName("double").item(1).getTextContent();
		String a =doc.getElementsByTagName("string").item(0).getTextContent();
		a="\""+a+"\"";
		System.out.println(corpsMethode);


		CtClass point = ClassPool.getDefault().makeClass("Point");

		CtField f  = new CtField(CtClass.doubleType,"x",point);
		point.addField(f,x);
		CtField f1  = new CtField(ClassPool.getDefault().get("java.lang.String"),"mark",point);
		point.addField(f1,a);

		CtField f2  = new CtField(CtClass.doubleType,"y",point);
		point.addField(f2,y);

		CtMethod m = CtNewMethod.make(corpsMethode, point);
		point.addMethod(m);


		Object p1 =point.toClass().newInstance();


		return p1;
	}

	/**
	 * 
	 * @param <T>
	 * @param node
	 * @param parameterType
	 * @return
	 * @throws Exception
	 */
	public static <T> Object createObjectFromNode(Node node, Class<?> parameterType) throws Exception{
		if(!node.getParentNode().getNodeName().equalsIgnoreCase("value")) 
			throw new Exception("le pere de l'objet n'est pas value"); 
		if(node.getNodeName().equalsIgnoreCase("int")){
			return Integer.parseInt(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("double")){
			return Double.parseDouble(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("boolean")){
			if(((Text)node.getFirstChild()).getData().equals("0")) return Boolean.FALSE;
			else return Boolean.TRUE;
		} else if(node.getNodeName().equalsIgnoreCase("string")){
			return new String(((Text)node.getFirstChild()).getData());
		} else if(node.getNodeName().equalsIgnoreCase("dateTime")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("base64")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("array")){
			ArrayList<Object> array = new ArrayList<Object>();
			NodeList nl = node.getChildNodes(), nl2 = null;
			Node firstChild = null;
			for(int i = 0; i < nl.getLength(); i++){
				nl2 = nl.item(i).getChildNodes();
				for(int j = 0; j < nl2.getLength(); j++){
					if(nl2.item(j).getNodeType() !=3) {
						firstChild = nl2.item(j);
						break;
					}
				}
				if( firstChild == null)continue;
				array.add(createObjectFromNode(firstChild, parameterType.getComponentType()));
			}
			if(array.size() == 0) return array.toArray();

			Class<?> arrayClass = array.get(0).getClass();
			return toArray(array, arrayClass);

		} else if(node.getNodeName().equalsIgnoreCase("struct")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("object")){
			System.err.println("hello");
			//on creer une hashmap pour garder la valeur des champs
			HashMap<String, Object> fieldMap = new HashMap<String, Object>();
			//on creer une classe avec pour nom l'oid de l'objet
			String oid = node.getAttributes().getNamedItem("oid").getNodeValue();
			CtClass clazz = ClassPool.getDefault().makeClass(oid);
			if(parameterType.isInterface())
				clazz.addInterface(ClassPool.getDefault().get(parameterType.getName()));
			clazz.stopPruning(true);

			NodeList nl = node.getChildNodes(), nl2;
			Node current, n, granChild;
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
						granChild = getFirstGranChild(n);
						Object value = createObjectFromNode(granChild, Object.class);
						addCtFieldToCtClass(granChild, name, clazz, value);
						//f= new CtField(CtClass.doubleType,name,clazz);
						//clazz.addField(f,"2.0"); // TODO A CHANGER 
						//fieldMap.put(name, );
					}
				} else if(current.getNodeName().equalsIgnoreCase("methods")){
					//on ajoute la method a la classe
					nl2 = current.getChildNodes();
					for(int j = 0; j< nl2.getLength(); j++){
						n = nl2.item(j);
						if(!n.getNodeName().equals("method"))continue;
						if(!n.getAttributes().getNamedItem("language").getNodeValue().equalsIgnoreCase("java"))continue;
						System.err.println(n.getTextContent());
						//String nm ="public String toString(){return \"x = \"+this.x+ \" y = \" + this.y;}";
						CtMethod m = CtNewMethod.make(n.getTextContent(), clazz);
						clazz.addMethod(m);
					}
				}
			}



			//on initialise les champs de l'instance
			Object o = clazz.toClass().newInstance();

			return o;
		} 

		return null;
	}

	private static <T> Object toArray(ArrayList<Object> array, Class<?> arrayClass) {
		if(arrayClass.equals(Integer.class)){
			int[] tab = new int[array.size()];
			for(int i = 0; i< array.size();i++){
				tab[i] = ((Integer)array.get(i)).intValue();
			}
			return tab;
		} else if(arrayClass.equals(Double.class)){
			double[] tab = new double[array.size()];
			for(int i = 0; i< array.size();i++){
				tab[i] = ((Double)array.get(i)).doubleValue();
			}
			return tab;
		} else if (arrayClass.equals(Boolean.class)){
			boolean[] tab = new boolean[array.size()];
			for(int i = 0; i< array.size();i++){
				tab[i] = ((Boolean)array.get(i)).booleanValue();
			}
			return tab;
		}
		return array.toArray((T[]) Array.newInstance(arrayClass, array.size()));	
	}

	private static Class<?> getPrimitiveClass(Class<?> c) {
		if(c.equals(Integer.class)) return int.class;
		if(c.equals(Double.class)) return double.class;
		if(c.equals(Boolean.class)) return boolean.class;
		return c;
	}

	/**
	 * 
	 * @param n
	 * @param name
	 * @param clazz
	 * @param value
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private static void addCtFieldToCtClass(Node n, String name, CtClass clazz, Object value) throws CannotCompileException, NotFoundException {
		if(n.getNodeName().equalsIgnoreCase("int")){
			CtField f = new CtField(CtClass.intType,name,clazz);
			clazz.addField(f, value.toString());
		} else if(n.getNodeName().equalsIgnoreCase("double")){
			CtField f = new CtField(CtClass.doubleType,name,clazz);
			clazz.addField(f, value.toString());
		} else if(n.getNodeName().equalsIgnoreCase("boolean")){
			CtField f = new CtField(CtClass.booleanType,name,clazz);
			clazz.addField(f, value.toString());
		} else if(n.getNodeName().equalsIgnoreCase("string")){
			CtField f = new CtField(ClassPool.getDefault().get("java.lang.String"),name,clazz);
			clazz.addField(f, "\""+value.toString()+"\"");
		} else {
			System.out.println("type non traité dans addCtFieldToCtClass");
		}

	}

	private static Node getFirstGranChild(Node node){
		NodeList nl = node.getChildNodes(), nl2;
		for(int i = 0; i< nl.getLength(); i++){

			if(nl.item(i).getNodeType() == 3) continue;
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
