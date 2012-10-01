package tools;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class TestEcritureXML {


	private static Document creerDocumentExemple(DocumentBuilder docBuilder) {
		Document doc = docBuilder.newDocument();

		Element racine = doc.createElement("root");
		racine.setAttribute("lang", "fr");
		doc.appendChild(racine);

		Element sujet = doc.createElement("node");
		sujet.setAttribute("role", "sujet");
		sujet.setTextContent("Sup�lec");
		racine.appendChild(sujet);

		Element verbe = doc.createElement("node");
		verbe.setAttribute("role", "verbe");
		verbe.setTextContent("est");
		racine.appendChild(verbe);

		Element complement = doc.createElement("node");
		complement.setAttribute("role", "compl�ment de lieu");
		complement.setTextContent("en France");
		racine.appendChild(complement);

		return doc;
	}

	/**
	 * �crit dans un fichier un document DOM, �tant donn� un nom de fichier.
	 * 
	 * @param doc le document � �crire
	 * @param nomFichier le nom du fichier de sortie
	 */
	private static void ecrireDocument(Document doc, String nomFichier) {
		// on consid�re le document "doc" comme �tant la source d'une 
		// transformation XML
		Source source = new DOMSource(doc);

		// le r�sultat de cette transformation sera un flux d'�criture dans
		// un fichier
		Result resultat = new StreamResult(new File(nomFichier));

		// cr�ation du transformateur XML
		Transformer transfo = null;
		try {
			transfo = TransformerFactory.newInstance().newTransformer();
		} catch(TransformerConfigurationException e) {
			System.err.println("Impossible de cr�er un transformateur XML.");
			System.exit(1);
		}

		// configuration du transformateur

		// sortie en XML
		transfo.setOutputProperty(OutputKeys.METHOD, "xml");

		// inclut une d�claration XML (recommand�)
		transfo.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		// codage des caract�res : UTF-8. Ce pourrait �tre �galement ISO-8859-1
		transfo.setOutputProperty(OutputKeys.ENCODING, "utf-8");

		// idente le fichier XML
		transfo.setOutputProperty(OutputKeys.INDENT, "yes");

		try {
			transfo.transform(source, resultat);
		} catch(TransformerException e) {
			System.err.println("La transformation a �chou� : " + e);
			System.exit(1);
		}
	}

	/**
	 * Lit un document XML � partir d'un fichier sur le disque, et construit
	 * le document DOM correspondant.
	 * 
	 * @param docBuilder une instance de DocumentBuilder
	 * @param nomFichier le fichier o� est stock� le document XML
	 * @return un objet DOM Document correspondant au document XML 
	 */
	public static Document lireDocument(DocumentBuilder docBuilder, 
			String nomFichier) {

		try {
			return docBuilder.parse(new File(nomFichier));
		} catch(SAXException e) {
			System.err.println("Erreur de parsing de " + nomFichier);
		} catch (IOException e) {
			System.err.println("Erreur d'entr�e/sortie sur " + nomFichier);
		}

		return null;
	}

	/**
	 * Affiche � l'�cran un document XML fourni sous forme d'un objet DOM
	 * Document.
	 * 
	 * @param doc le document
	 */
	public static void afficherDocument(Document doc) {
		Element e = doc.getDocumentElement();
		afficherElement(e);
	}

	/**
	 * Affiche � l'�cran un �l�ment XML, ainsi que ses attributs, ses noeuds
	 * de texte, et ses sous-�l�ments.
	 * 
	 * @param e l'�l�ment � afficher
	 */
	private static void afficherElement(Element e) {
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
	 * @param args
	 */
	public static void main(String[] args) {

		DocumentBuilder docBuilder = null;

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch(ParserConfigurationException e) {
			System.err.println("Impossible de cr�er un DocumentBuilder.");
			System.exit(1);
		}

		// cr�e un petit document d'exemple
		Document doc = creerDocumentExemple(docBuilder);

		// l'�crire sur le disque dans un fichier
		ecrireDocument(doc, "test.xml");
//		// re-charger ce document � partir du fichier
//		Document doc2 = lireDocument(docBuilder, "test.xml");
//		if(doc2 == null) System.exit(1);
//
//		afficherDocument(doc2);


	}

}
