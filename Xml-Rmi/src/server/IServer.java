package server;

import objets.Movable;
import objets.PointContainer;
import objets.ReversibleXY;
import objets.Stringable;
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
	public void displayField(PointContainer c);
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
	
}
