package server;

import java.util.Date;

import objets.Movable;
import objets.ReversibleXY;
import objets.ReversibleXYContainer;
import objets.Stringable;
import objets.StringableContainer;
import objets.StringableContainerType;
/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette interface est l'interface du serveur.
 * Elle definie toutes les methodes appeleable depuis l'exterieur
 *
 */
public interface IServer {
	public void display(Stringable s);
	public void displayField(StringableContainer c);
	public void displayField(StringableContainerType c);
	
	public ReversibleXYContainer reverseXYField(ReversibleXYContainer r);
	public ReversibleXY reverseXY(ReversibleXY r);
	
	public void movex(Movable m,double dx);
	
	public int increment(int x);
	public double increment(double d);
	public boolean opposite(boolean b);
	public String concatWorld(String hello);
	public int[] inverse(int[] tab);
	public double[] inverse(double[] tab);
	public String[] inverse(String[] tab);
	public Stringable[] inverse(Stringable[] tab);
	
	public boolean isD1BeforeD2(Date d1, Date d2);
	
}
