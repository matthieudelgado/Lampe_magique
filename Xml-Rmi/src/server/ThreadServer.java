package server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import objets.Stringable;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import tools.TestEcritureXML;
import tools.XMLToObject;
import xmlrmi.XMLInputStream;
import xmlrmi.XMLOutputStream;

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

	private void doTreatement(Document doc) throws Exception {
		TestEcritureXML.afficherDocument(doc);
		//on commence par verifier l'integrite du doc par rapport a la grammaire
		
		//on recherche la méthode appelée
		Node n = doc.getElementsByTagName("methodeName").item(0).getFirstChild();
		System.out.println("nom de methode "+n.getTextContent());
		String MethodeName = n.getTextContent();
		//on recupère les parametres de la methode dans le xml
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
		//Dans paramList ici on a les parametre de la methode display
		
		//on verfie qu'elle existe dans l'inteface IServer
		Class<IServer> itf = IServer.class;
		Method[] methods = itf.getDeclaredMethods();
		Method calledMethod = null;
		ArrayList<Object> args = new ArrayList<Object>();
		Class<?>[] parameterTypes = null;
		boolean trouve = false;
		for(Method m : methods){
			//on commence par verifier si le nom correspond
			if( !m.getName().equalsIgnoreCase(MethodeName)) continue;
			//on verifie ensuite les arguments
			parameterTypes = m.getParameterTypes();
			//on compare le type des parametres avec le xml
			for(int i = 0 ; i < parameterTypes.length; i++){
				System.err.println(parameterTypes[i].getSimpleName());
				Object o = XMLToObject.createObjectFromNode(paramList.get(i));
				System.err.println(o);
				//si le parametre attendu est une interface, il faut tester
				//que l'objet implemente les methode de l'interface
				if(parameterTypes[i].isInterface()){
					if(implement(o, parameterTypes[i])){//on regarde si o implemente l'itf
						o = addInterface(o, parameterTypes[i]);//on ajoute l'itf a la classe de o
						args.add(o);
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
		
		System.err.println(calledMethod.getName());
		System.err.println(args.size());
		Object ret = calledMethod.invoke(this, args.toArray());
		
		

	}
	

	private Object addInterface(Object o, Class<?> class1) throws Exception {
		CtClass clazz = ClassPool.getDefault().get(o.getClass().getName());
		clazz.addInterface(ClassPool.getDefault().get(class1.getName()));
		Object ret = clazz.toClass().newInstance();
		for(Field f : ret.getClass().getDeclaredFields()){
			ret.getClass().getField(f.getName()).set(ret, o.getClass().getField(f.getName()).get(o));
		}
		return ret;
	}

	private boolean implement(Object o, Class<?> class1) {
		Method[] methods = class1.getDeclaredMethods();
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
	
	// Construction de l'objet depuis un XML
}
