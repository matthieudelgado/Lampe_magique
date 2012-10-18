package server;

import objets.Movable;
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
	
	public void movex(Movable m,double dx);
	
	public int increment(int x);
	public double increment(double d);
}
