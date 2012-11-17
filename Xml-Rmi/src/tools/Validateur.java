package tools;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.rng.CompactSchemaReader;

public class Validateur {
	public static void validateXmlAgainstRnc(String message, String rncFilePath) 
            throws SAXException, IOException{
        
        final InputSource isg = ValidationDriver.fileInputSource(rncFilePath);
        final ValidationDriver vd = 
                new ValidationDriver(CompactSchemaReader.getInstance());
        vd.loadSchema(isg);
        InputSource is = new org.xml.sax.InputSource(new StringReader(
                message));
        
        if (!vd.validate(is)) {
            throw new SAXException("Invalid XML program!");
        }
    }
}
