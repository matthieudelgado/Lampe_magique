package tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import objets.Point;
import objets.Stringable;
import objets.XMLRMISerializable;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import xmlrmi.XMLRMIField;

/**
 * 
 * @author marcgregoire
 * @author matthieudelgado
 * classe contenant des méthodes static permettant la gestion des fichiers XML
 */
public class ObjectToXML {

	/**
	 * Cette methode permet de recuperer un fichier xml sous forme de string
	 * @param nomFichier le chemin du fichier xml
	 * @return le fichier xml sous forme de string
	 */
	public static String fileToString(String nomFichier){
		String chaine="";
		try{
			InputStream ips=new FileInputStream(nomFichier); 
			InputStreamReader ipsr=new InputStreamReader(ips);
			BufferedReader br=new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null){
				chaine+=ligne+"\n";
			}
			br.close(); 
		}		
		catch (Exception e){
			System.out.println(e.toString());
		}
		return chaine.replaceAll(">\\s*<", "><"); 
	}

	/**
	 * transforme un string en document xml
	 * @param chaine la chaine correspondant au document
	 * @return un document xml
	 */
	public static Document stringToDoc(String chaine){
		DocumentBuilder docB = null;
		Document doc = null;
		try {
			docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = docB.parse(new InputSource(new StringReader(chaine)));
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * Prend en argument un nom de fichier et renvoie un objet Document
	 * @param nomDeFichier le chemin du fichier xml
	 * @return un objet Document correspondant à la structure du fichier XML
	 */
	public static Document fileToDoc(String nomDeFichier){
		DocumentBuilder docB = null;
		try {
			docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			return docB.parse(new File(nomDeFichier));
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
		} catch(SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Ajoute dans le document doc param comme un fils de la balise params
	 * @param doc le document
	 * @param param l'element a ajouter
	 * @return doc
	 */
	public static Document mergeDocs(Document doc, Element param) {
		Node racine= doc.getElementsByTagName("params").item(0);
		Element racine2=  param;
		racine.appendChild(racine2);
		return doc;
	}


	/**
	 * Cette methode creer un element correspondant à l'objet p et le wrap dans un element param.( p ne doit correspondre à un type primitif ou à un tableau.) Et
	 * l'ajoute comme paramètre du document doc. Si p est un tableau d'objet, l'element num_itf de la liste interface.
	 * @param p l'objet
	 * @param doc le document
	 * @param num_itf le numero d'interface dans la liste
	 * @param inters la liste d'interface
	 * @return
	 */
	public static Element getNodePrimitif(Object p,Document doc, ArrayList<Class<?>> inters, Integer num_itf)
	{
		Element param = doc.createElement("param");
		Element value =getContenuNodePrimitif(p, doc, inters, num_itf);
		param.appendChild(value);
		return param;
	}

	/**
	 * Cette methode creer un element correspondant à l'objet p.( p ne doit correspondre à un type primitif ou à un tableau.) Et
	 * l'ajoute comme paramètre du document doc. Si p est un tableau d'objet, l'element num_itf de la liste interface.
	 * @param p l'objet
	 * @param doc le document
	 * @param num_itf le numero d'interface dans la liste
	 * @param inters la liste d'interface
	 * @return
	 */
	public static Element getContenuNodePrimitif(Object p,Document doc, ArrayList<Class<?>> inters, Integer num_itf)
	{
		Element value = doc.createElement("value");

		String type = p.getClass().getSimpleName();
		Element ty=null;
		if(type.equals("Integer"))
		{
			ty = doc.createElement("int");
			ty.setTextContent(p.toString());
		}
		else if(type.equals("Double"))
		{
			ty = doc.createElement("double");
			ty.setTextContent(p.toString());
		}
		else if(type.equals("Boolean"))
		{
			ty = doc.createElement("boolean");
			if(p.toString().equals("true"))
				ty.setTextContent("1");
			else ty.setTextContent("0");
		} 
		else if(type.equals("String"))
		{
			ty = doc.createElement("string");
			ty.setTextContent(p.toString());
		}
		else if(type.equals("Date"))
		{
			ty = doc.createElement("datetime");
			ty.setTextContent(dateToDateTime((Date)p));
		} 
		else if(p.getClass().isArray())
		{
			if(XMLRMISerializable.class.isAssignableFrom(p.getClass().getComponentType())){
				XMLRMISerializable[] tab = (XMLRMISerializable[])p;
				ty = doc.createElement("array");
				for(XMLRMISerializable o : tab)
				{
					Element e = (Element) createElementObject(o, inters.get(num_itf), doc);
					num_itf++;
					ty.appendChild(e);
				}
			} 
			else 
			{
				Object[] tab = (Object[]) p;
				ty = doc.createElement("array");
				Element val;
				Element e ;
				for(Object o : tab)
				{
					val = doc.createElement("value");
					e = (Element)getContenuNodePrimitif(o, doc, inters, num_itf).getFirstChild();
					val.appendChild(e);
					ty.appendChild(val);
				}
			}
		}
		else 
		{
			System.err.println("type non pris en charge de getContenuNodePrimitif");
		}

		value.appendChild(ty);
		return value;
	}

	/**
	 * Cette methode transforme une Date en string dans le format dateTime 
	 * @param d la date
	 * @return la date sous forme de dateTime
	 */
	public static String dateToDateTime(Date d)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String dateString = df.format(d);
		return dateString;
	}


	/**
	 * Creer le document de l'appel Client ainsi que son entete a partir d'un nom de methode
	 * @param methode le nom de methode
	 * @return le document
	 */
	public static Document appelClient(String methode)
	{
		Document doc = ObjectToXML.creerDocument();
		Element racine=doc.createElement("methodCall");
		doc.appendChild(racine);
		Element methodeName = doc.createElement("methodeName");
		methodeName.setTextContent(methode);
		racine.appendChild(methodeName);
		Element params = doc.createElement("params");
		racine.appendChild(params);
		return doc;
	}

	/**
	 * Cette methode transforme un objet en element.
	 * @param oid l'oid de l'objet
	 * @param itf l'interface de l'objet
	 * @param itfArray 
	 * @param obj l'objet à transformer
	 * @param methodes les methodes a ajouter dans l'objet
	 * @param doc le document 
	 * @return
	 */
	public static Element objectToElement(String oid,Class<?> itf,Class<?> itfArray, Object obj,ArrayList<String> methodes,Document doc)
	{
		Element value = doc.createElement("value");
		Element object = doc.createElement("object");
		object.setAttribute("oid", oid);
		object.setAttribute("type", itf.getName());
		value.appendChild(object);
		Element fields =  doc.createElement("fields");
		object.appendChild(fields);
		for(int j =0; j<obj.getClass().getDeclaredFields().length;j++){
			Field fieldObj = obj.getClass().getDeclaredFields()[j];
			fieldObj.setAccessible(true);
			Annotation[] annotations=fieldObj.getDeclaredAnnotations();
			for(Annotation annotation : annotations){
				if(annotation instanceof XMLRMIField){
					XMLRMIField myAnnotation = (XMLRMIField) annotation;
					Element field = doc.createElement("field");
					field.setAttribute("name", myAnnotation.serializationName());
					fields.appendChild(field);
					Element valueField = null;
					if(XMLRMISerializable.class.isAssignableFrom(fieldObj.getType()))
					{
						try 
						{
							valueField = ((XMLRMISerializable)fieldObj.get(obj)).
									toXML(Class.forName(myAnnotation.serializationType()), doc);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} 
					else if (myAnnotation.serializationType().equals("array"))	
					{
						ArrayList <Class<?>> liter = new ArrayList<Class<?>>();
						liter.add(itfArray);
						try {
							valueField=ObjectToXML.getContenuNodePrimitif(fieldObj.get(obj), doc, liter, 0);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					else
					{
						valueField = doc.createElement("value");
						Element type = doc.createElement(myAnnotation.serializationType());
						try {
							type.setTextContent(fieldObj.get(obj).toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
						valueField.appendChild(type);
					}
					field.appendChild(valueField);

				}
			}
			fieldObj.setAccessible(false);
		}
		Element methods = doc.createElement("methods");
		object.appendChild(methods);


		for(int i = 0;i<methodes.size();i++){
			Element method = doc.createElement("method");
			method.setAttribute("language", "Java");
			method.setTextContent(methodes.get(i));
			methods.appendChild(method);
		}

		return value;
	}


	/**
	 * Permet la copie d'un fichier XML
	 * @param fichierACopier le fichier
	 * @param destination la destination de la copie
	 */
	public static void copyPasteXml(String fichierACopier, String destination){
		ObjectToXML.docToFile(ObjectToXML.fileToDoc(fichierACopier), destination);
	}


	/**
	 * Permet de creer un document
	 * @return Document
	 */
	public static Document creerDocument(){
		//DocumentBuilder docBuilder = this.getDocBuilder();	
		DocumentBuilder docB = null;
		try {
			docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {

		}

		Document doc = docB.newDocument();
		return doc;
	}

	/**
	 * Crée un fichier "nomDeFichier" à partir de l'objet Document
	 * @param doc
	 * @param nomDeFichier
	 */
	public static void docToFile(Document doc, String nomDeFichier){
		Source source = new DOMSource(doc);
		Result resultat = new StreamResult(new File(nomDeFichier));
		Transformer transfo = null;
		try {
			transfo = TransformerFactory.newInstance().newTransformer();
		} catch(TransformerConfigurationException e) {
			System.err.println("Impossible de creer un transformateur XML.");
			System.exit(1);
		}
		transfo.setOutputProperty(OutputKeys.METHOD, "xml");
		transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transfo.setOutputProperty(OutputKeys.INDENT, "yes");
		try {
			transfo.transform(source, resultat);
		} catch(TransformerException e) {
			System.err.println("La transformation a echoue : " + e);
			System.exit(1);
		}
	}


	/**
	 * Affiche à l'ecran un document XML fourni sous forme d'un objet DOM
	 * Document.
	 * @param doc le document
	 */
	public static void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		ObjectToXML.afficherElement(e);
	}
	
	/**
	 * transforme un element en String
	 * @param e
	 * @return
	 */
	private static String docToString(Element e)
	{
		String s = "";
		s+="<" + e.getNodeName() + " ";
		NamedNodeMap attr = e.getAttributes();
		for(int i=0; i<attr.getLength(); i++) 
		{
			Attr a = (Attr)attr.item(i);
			s+= a.getName() + "=\"" + a.getNodeValue() + "\" ";
		}
		s+=">";

		for(Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) 
		{
			switch(n.getNodeType()) {
			case Node.ELEMENT_NODE:
				s+=docToString((Element)n);
				break;
			case Node.TEXT_NODE:
				String data = ((Text)n).getData();
				s+=data;
				break;
			}
		}
		return s+"</" + e.getNodeName() + ">";
	}
	
	
	/**
	 * transforme un document en String
	 * @param doc
	 * @return
	 */
	public static String docToString(Document doc)
	{
		Element e = doc.getDocumentElement();
		return docToString(e);
	}
		

	/**
	 * Affiche à l'ecran un document XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-elements.
	 * @param e l'element à afficher
	 */
	public static void afficherElement(Element e) {
		System.out.print("<" + e.getNodeName() + " ");
		NamedNodeMap attr = e.getAttributes();
		for(int i=0; i<attr.getLength(); i++) 
		{
			Attr a = (Attr)attr.item(i);
			System.out.print(a.getName() + "=\"" + a.getNodeValue() + "\" ");
		}
		System.out.println(">");

		for(Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) 
		{
			switch(n.getNodeType()) {
			case Node.ELEMENT_NODE:
				afficherElement((Element)n);
				break;
			case Node.TEXT_NODE:
				String data = ((Text)n).getData();
				System.out.print(data);
				break;
			}
		}
		System.out.println("</" + e.getNodeName() + ">");
	}


	/**
	 * Recupere tout les oid de la reponse et les met dans une ArrayList<String>
	 * @param doc 
	 * @return La liste des oid
	 */
	public static ArrayList<String> getOidFromXML(Document doc){
		ArrayList<String> loid= new ArrayList<String>();
		NodeList lobj = doc.getElementsByTagName("object");
		System.out.println("nombre d'objet "+lobj.getLength());
		for(int i=0;i<lobj.getLength();i++){
			loid.add(lobj.item(i).getAttributes().item(0).getTextContent());
		}
		return loid;
	}

	/**
	 * Recupere dans le document doc l'objet d'oid oid
	 * @param doc le document
	 * @param oid l'oid
	 * @return l'element de l'objet
	 */
	public static Element getFieldsByOID(Document doc, String oid)
	{
		Element e =null;
		NodeList lobj = doc.getElementsByTagName("object");
		for(int i=0;i<lobj.getLength();i++)
		{
			if(lobj.item(i).getAttributes().item(0).getTextContent().equals(oid))
				e = (Element) lobj.item(i).getFirstChild();
		}
		return e;
	}



	/**
	 * Permet de mettre a jour un objet a partir d'un element
	 * @param el l'element 
	 * @param obj l'objet
	 */
	public static void updateObjectFromElement(Element el, Object obj)
	{
		// pour chaque annotation correspondant au name de <field> dans le xml, 
		//il faut modifier la valeur de l'attribut sous l'annotation
		NodeList fields = el.getChildNodes();
		int nbField = fields.getLength();
		String name=""; // Contiendra la valeur de name pour chaque field traité

		//recupere la liste des attributs annotés
		// et les stocke dans fieldAnnote
		for(int j =0; j<obj.getClass().getDeclaredFields().length;j++)
		{
			Field fieldObj = obj.getClass().getDeclaredFields()[j];
			fieldObj.setAccessible(true);
			Annotation[] annotations=fieldObj.getDeclaredAnnotations();

			for(Annotation annotation : annotations)
			{
				if(annotation instanceof XMLRMIField)
				{
					//on regarde parmi toutes les balise field, laquelle correspond a l'annotation de ce field
					//des qu'on l'a trouve on update le fiedl associe a cette annotation
					XMLRMIField myAnnotation = (XMLRMIField) annotation;

					for(int i=0;i<nbField;i++)// attention pour chaque element de field, il faut le caster en Element
					{ 
						name=fields.item(i).getAttributes().item(0).getTextContent();
						if(name.equals(myAnnotation.serializationName()))// si l'annotation correspond a la bonne balise <field> du XML
						{ 
							//alors il faut update le champ correspond a l'annotation en l'occurence fieldObj
							String type =fields.item(i).getFirstChild().getFirstChild().getNodeName();
							ObjectToXML.updateFieldByType(type, obj, fieldObj, fields.item(i).getFirstChild().getFirstChild());
						}
					}
				}
			}
			fieldObj.setAccessible(false);
		}
	}


	/**
	 * Permet de mettre a jour un champs en fonction de son type
	 * @param type
	 * @param obj
	 * @param fieldObj
	 * @param valueElement
	 */
	public static void updateFieldByType(String type,Object obj,Field fieldObj, Node valueElement){
		try{
			if(type.equals("double")){
				double value=Double.parseDouble(valueElement.getTextContent());
				fieldObj.setDouble(obj, value);
			}else if(type.equals("int")){
				int value=Integer.parseInt(valueElement.getTextContent());
				fieldObj.setInt(obj, value);
			}else if(type.equals("char")){
				//TODO
			}else if(type.equals("boolean")){
				boolean value = Boolean.parseBoolean(valueElement.getTextContent());
				fieldObj.setBoolean(obj, value);
			}else if(type.equals("datetime")){
				//TODO
			}else if(type.equals("base64")){
				//TODO
			}else if(type.equals("string")){
				String value = valueElement.getTextContent();
				fieldObj.set(obj, value);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cree un Document representant un appel client a partir d'un nom de methode et d'une liste\n
	 * d'arguments
	 * @param methode
	 * @param params
	 * @return Document
	 */
	public static Document createAppelClient(ArrayList<Class<?>> inters,String methode,ArrayList<Object> params){
		Integer num_itf = 0;
		Document doc = ObjectToXML.appelClient(methode);
		boolean trouve = false;
		for(int i=0;i<params.size();i++){  // verifier si le param implement l'interface XMLRMISerializable, dans ce cas c'est un type object
			for(int j = 0;j<params.get(i).getClass().getInterfaces().length;j++){
				if(params.get(i).getClass().getInterfaces()[j].equals(XMLRMISerializable.class)){ // ici c'est le cas de notre point
					//System.err.println("ittf : "+inters.get(num_itf).getSimpleName());
					Element paramObject = createElementParamObject(params.get(i), inters.get(num_itf), doc);
					num_itf++;
					ObjectToXML.mergeDocs(doc, paramObject);
					trouve=true;
				}
			}
			if(!trouve){
				Element p2 = ObjectToXML.getNodePrimitif(params.get(i), doc, inters, num_itf);
				ObjectToXML.mergeDocs(doc, p2);
			}
			trouve=false;
		}
		return doc;
	}

	/**
	 * Creer un element parametre pour l'objet objet
	 * @param object l'objet
	 * @param itf l'interface de l'objet
	 * @param doc le document
	 * @return l'element
	 */
	private static Element createElementParamObject(Object object,
			Class<?> itf, Document doc) {
		Element paramObject = doc.createElement("param");

		paramObject.appendChild(createElementObject(object, itf, doc));
		return paramObject;
	}

	/**
	 * Creer un element objet pour l'objet objet
	 * @param object l'objet
	 * @param itf l'interface de l'objet
	 * @param doc le document
	 * @return
	 */
	private static Element createElementObject(Object object,
			Class<?> itf, Document doc) {
		XMLRMISerializable p = (XMLRMISerializable)object;
		Element obje=p.toXML(itf, doc);

		return obje;
	}

	/**
	 * Cette methode prend un objet et retourne l'element objet corrspondant
	 * @param oid l'oid de l'objet
	 * @param itf l'interface
	 * @param obj l'objet
	 * @param methodes les methodes de l'objet
	 * @param doc le document
	 * @return l'element.
	 */
	public static Element objectWithoutAnnotationsToElement(String oid, Class<?> itf,
			Object obj, ArrayList<String> methodes, Document doc) throws IllegalArgumentException, IllegalAccessException {
		Element value = doc.createElement("value");
		//faire un switch sur le type de obj
		if( obj == null){
			value.setTextContent("void");
			return value;
		} else if(obj instanceof Integer){
			Element val =  doc.createElement("int");
			value.appendChild(val);
			val.setTextContent(obj.toString());
			return value;
		} else if(obj instanceof Double){
			Element val =  doc.createElement("double");
			value.appendChild(val);
			val.setTextContent(obj.toString());
			return value;
		} else if(obj instanceof Boolean){
			Element val =  doc.createElement("boolean");
			value.appendChild(val);
			if(obj.toString().equals("false")){
				val.setTextContent("0");
			} else val.setTextContent("1");
			return value;
		}  else if(obj instanceof String){
			Element val =  doc.createElement("string");
			value.appendChild(val);
			val.setTextContent(obj.toString());
			return value;
		}else if(obj instanceof Date){
			Element val = doc.createElement("datetime");
			val.setTextContent(dateToDateTime((Date)obj));
			value.appendChild(val);
			return value;
		} else if(obj.getClass().isArray()){
			Element val =  doc.createElement("array");
			value.appendChild(val);

			if(obj.getClass().getComponentType().equals(int.class)){
				int[] tab = (int[])obj;
				for(int o : tab){
					Element e = objectWithoutAnnotationsToElement("int", int.class, o, new ArrayList<String>(), doc);
					val.appendChild(e);
				}
				return value;
			} else if(obj.getClass().getComponentType().equals(double.class)){
				double[] tab = (double[])obj;
				for(double o : tab){
					Element e = objectWithoutAnnotationsToElement("double", double.class, o, new ArrayList<String>(), doc);
					val.appendChild(e);
				}
				return value;
			} else if(obj.getClass().getComponentType().equals(boolean.class)){
				boolean[] tab = (boolean[])obj;
				for(Object o : tab){
					Element e = objectWithoutAnnotationsToElement("boolean", boolean.class, o, new ArrayList<String>(), doc);
					val.appendChild(e);
				}
				return value;
			} 
			Object[] tab = (Object[])obj;
			for(Object o : tab){
				Element e = objectWithoutAnnotationsToElement(o.getClass().getSimpleName(),itf.getComponentType(), o, new ArrayList<String>(), doc);
				val.appendChild(e);
			}
			return value;
		} else if( ! (obj instanceof Object)){
			System.err.println("objectWthoutAnnoatationToElement : incohérence");
			return value;
		} else {
			Element object = doc.createElement("object");
			object.setAttribute("oid", oid);
			object.setAttribute("type", itf.getName());
			value.appendChild(object);
			Element fields =  doc.createElement("fields");
			object.appendChild(fields);
			for(int j =0; j<obj.getClass().getDeclaredFields().length;j++){
				Field fieldObj = obj.getClass().getDeclaredFields()[j];
				fieldObj.setAccessible(true);
				// Balise Field
				Element field = doc.createElement("field");
				field.setAttribute("name", fieldObj.getName());
				fields.appendChild(field);
				Element valueField = null; 
				Element type = null;
				if(fieldObj.getType().isInterface())
				{
					//objectWithoutAnnotationsToElement(String oid, Class<?> itf,
					//	Object obj, ArrayList<String> methodes, Document doc) 
					try {
						Object recObj = fieldObj.get(obj);
						valueField = objectWithoutAnnotationsToElement(recObj.getClass().getSimpleName(),
								fieldObj.getType(), recObj, methodes, doc);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} 
				else if(fieldObj.getType().isArray())
				{
					Object[] recObj = (Object[]) fieldObj.get(obj);
					valueField = doc.createElement("value");
					Element arrayField = doc.createElement("array");
					for(int i =0; i<recObj.length;i++){
						Element valueArray  = objectWithoutAnnotationsToElement(recObj[i].getClass().getSimpleName(), fieldObj.getType().getComponentType(), recObj[i], methodes, doc);
						arrayField.appendChild(valueArray);
					}
					valueField.appendChild(arrayField);
				}
				else
				{
					valueField = doc.createElement("value");
					type = doc.createElement(fieldObj.getType().getSimpleName().toLowerCase());
					valueField.appendChild(type);
					try {
						type.setTextContent(fieldObj.get(obj).toString());
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				field.appendChild(valueField);
				fieldObj.setAccessible(false);
			}

			Element methods = doc.createElement("methods");
			object.appendChild(methods);
			for(int i = 0;i<methodes.size();i++){
				Element method = doc.createElement("method");
				method.setAttribute("language", "Java");
				method.setTextContent(methodes.get(i));
				methods.appendChild(method);
			}

			return value;
		}
	}

}


