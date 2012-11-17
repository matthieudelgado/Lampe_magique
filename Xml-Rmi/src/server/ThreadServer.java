package server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import objets.Movable;
import objets.ReversibleXY;
import objets.ReversibleXYContainer;
import objets.Stringable;
import objets.StringableContainer;
import objets.StringableContainerType;

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

	/**
	 * Cette methode execute le corps du thread
	 * Le thread lit l'appel,  le traite et renvoi la reponse
	 */
	public void run(){
		System.out.println("Un client s'est connecte");
		try { 
			DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
			Document doc = null;

			in.receive();
			doc = docBuilder.parse(in);
			
			doTreatement(doc);
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
		String methodName = getMethodName(doc);

		//on recup√®re les parametres de la methode dans le xml
		ArrayList<Node> paramList = getParameters(doc); 

		//Dans paramList ici on a les parametre de la methode display

		//on verfie qu'elle existe dans l'inteface IServer et
		//que les parametre de paramList correspondent
		ArrayList<Object> args = new ArrayList<Object>();
		Method calledMethod = findCalledMethodInServerInterface(methodName, args, paramList);

		Object ret;
		if(calledMethod == null){//si on a pas trouvé la methode, message d'erreur
			ret = "methode introuvable";
			System.out.println("methode introuvable"); //TODO  il arrive pas a trouver la methode !!!!!
		} else {//sinon on l'applique
			System.out.print("do Treatement : le serveur applique "+calledMethod.getName()+"(");
			for(Class<?> c : calledMethod.getParameterTypes())
			{
				System.out.print(c.getSimpleName()+",");
			}
			System.out.println(")");
			ret = calledMethod.invoke(this, args.toArray());
		}

		//on envoi le resultat au client
		sendResult(ret, calledMethod.getReturnType(), calledMethod.getParameterTypes(), args);

	}

	/**
	 * Cette methode permet d'obtenir le nom de la methode du serveur appelee par le client
	 * @param doc le document xml envoyé par le client
	 * @return le nom de la methode
	 */
	private String getMethodName(Document doc){
		Node n = doc.getElementsByTagName("methodName").item(0).getFirstChild();
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
	 * @param methodName le nom de la methode appelee
	 * @param args la liste des arguments 
	 * @param paramList la liste des noeuds correspondants aux parametres dans le xml du client
	 * @return la methode appelee si elle existe, null sinon
	 * @throws Exception
	 */
	private Method findCalledMethodInServerInterface(String methodName, ArrayList<Object> args, ArrayList<Node> paramList) throws Exception{
		Class<IServer> itf = IServer.class;
		Method[] methods = itf.getDeclaredMethods();
		Method calledMethod = null;
		Class<?>[] parameterTypes = null;
		boolean trouve = false;
		boolean broken = false;
		for(Method m : methods)
		{
			//on commence par verifier si le nom correspond
			if( !m.getName().equalsIgnoreCase(methodName)) continue;
			//on verifie ensuite les arguments
			parameterTypes = m.getParameterTypes();
			if(parameterTypes.length != paramList.size()) continue;
			for(int i = 0 ; i < parameterTypes.length; i++)
			{
				if(XMLToObject.typeChecker(parameterTypes[i], paramList.get(i)))
				{
//					System.out.println("le type "+parameterTypes[i].getSimpleName()+" correspônd a "+paramList.get(i).getNodeName());
					continue;
				} 
				else
				{
					broken = true;
					break;
				}
			}
			if(broken){
				broken = !broken;
				parameterTypes = null;
				continue;
			} 
			calledMethod = m;
			break;
		}	
		for(int i = 0; i< paramList.size();i++)
		{
			args.add(XMLToObject.createObjectFromNode(paramList.get(i), parameterTypes[i]));
		}
		
		return calledMethod;
	}




	/**
	 * Cette methode envoi la reponse du serveur au client.
	 * @param ret la valeur de retour de la methode
	 * @param paramType 
	 * @param args les arguments de la methode dans leur nouvel etat
	 * @throws Exception
	 */
	private void sendResult(Object ret, Class<?> retType, Class<?>[] paramType, ArrayList<Object> args) throws Exception {
		DocumentBuilder docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document reponse = docB.newDocument();

		Element methRep = reponse.createElement("methodResponse");
		reponse.appendChild(methRep);

		Element paramS = reponse.createElement("params");
		methRep.appendChild(paramS);

		Element retParam = reponse.createElement("param");
		paramS.appendChild(retParam);

		Element retValue = reponse.createElement("value");


		//retValue.setTextContent("void");//TODO faire un filtre pour choiri la bonne valeur
		if(ret == null){
			ret = "void";
		}

		retValue = ObjectToXML.objectWithoutAnnotationsToElement(ret.getClass().getSimpleName(), retType, ret, new ArrayList<String>(), reponse);
		retParam.appendChild(retValue);
		int i = 0;
		for(Object o : args){
			retParam = reponse.createElement("param");
			paramS.appendChild(retParam);

			retValue = ObjectToXML.objectWithoutAnnotationsToElement(o.getClass().getSimpleName(), paramType[i], o, new ArrayList<String>(), reponse);
			retParam.appendChild(retValue);
			i++;

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

	@Override
	public void movex(Movable m,double dx) {
		m.move(dx);
	}

	@Override
	public int increment(int x) {
		return x+1;
	}

	@Override
	public double increment(double d) {
		return d + 1.0;
	}

	@Override
	public boolean opposite(boolean b) {
		return !b;
	}

	@Override
	public String concatWorld(String hello) {
		return hello+"World";
	}

	@Override
	public int[] inverse(int[] tab) {
		int[] t = new int[tab.length];
		for(int i = 0;i<t.length;i++){
			t[i] = tab[t.length - 1 - i];
		}
		return t;
	}

	@Override
	public Stringable[] inverse(Stringable[] tab) {
		Stringable[] t = new Stringable[tab.length];
		for(int i = 0;i<t.length;i++){
			t[i] = tab[t.length - 1 - i];
		}
		return t;
	}

	@Override
	public double[] inverse(double[] tab) {
		double[] t = new double[tab.length];
		for(int i = 0; i<t.length;i++){
			t[i]= tab[t.length-1-i];
		}
		return t;
	}

	@Override
	public String[] inverse(String[] tab) {
		String[] t = new String[tab.length];
		for(int i = 0; i<t.length;i++){
			t[i]= tab[t.length-1-i];
		}
		return t;
	}

	@Override
	public ReversibleXY reverseXY(ReversibleXY r) {
		r.reverse();
		return r;
	}

	@Override
	public void displayField(StringableContainer c) {
		System.out.println("Un point 2D dans un container: " + c.toString()) ;
	}

	@Override
	public ReversibleXYContainer reverseXYField(ReversibleXYContainer r) {
		r.reverse();
		return r;
	}

	@Override
	public boolean isD1BeforeD2(Date d1, Date d2) {
		return d1.before(d2);
	}

	@Override
	public void displayField(StringableContainerType c) {
		System.out.println("Une liste dans c : "+c.toString());
	}

}
