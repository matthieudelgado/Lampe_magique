package objets;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javassist.ClassPool;
import javassist.CtField;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tools.ObjectToXML;
import xmlrmi.XMLRMIField;
import client.Client;

public class Point implements XMLRMISerializable, Stringable {

	// attention construire une partie de l'XML en local pour les champs dans la metthode de contstruction XML


	@XMLRMIField(serializationName = "x", serializationType = "double")
	protected double a;

	@XMLRMIField(serializationName = "y", serializationType = "double")
	protected double b;

	@XMLRMIField(serializationName = "mark", serializationType = "string")
	protected String marque="m";

	private String oid = "testC";
	

	private static Integer compteur = 0;

	public Point(int x, int y){
		this.a=x;
		this.b=y;
	}

	@Override
	public String toString(){
		return "x = "+this.a+ " y =  " + this.b+" mark = "+this.marque;
	}

	public String getOid(){
		return this.oid;
	}


	// recuperer de l'interface la classe pour avoir les mï¿½thode a passer dans l'XML
	@Override
	/**
	 * 
	 */
	public Element toXML(Class<?> inter,Document doc) {
		//initOid();
		Client.repertoire.put(this.oid, this);
		
		String interString = inter.getName();
		String tostring = "public String toString(){return \"x = \"+this.x+ \" y =  \" + this.y+ \" marque \"+this.mark;}";
		ArrayList<String> aString= new ArrayList<String>();
		aString.add(tostring);
		return ObjectToXML.appelClientToDocument(this.getOid(),this, aString,doc);
		
		
	}

	@Override
	//TODO faire appel a une methode static pour plus de proprete
	public void updateFromXML(Element theXML) {
		NodeList fields = theXML.getChildNodes();
		
		int nbField = fields.getLength();
		String name=""; // Contiendra la valeur de name pour chaque field traitŽ

		//recupere la liste des attributs annotŽs
		// et les stocke dans fieldAnnote
		for(int j =0; j<this.getClass().getDeclaredFields().length;j++){

			Field fieldObj = this.getClass().getDeclaredFields()[j];

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
							if(type.equals("double")){
								double value=Double.parseDouble(fields.item(i).getFirstChild().getFirstChild().getTextContent());
								try {
									fieldObj.setDouble(this, value);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}else if(type.equals("int")){
								int value=Integer.parseInt(fields.item(i).getFirstChild().getFirstChild().getTextContent());
								try {
									fieldObj.setInt(this, value);
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								}
							}else if(type.equals("string")){
								String value = fields.item(i).getFirstChild().getFirstChild().getTextContent();
								value="\""+value+"\"";
								try {
									fieldObj.set(this, value);
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								//IDEE Invokation du set correspondant au champs
								// passer avec des annnotation?
								

							}
							System.out.println("type : "+type);
							//Object value = fields.item(i).getFirstChild().getFirstChild().getTextContent();
						}
					}


				}
			}
			fieldObj.setAccessible(false);
		}

		
	}

	@Override
	/**
	 * Initialise l'oid de l'objet
	 */
	public void initOid() {
		if(this.oid == "") return;
		synchronized(compteur){
			this.oid =""+this.getClass().getName()+"_"+compteur;
			compteur++;
		}
	}

	
}
