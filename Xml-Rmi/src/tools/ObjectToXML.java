package tools;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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


import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import xmlrmi.XMLRMIField;

/**
 * 
 * @author marcgregoire
 * @author matthieudelgado
 * classe contenant des mŽthodes static permettant la gestion des fichiers XML
 */
public class ObjectToXML {


	/**
	 * Prend en argument un nom de fichier et renvoie un objet Document
	 * @param nomDeFichier
	 * @return un objet Document correspondant ˆ la structure du fichier XML
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


	// TODO attention aux types des attributs
	//Construction de l'appel client
	// Gï¿½nï¿½re un doc appel client ï¿½ partir d'un objet
	/**
	 * 
	 * @param oid
	 * @param obj
	 * @param methode_serveur
	 * @param methodes
	 * @return
	 */
	public static Document appelClientToDocument(String oid,Object obj,String methode_serveur,ArrayList<String> methodes){

		Document doc = ObjectToXML.creerDocument();
		Element racine=doc.createElement("methodCall");
		doc.appendChild(racine);

		Element methodeName = doc.createElement("methodeName");
		methodeName.setTextContent(methode_serveur);
		racine.appendChild(methodeName);

		Element params = doc.createElement("params");
		racine.appendChild(params);

		Element param = doc.createElement("param");
		params.appendChild(param);

		Element value = doc.createElement("value");
		param.appendChild(value);

		Element object = doc.createElement("object");
		object.setAttribute("oid", oid);
		value.appendChild(object);

		// TODO: Systeme d'annotation pour catcher ce que l'on veut (what?)

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
					fields.appendChild(valueField);

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

		return doc;
	}


	/**
	 * Permet la copie d'un fichier XML
	 * @param fichierACopier
	 * @param destination
	 */
	public static void copyPasteXml(String fichierACopier, String destination){
		ObjectToXML.docToFile(ObjectToXML.fileToDoc(fichierACopier), destination);
	}

	// cree un nouveau Document a partir du Builder
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
	 * CrŽe un fichier "nomDeFichier" ˆ partir de l'objet Document
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
			System.err.println("Impossible de crï¿½er un transformateur XML.");
			System.exit(1);
		}

		transfo.setOutputProperty(OutputKeys.METHOD, "xml");

		transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");

		transfo.setOutputProperty(OutputKeys.INDENT, "yes");

		try {
			transfo.transform(source, resultat);
		} catch(TransformerException e) {
			System.err.println("La transformation a ï¿½chouï¿½ : " + e);
			System.exit(1);
		}
	}


	/**
	 * Affiche ï¿½ l'ï¿½cran un document XML fourni sous forme d'un objet DOM
	 * Document.
	 * 
	 * @param doc le document
	 */
	public static void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		ObjectToXML.afficherElement(e);
	}

	/**
	 * Affiche ï¿½ l'ï¿½cran un ï¿½lï¿½ment XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-ï¿½lï¿½ments.
	 * 
	 * @param e l'ï¿½lï¿½ment ï¿½ afficher
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
	 * CrŽe un objet Object ˆ partir d'un objet Document contenant les attribut et les mŽthodes\n
	 * necessaires ˆ la construction de l'Object.
	 * @param doc
	 * @return
	 * @throws CannotCompileException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Object createObject(Document doc) throws CannotCompileException, InstantiationException, IllegalAccessException{

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

	/**
	 * 
	 * @param doc
	 * @param lo
	 */
	// a quoi sert cette methode?
	public static void updateFromXml(Document doc,ArrayList<Object> lo){
		String oid=doc.getElementsByTagName("object").item(0).getAttributes().getNamedItem("oid").getTextContent();
		System.out.println(oid);
		Object objToUpdate;

		// pour tout les objets, recupere la valeur de l'oid dans l'annotation

	}

	/**
	 * Permet de mettre a jour un objet a partir d'un element et de tout ses fils
	 * @param e
	 * @param obj
	 */
	public static void updateObjectFromElement(Element e, Object obj){
		// on part a partir de la balise value
	}


}


