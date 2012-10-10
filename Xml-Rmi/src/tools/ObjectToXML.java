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

public class ObjectToXML {

	DocumentBuilder docBuilder;
	Document docXml;


	public DocumentBuilder getDocBuilder() {
		return docBuilder;
	}

	public void setDocBuilder(DocumentBuilder docBuilder) {
		this.docBuilder = docBuilder;
	}

	public Document getDocXml() {
		return docXml;
	}


	public void setDocXml(Document docXml) {
		this.docXml = docXml;
	}

	public ObjectToXML() {
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {

		}
		this.setDocBuilder(docBuilder);
	}

	// faire une methode qui cr�e un doc a partir d'un parsage d'un xml model

	public Document fileToDoc(String nomDeFichier){
		try {
			return this.docBuilder.parse(new File(nomDeFichier));
		} catch(SAXException e) {

		} catch (IOException e) {

		}

		return null;
	}

	// en cours de test
	public void objectToXML(Object objet,Document doc){
		doc.getElementsByTagName("methodName").item(0).setTextContent("nom de la methode");
		Element e =(Element) doc.getElementsByTagName("field").item(0);
		e.setAttribute("name", objet.getClass().getName());

	}

	//Construction de l'appel client
	// G�n�re un doc appel client � partir d'un objet
	public Document appelClientToDocument(Object obj,String methode_serveur,ArrayList<String> methodes){

		Document doc = this.creerDocument();
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
		object.setAttribute("oid", "ici oid");
		value.appendChild(object);

		// TODO: Systeme d'annotation pour catcher ce que l'on veut

		Element fields =  doc.createElement("fields");
		object.appendChild(fields);

		// ATTENTION ERREUR : il faut regarder l'annotation de chaque field
		// L'objet appel cette méthode en lui mettant en parametre ses valeurs annotees
		// pour chaque field de l'objet

		for(int j =0; j<obj.getClass().getDeclaredFields().length;j++){

			Field fieldObj = obj.getClass().getDeclaredFields()[j];

			fieldObj.setAccessible(true);

			Annotation[] annotations=fieldObj.getDeclaredAnnotations();
			System.out.println("Nb d'annotations : "+annotations.length);
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

		//Methodes
		// avec javassist
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


	public void copyPasteXml(String fichierACopier, String destination){
		this.docToFile(this.fileToDoc(fichierACopier), destination);
	}

	// cree un nouveau Document a partir du Builder
	public Document creerDocument(){
		DocumentBuilder docBuilder = this.getDocBuilder();	
		Document doc = docBuilder.newDocument();
		return doc;
	}


	// pour tester la construction du XML
	// A effacer plus tard
	public Document creerDocument2(){
		DocumentBuilder docBuilder = this.getDocBuilder();

		Document doc = docBuilder.newDocument();

		Element racine = doc.createElement("methodCall");
		doc.appendChild(racine);

		Element methodeName = doc.createElement("methodName");
		methodeName.setTextContent("display"); // a remplacer par le nom de la m�thode � appeler
		racine.appendChild(methodeName);

		Element params = doc.createElement("params");
		racine.appendChild(params);

		return doc;

	}

	public void docToFile(Document doc, String nomDeFichier){

		Source source = new DOMSource(doc);


		Result resultat = new StreamResult(new File(nomDeFichier));


		Transformer transfo = null;
		try {
			transfo = TransformerFactory.newInstance().newTransformer();
		} catch(TransformerConfigurationException e) {
			System.err.println("Impossible de cr�er un transformateur XML.");
			System.exit(1);
		}

		transfo.setOutputProperty(OutputKeys.METHOD, "xml");

		transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");

		transfo.setOutputProperty(OutputKeys.INDENT, "yes");

		try {
			transfo.transform(source, resultat);
		} catch(TransformerException e) {
			System.err.println("La transformation a �chou� : " + e);
			System.exit(1);
		}
	}


	// A ENLEVER

	/**
	 * Affiche � l'�cran un document XML fourni sous forme d'un objet DOM
	 * Document.
	 * 
	 * @param doc le document
	 */
	public void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		afficherElement(e);
	}

	/**
	 * Affiche � l'�cran un �l�ment XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-�l�ments.
	 * 
	 * @param e l'�l�ment � afficher
	 */
	public void afficherElement(Element e) {
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


}


