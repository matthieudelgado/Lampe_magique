package tools;


import java.util.ArrayList;

import javax.xml.parsers.*;



import objets.Point;
import objets.Stringable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TestDom {
	public static void main(String[] args) {
			
		Point p = new Point(1,2);
		p.toXML(Stringable.class);
		
		
	}
}
