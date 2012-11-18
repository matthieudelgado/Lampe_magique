package tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette classe permet d'effectuer les transformation de xml vers des objets
 *
 */
public class XMLToObject {
	
	
	/**
	 * Cette methode n'est qu'un TEST ! Elle n'est pas utilisee dans le projet
	 * Cette methode recupere un objet dans un document 
	 * 
	 * @param doc le doucment
	 * @return
	 */
	public Object createObject(Document doc) throws CannotCompileException, InstantiationException, IllegalAccessException, NotFoundException{
		// on recupere le contenu de la balise method
		String corpsMethode= doc.getElementsByTagName("method").item(0).getTextContent();
		//String corpsMethode = "public String toString(){return \"r\";}";
		String x = doc.getElementsByTagName("double").item(0).getTextContent();
		String y = doc.getElementsByTagName("double").item(1).getTextContent();
		String a =doc.getElementsByTagName("string").item(0).getTextContent();
		a="\""+a+"\"";


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
	 * Cette methode permet de creer un objet a partir d'un noeud
	 * @param <T> le type du tableau si le neoud decrit un tableau
	 * @param node le noeud
	 * @param parameterType le type de l'objet
	 * @return l'objet
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
		} else if(node.getNodeName().equalsIgnoreCase("dateTime.iso8601")){
			//SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("[0-9]{4}[0-1][0-9][0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]");
			SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			String date = ((Text)node.getFirstChild()).getData().replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
			return ISO8601DATEFORMAT.parse(date);
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
							} else if(granChild.getNodeName().equals("array")){
								Class<?> typeArray = Array.newInstance(Class.forName(getFirstGranChild(granChild).getAttributes().getNamedItem("type").getTextContent()), 0).getClass();
								value = createObjectFromNode(granChild, typeArray);
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
								value = createObjectFromNode(granChild, 
										Class.forName(granChild.getAttributes().getNamedItem("type").getTextContent()));
								fieldMap.put(name, value);
							}
							else if(granChild.getNodeName().equals("array")){
								Class<?> typeArray = Array.newInstance(Class.forName(getFirstGranChild(granChild).getAttributes().getNamedItem("type").getTextContent()), 0).getClass();
								value = createObjectFromNode(granChild, typeArray);
								fieldMap.put(name, value);
							}
							else
							{
								value = createObjectFromNode(granChild, Object.class);
							}	
							addCtFieldToCtClass(granChild, name, clazz, value);

						}
					} 
					else if(current.getNodeName().equalsIgnoreCase("methods"))
					{
						//on ajoute la method a la classe
						
						// faire un cas pour les array
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

			}

			return o;
		} 

		return null;
	}

	/**
	 * Cette methode verfie que l'objet decrit par le noeud noeud est de type classe
	 * @param classe la classe
	 * @param noeud le noeud
	 * @return vrai si oui faux sinon
	 */
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
		else if(classe.equals(Date.class))
		{
			return(noeud.getNodeName().equals("dateTime.iso8601"));
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


	/**
	 * Cette methode transforme la liste d'object de type arrayClass en tableau de type arrayClass
	 * @param array le tableau d'object
	 * @param arrayClass le type du tableau
	 * @return l'objet
	 */
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


	/**
	 * Cette methode permet d'ajouter a la classe le champs de nom name et de value value et de type n.getNodename() 
	 * @param n le noeud
	 * @param name le nom du field
	 * @param clazz la ctClass
	 * @param value la valeur
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 * @throws NegativeArraySizeException
	 * @throws DOMException
	 * @throws ClassNotFoundException
	 */
	private static void addCtFieldToCtClass(Node n, String name, CtClass clazz, Object value) throws CannotCompileException, NotFoundException, NegativeArraySizeException, DOMException, ClassNotFoundException {
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
		else if(n.getNodeName().equalsIgnoreCase("array")){
			
			Class<?> typeArray = Array.newInstance(Class.forName(getFirstGranChild(n).getAttributes().getNamedItem("type").getTextContent()), 0).getClass();
			CtField f =  new CtField(ClassPool.getDefault().get(typeArray.getName()),name,clazz);
			f.setModifiers(Modifier.PUBLIC);
			clazz.addField(f);
			
		}
		else {
			System.out.println("type non traité dans addCtFieldToCtClass");
		}

	}

	/**
	 * Cette methode permet de recuperer le premier petit fils du noeud
	 * @param node le noeud grand pere
	 * @return le petit fils
	 */
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

	/**
	 * Cette methode permet de recuperer la liste des petits fils du noeud node
	 * @param node le noeud
	 * @return 
	 */
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


}
