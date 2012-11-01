package tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtField.Initializer;
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
			boolean isOb= false; //a changer?
			if(nl.item(0).getFirstChild().getNodeName().equals("object")) isOb = true;//a changer
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
			if(isOb)
				return toArray(array,parameterType.getComponentType());
			//ici on cast ?

			return toArray(array, arrayClass);

		} else if(node.getNodeName().equalsIgnoreCase("struct")){
			//TODO a completer
		} else if(node.getNodeName().equalsIgnoreCase("object")){
			//on creer une hashmap pour garder la valeur des champs
			HashMap<String, Object> fieldMap = new HashMap<String, Object>();
			//on creer une classe avec pour nom l'oid de l'objet
			String oid = node.getAttributes().getNamedItem("oid").getNodeValue();
			CtClass clazz ;
			boolean classAlreadyExists = true;
			if((clazz = ClassPool.getDefault().getOrNull(oid)) == null)
			{
				clazz = ClassPool.getDefault().makeClass(oid);
				if(parameterType.isInterface())
					clazz.addInterface(ClassPool.getDefault().get(parameterType.getName()));
				clazz.stopPruning(true);
				classAlreadyExists = false;
			} 
			Object o = null;
			NodeList nl = node.getChildNodes(), nl2;
			Node current, n, granChild;
			String name;
			if(classAlreadyExists)
			{
				for(int i = 0; i< nl.getLength(); i++)
				{
					current = nl.item(i);
					//on parcours les fields et les method
					if(current.getNodeName().equalsIgnoreCase("fields"))
					{
						nl2 = current.getChildNodes();
						for(int j = 0; j< nl2.getLength(); j++)
						{
							n = nl2.item(j);
							//on ajoute chaque field dans la classe et on stoque la valeur dans la hashmap
							if(!n.getNodeName().equals("field")) continue;
							name = n.getAttributes().getNamedItem("name").getNodeValue();
							granChild = getFirstGranChild(n);
							Object value = null;
							if(granChild.getNodeName().equals("object"))
							{
								value = createObjectFromNode(granChild, 
										Class.forName(granChild.getAttributes().getNamedItem("type").getTextContent()));
								fieldMap.put(name, value);
							} 
							else
							{
								value = createObjectFromNode(granChild, Object.class);

							}
							clazz.defrost();
							clazz.removeField(clazz.getField(name));
							addCtFieldToCtClass(granChild, name, clazz, value);


						}
					}
				}
				clazz.setName(clazz.getName()+"_1");
				clazz.detach();
				clazz.freeze();
				o = clazz.toClass().newInstance();
			}
			else
			{
				for(int i = 0; i< nl.getLength(); i++){
					current = nl.item(i);
					//on parcours les fields et les method
					if(current.getNodeName().equalsIgnoreCase("fields"))
					{
						nl2 = current.getChildNodes();
						for(int j = 0; j< nl2.getLength(); j++)
						{
							n = nl2.item(j);
							//on ajoute chaque field dans la classe et on stoque la valeur dans la hashmap
							if(!n.getNodeName().equals("field")) continue;
							name = n.getAttributes().getNamedItem("name").getNodeValue();
							granChild = getFirstGranChild(n);
							Object value = null;
							if(granChild.getNodeName().equals("object"))
							{
								System.out.println("avant objet");
								value = createObjectFromNode(granChild, 
										Class.forName(granChild.getAttributes().getNamedItem("type").getTextContent()));
								System.out.println("apres objet");
								fieldMap.put(name, value);
							} 
							else
							{
								System.out.println("name : "+name+", type : "+granChild.getNodeName());
								value = createObjectFromNode(granChild, Object.class);
							}	
							addCtFieldToCtClass(granChild, name, clazz, value);

						}
					} 
					else if(current.getNodeName().equalsIgnoreCase("methods"))
					{
						//on ajoute la method a la classe
						nl2 = current.getChildNodes();
						for(int j = 0; j< nl2.getLength(); j++)
						{
							n = nl2.item(j);
							if(!n.getNodeName().equals("method"))continue;
							if(!n.getAttributes().getNamedItem("language").getNodeValue().equalsIgnoreCase("java"))continue;
							//String nm ="public String toString(){return \"x = \"+this.x+ \" y = \" + this.y;}";
							CtMethod m = CtNewMethod.make(n.getTextContent(), clazz);
							clazz.addMethod(m);
						}
					}
				}

				//on initialise les champs de l'instance
				o = clazz.toClass().newInstance();
				Field f = null;
				for(String key : fieldMap.keySet())
				{
					f = o.getClass().getField(key);
					f.setAccessible(true);
					f.set(o,fieldMap.get(key) );
					f.setAccessible(false);
				}


				System.out.println("contenu de la class "+o.getClass().getSimpleName());
				for(Field f1 : o.getClass().getDeclaredFields()){
					System.out.println(f1.getName());
				}
				System.out.println("contenu de la CTclass "+clazz.getName());
				for(CtField f1 : clazz.getFields()) 
					System.out.println(f1.getName());

				System.out.println();
			}

			return o;
		} 

		return null;
	}

	public static boolean typeChecker(Class<?> classe, Node noeud){
		//System.out.println("typechecker : "+classe.getSimpleName()+ " "+noeud.getNodeName());
		if(classe.equals(int.class))
		{
			return(noeud.getNodeName().equals("int"));
		}
		else if(classe.equals(double.class))
		{
			return(noeud.getNodeName().equals("double"));
		}
		else if(classe.equals(boolean.class))
		{
			return(noeud.getNodeName().equals("boolean"));
		}
		else if(classe.equals(String.class))
		{
			return(noeud.getNodeName().equals("string"));
		}
		else if(classe.isArray())
		{
			//typechek tout les element de la liste
			ArrayList<Node> lnode = getGrandChildList(noeud);
			boolean b = true;
			for(Node n : lnode)
			{
				b = b && typeChecker(classe.getComponentType(), n);	
			}
			//System.out.println("retour de typeChecker pour le tableau : "+b);
			return b;
		}
		else if(classe.isInterface())
		{
			if(noeud.getAttributes().getNamedItem("type") == null)return false;
			String type = noeud.getAttributes().getNamedItem("type").getNodeValue();
			return type.equals(classe.getName());
		}
		else {
			System.err.println("type checker : cas non traité");
			return false;
		}
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
		System.err.println("class a caster : "+arrayClass.getName());
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
		if(n.getNodeName().equalsIgnoreCase("int"))
		{
			CtField f = new CtField(CtClass.intType,name,clazz);
			f.setModifiers(Modifier.PUBLIC);
			clazz.addField(f, value.toString());
		} 
		else if(n.getNodeName().equalsIgnoreCase("double"))
		{
			CtField f = new CtField(CtClass.doubleType,name,clazz);
			f.setModifiers(Modifier.PUBLIC);
			clazz.addField(f, value.toString());
		} 
		else if(n.getNodeName().equalsIgnoreCase("boolean"))
		{
			CtField f = new CtField(CtClass.booleanType,name,clazz);
			f.setModifiers(Modifier.PUBLIC);
			clazz.addField(f, value.toString());
		} 
		else if(n.getNodeName().equalsIgnoreCase("string"))
		{
			CtField f = new CtField(ClassPool.getDefault().get("java.lang.String"),name,clazz);
			f.setModifiers(Modifier.PUBLIC);
			clazz.addField(f, "\""+value.toString()+"\"");
		} 
		else if(n.getNodeName().equalsIgnoreCase("object"))
		{
			CtField f = new CtField(ClassPool.getDefault().get(n.getAttributes().getNamedItem("type").getTextContent()),name,clazz);
			f.setModifiers(Modifier.PUBLIC);
			clazz.addField(f);
		}
		else {
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
		return null;
	}

	private static ArrayList<Node> getGrandChildList(Node node)
	{
		NodeList nl = node.getChildNodes(), nl2;
		ArrayList<Node> ret = new ArrayList<Node>();
		for(int i = 0; i< nl.getLength(); i++)
		{
			if(nl.item(i).getNodeType() == 3) continue;
			nl2 = nl.item(i).getChildNodes();
			for(int j = 0; j<nl2.getLength();j++)
			{
				if(nl2.item(j).getNodeType() == 3)continue;
				ret.add(nl2.item(j));
			}
		}
		return ret;
	}

	public void updateFromXml(Document doc,ArrayList<Object> lo){
		String oid=doc.getElementsByTagName("object").item(0).getAttributes().getNamedItem("oid").getTextContent();
		System.out.println(oid);
		Object objToUpdate;

		// pour tout les objets, recupere la valeur de l'oid dans l'annotation

	}
}
