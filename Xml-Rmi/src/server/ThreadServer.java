package server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import objets.Stringable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tools.ObjectToXML;
import tools.TestEcritureXML;
import tools.XMLToObject;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 *
 * Cette classe est le thread qui traite les connections au serveur.
 */
public class ThreadServer extends Thread implements IServer{
	private Socket socket;
	private XMLOutputStream out;
	private XMLInputStream in;

	public ThreadServer(Socket socket) {
		this.socket = socket;
		try {
			this.out = new XMLOutputStream(socket.getOutputStream());
			this.in = new XMLInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		System.out.println("Un client s'est connecte");
		try { 
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
			Document doc = null;

			while(true) 
			{
				in.recive();
				doc = docBuilder.parse(in);
				doTreatement(doc);
			}
		}
		catch (Exception e){ e.printStackTrace();}
		finally 
		{
			try
			{
				System.out.println("Un client s'est deconnecte");
				socket.close(); 
			}
			catch (IOException e){ }
		}
	}
	
	/**
	 * Methode de traitement de le requete du client 
	 * @param doc le document xml envoyé par le client
	 * @throws Exception
	 */
	private void doTreatement(Document doc) throws Exception {
		TestEcritureXML.afficherDocument(doc);
		//TODO on commence par verifier l'integrite du doc par rapport a la grammaire

		//on recherche la m√©thode appel√©e
		String methodeName = getMethodName(doc);

		//on recup√®re les parametres de la methode dans le xml
		ArrayList<Node> paramList = getParameters(doc);

		//Dans paramList ici on a les parametre de la methode display

		//on verfie qu'elle existe dans l'inteface IServer et
		//que les parametre de paramList correspondent
		ArrayList<Object> args = new ArrayList<Object>();
		Method calledMethod = findCalledMethodInServerInterface(methodeName, args, paramList);

		Object ret;
		if(calledMethod == null){//si on a pas trouvé la methode, message d'erreur
			ret = "methode introuvable";
		} else {//sinon on l'applique
			ret = calledMethod.invoke(this, args.toArray());
		}
		
		//on envoi le resultat au client
		sendResult(ret, args);

	}
	
	/**
	 * Cette methode permet d'obtenir le nom de la methode du serveur appelee par le client
	 * @param doc le document xml envoyé par le client
	 * @return le nom de la methode
	 */
	private String getMethodName(Document doc){
		Node n = doc.getElementsByTagName("methodeName").item(0).getFirstChild();
		System.out.println("nom de methode "+n.getTextContent());
		return n.getTextContent();
	}
	
	/**
	 * Cette methode permet d'obtenir la liste des Node du document xml envoyé
	 * par le client, qui contient les parametres de la methode appelee
	 * @param doc le document xml envoyé par le client
	 * @return la liste des Node correspondant aux parametre de la methode
	 */
	private ArrayList<Node> getParameters(Document doc){
		ArrayList<Node> paramList = new ArrayList<Node>();
		NodeList params = doc.getElementsByTagName("value"), childs;
		Node current = null;
		for(int i = 0; i < params.getLength() ; i++) {
			current = params.item(i);
			if(current.getParentNode().getNodeName().equalsIgnoreCase("param")){
				childs = current.getChildNodes();
				for(int j = 0; j< childs.getLength();j++){
					if(childs.item(j).getNodeType() != 3){
						paramList.add(childs.item(j));
						break;
					}
				}
			}
		}
		return paramList;
	}
	
	/**
	 * Cette methode recherche la methode appelee dans l'interface du serveur.
	 * Pour cela elle se sert du nom de la methode ainsi que de ces parametres.
	 * De plus, la methode reconstruit les objets parametre d'apres la liste de Node paramList, 
	 * et les ajoute à la liste args.
	 * @param methodeName le nom de la methode appelee
	 * @param args la liste des arguments 
	 * @param paramList la liste des noeuds correspondants aux parametres dans le xml du client
	 * @return la methode appelee si elle existe, null sinon
	 * @throws Exception
	 */
	private Method findCalledMethodInServerInterface(String methodeName, ArrayList<Object> args, ArrayList<Node> paramList) throws Exception{
		Class<IServer> itf = IServer.class;
		Method[] methods = itf.getDeclaredMethods();
		Method calledMethod = null;
		Class<?>[] parameterTypes = null;
		boolean trouve = false;
		for(Method m : methods){
			//on commence par verifier si le nom correspond
			if( !m.getName().equalsIgnoreCase(methodeName)) continue;
			//on verifie ensuite les arguments
			parameterTypes = m.getParameterTypes();
			//on compare le type des parametres avec le xml
			for(int i = 0 ; i < parameterTypes.length; i++){
				System.err.println(parameterTypes[i].getSimpleName());
				Object o = XMLToObject.createObjectFromNode(paramList.get(i), parameterTypes[i]);
				System.err.println("affichage de o : "+o);
				//si le parametre attendu est une interface, il faut tester
				//que l'objet implemente les methode de l'interface
				if(parameterTypes[i].isInterface()){
					if(implement(o, parameterTypes[i])){//on regarde si o implemente l'itf
						//o = addInterface(o, parameterTypes[i]);//on ajoute l'itf a la classe de o
						args.add(o);
						trouve = true;
					} else {
						trouve = false;
						break;
					}
				} else if( ! parameterTypes[i].isInstance(o) ){
					trouve = false;
					break;
				} else {
					args.add(o);
				}
			}
			if(trouve == false){
				args.clear();
				continue;
			} else{
				System.err.println("bla");
				calledMethod = m;
				break;
			}

		}
		return calledMethod;
	}


	/**
	 * Cette methode envoi la reponse du serveur au client.
	 * @param ret la valeur de retour de la methode
	 * @param args les arguments de la methode dans leur nouvel etat
	 * @throws Exception
	 */
	private void sendResult(Object ret, ArrayList<Object> args) throws Exception {
		DocumentBuilder docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document reponse = docB.newDocument();

		Element methRep = reponse.createElement("methodResponse");
		reponse.appendChild(methRep);

		Element paramS = reponse.createElement("params");
		methRep.appendChild(paramS);

		Element retParam = reponse.createElement("param");
		paramS.appendChild(retParam);

		Element retValue = reponse.createElement("value");
		retParam.appendChild(retValue);

		retValue.setTextContent("void");//faire un filtre pour choiri la bonne valeur

		for(Object o : args){
			retParam = reponse.createElement("param");
			paramS.appendChild(retParam);

			retValue = ObjectToXML.objectWithoutAnnotationsToElement(o.getClass().getSimpleName(), o, new ArrayList<String>(), reponse);
			retParam.appendChild(retValue);

		}

		StreamResult sr = new StreamResult(out);
		DOMSource ds = new DOMSource(reponse);
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.transform(ds, sr);

		out.send();		
	}


	/**
	 * Cette methode verifie que l'objet o implement chaque methode 
	 * de la classe itf. 
	 * @param o l'objet a tester
	 * @param itf l'interface
	 * @return true si l'objet implement la methode, false sinon
	 */
	private boolean implement(Object o, Class<?> itf) {
		Method[] methods = itf.getDeclaredMethods();
		for(Method m : methods){
			try {
				if( o.getClass().getMethod(m.getName(), m.getParameterTypes()) == null){
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public void display(Stringable s) {
		System.out.println("Un point 2D : " + s.toString()) ;
	}

}
