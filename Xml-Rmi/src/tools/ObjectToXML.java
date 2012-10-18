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
import java.util.ArrayList;

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
	 * 
	 * @param chaine
	 * @return
	 */
	public static Document stringToDoc(String chaine){
		//DocumentBuilder docBuilder = this.getDocBuilder();	
		DocumentBuilder docB = null;
		try {
			docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {

		}

		Document doc=null;
		try {
			doc = docB.parse(new InputSource(new StringReader(chaine)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
	}


	/**
	 * Prend en argument un nom de fichier et renvoie un objet Document
	 * @param nomDeFichier
	 * @return un objet Document correspondant à la structure du fichier XML
	 */
	public static Document fileToDoc(String nomDeFichier){
		DocumentBuilder docB = null;
		try {
			docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {

		}
		try {
			return docB.parse(new File(nomDeFichier));
		} catch(SAXException e) {

		} catch (IOException e) {

		}

		return null;
	}


	/**
	 * Ajoute dans l'appel client, param comme un fils de la balise params
	 * @param doc
	 * @param param
	 * @return
	 */
	public static Document mergeDocs(Document doc, Element param) {
		Node racine= doc.getElementsByTagName("params").item(0);
		Element racine2=  param;

		racine.appendChild(racine2);

		return doc;
	}


	/**
	 * A pour charge la partie des parametres non Object de l'appel client
	 * @param p
	 * @param doc
	 * @return
	 */
	public static Element getNodePrimitif(Object p,Document doc){

		Element param = doc.createElement("param");

		Element value = doc.createElement("value");
		param.appendChild(value);


		String type = p.getClass().getSimpleName();
		Element ty=null;
		if(type.equals("Integer")){
			ty = doc.createElement("int");
			ty.setTextContent(p.toString());
		}else if(type.equals("Double")){
			ty = doc.createElement("double");
			ty.setTextContent(p.toString());
		}

		value.appendChild(ty);



		return param;
	}

	/**
	 * Creer le document de l'appel Client ainsi que son entete a partir d'un nom de methode
	 * @param methode
	 * @return
	 */
	public static Document appelClient(String methode){
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


	// TODO attention aux types des attributs
	//Construction de l'appel client
	// GÔøΩnÔøΩre un doc appel client ÔøΩ partir d'un objet
	/**
	 * 
	 * @param oid
	 * @param obj
	 * @param methode_serveur
	 * @param methodes
	 * @return
	 * TODO rename cette methode passe un objet en xml
	 */
	public static Element appelClientToDocument(String oid,Object obj,ArrayList<String> methodes,Document doc){

		Element value = doc.createElement("value");

		Element object = doc.createElement("object");
		object.setAttribute("oid", oid);
		value.appendChild(object);


		Element fields =  doc.createElement("fields");
		object.appendChild(fields);


		for(int j =0; j<obj.getClass().getDeclaredFields().length;j++){

			Field fieldObj = obj.getClass().getDeclaredFields()[j];

			fieldObj.setAccessible(true);

			Annotation[] annotations=fieldObj.getDeclaredAnnotations();
			for(Annotation annotation : annotations){

				if(annotation instanceof XMLRMIField){
					// Balise Field
					XMLRMIField myAnnotation = (XMLRMIField) annotation;
					Element field = doc.createElement("field");
					field.setAttribute("name", myAnnotation.serializationName());

					fields.appendChild(field);

					Element valueField = doc.createElement("value");
					field.appendChild(valueField);

					Element type = doc.createElement(myAnnotation.serializationType());
					//type.setTextContent("Valeur a entrer");
					try {
						type.setTextContent(fieldObj.get(obj).toString());
					} catch (DOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					valueField.appendChild(type);

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
	 * @param fichierACopier
	 * @param destination
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
	 * Affiche ÔøΩ l'ÔøΩcran un document XML fourni sous forme d'un objet DOM
	 * Document.
	 * 
	 * @param doc le document
	 */
	public static void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		ObjectToXML.afficherElement(e);
	}

	/**
	 * Affiche ÔøΩ l'ÔøΩcran un ÔøΩlÔøΩment XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-ÔøΩlÔøΩments.
	 * 
	 * @param e l'ÔøΩlÔøΩment ÔøΩ afficher
	 */
	public static void afficherElement(Element e) {
		System.out.print("<" + e.getNodeName() + " ");

		NamedNodeMap attr = e.getAttributes();
		for(int i=0; i<attr.getLength(); i++) {
			Attr a = (Attr)attr.item(i);
			System.out.print(a.getName() + "=\"" + a.getNodeValue() + "\" ");
		}
		System.out.println(">");

		for(Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
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
	 * Crée un objet Object à partir d'un objet Document contenant les attribut et les méthodes\n
	 * necessaires à la construction de l'Object.
	 * @param doc
	 * @return
	 * @throws CannotCompileException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	//TODO A modifier
	public static Object createObject(Document doc) throws CannotCompileException, InstantiationException, IllegalAccessException{

		// on recupere le contenu de la balise method
		String corpsMethode= doc.getElementsByTagName("method").item(0).getTextContent();
		//String corpsMethode = "public String toString(){return \"r\";}";
		String x = doc.getElementsByTagName("double").item(0).getTextContent();
		String y = doc.getElementsByTagName("double").item(1).getTextContent();

		System.out.println(corpsMethode);

		CtClass point = ClassPool.getDefault().makeClass("Point"); //TODO A CHANGER 

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

	/**
	 * Recupere tout les oid de la reponse et les met dans une ArrayList<String>
	 * @param doc
	 * @return La liste des oid
	 */
	public static ArrayList<String> getOidFromXML(Document doc){
		ArrayList<String> loid= new ArrayList<String>();
		NodeList lobj = doc.getElementsByTagName("object");
		for(int i=0;i<lobj.getLength();i++){
			loid.add(lobj.item(i).getAttributes().item(0).getTextContent());
		}
		return loid;
	}


	public static Element getFieldsByOID(Document doc, String oid){
		Element e =null;
		NodeList lobj = doc.getElementsByTagName("object");
		for(int i=0;i<lobj.getLength();i++){
			if(lobj.item(i).getAttributes().item(0).getTextContent().equals(oid)){
				e = (Element) lobj.item(i).getFirstChild();
			}
		}
		return e;
	}



	/**
	 * Permet de mettre a jour un objet a partir d'un doc
	 * @param doc
	 * @param obj
	 */
	public static void updateObjectFromElement(Element el, Object obj){
		// pour chaque annotation correspondant au name de <field> dans le xml, il faut modifier la valeur de l'attribut sous l'annotation
		NodeList fields = el.getChildNodes();
		int nbField = fields.getLength();
		String name=""; // Contiendra la valeur de name pour chaque field traité

		//recupere la liste des attributs annotés
		// et les stocke dans fieldAnnote
		for(int j =0; j<obj.getClass().getDeclaredFields().length;j++){
			Field fieldObj = obj.getClass().getDeclaredFields()[j];
			fieldObj.setAccessible(true);
			Annotation[] annotations=fieldObj.getDeclaredAnnotations();

			for(Annotation annotation : annotations){
				if(annotation instanceof XMLRMIField){
					//on regarde parmi toutes les balise field, laquelle correspond a l'annotation de ce field
					//des qu'on l'a trouve on update le fiedl associe a cette annotation
					XMLRMIField myAnnotation = (XMLRMIField) annotation;

					for(int i=0;i<nbField;i++){ // attention pour chaque element de field, il faut le caster en Element
						name=fields.item(i).getAttributes().item(0).getTextContent();
						if(name.equals(myAnnotation.serializationName())){ // si l'annotation correspond a la bonne balise <field> du XML
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
	 * TODO update do + mettre une liste d'interface ou alors aller chercher directement le type des args de la methode appelée 
	 */
	public static Document createAppelClient(Class<?> inter,String methode,ArrayList<Object> params){
		Document doc = ObjectToXML.appelClient(methode);
		boolean trouve = false;
		for(int i=0;i<params.size();i++){  // verifier si le param implement l'interface XMLRMISerializable, dans ce cas c'est un type object
			for(int j = 0;j<params.get(i).getClass().getInterfaces().length;j++){
				if(params.get(i).getClass().getInterfaces()[j].equals(XMLRMISerializable.class)){ // ici c'est le cas de notre point
					Element paramObject = doc.createElement("param");
					Point p = new Point(1,2);
					Element obje=p.toXML(inter,doc);
					paramObject.appendChild(obje);
					ObjectToXML.mergeDocs(doc, paramObject);
					trouve=true;
				}
			}
			if(!trouve){
				Element p2 = ObjectToXML.getNodePrimitif(params.get(i), doc);
				ObjectToXML.mergeDocs(doc, p2);
			}
			trouve=false;
		}
		return doc;
	}

	public static Element objectWithoutAnnotationsToElement(String oid,
			Object obj, ArrayList<String> methodes, Document doc) {
		System.err.println("objectWithoutAnnotationToElem : oid = "+oid);
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
			Element val =  doc.createElement("bool");
			value.appendChild(val);
			val.setTextContent(obj.toString());
			return value;
		}  else if(obj instanceof String){
			Element val =  doc.createElement("string");
			value.appendChild(val);
			val.setTextContent(obj.toString());
			return value;
		} else if( ! (obj instanceof Object)){//TODO gerer les autres types
			System.err.println("objectWthoutAnnoatationToElement : pas un objet");
			return value;
		} else {



			Element object = doc.createElement("object");
			object.setAttribute("oid", oid);
			value.appendChild(object);

			// TODO: Systeme d'annotation pour catcher ce que l'on veut (what?)

			Element fields =  doc.createElement("fields");
			object.appendChild(fields);


			for(int j =0; j<obj.getClass().getDeclaredFields().length;j++){

				Field fieldObj = obj.getClass().getDeclaredFields()[j];

				fieldObj.setAccessible(true);


				// Balise Field
				Element field = doc.createElement("field");
				field.setAttribute("name", fieldObj.getName());

				fields.appendChild(field);

				Element valueField = doc.createElement("value");
				field.appendChild(valueField);

				System.out.println("test : " +fieldObj.getType().getSimpleName().toLowerCase());
				//TODO faire un switch sur le type
				Element type = doc.createElement(fieldObj.getType().getSimpleName().toLowerCase());
				//type.setTextContent("Valeur a entrer");
				try {
					type.setTextContent(fieldObj.get(obj).toString());
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				valueField.appendChild(type);



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


