package xmlrmi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette classe est une surcouche de ByteArrayOutputStream.
 * Elle permet d'ecrire un document xm sur un outputstream.
 */
public class XMLOutputStream extends  ByteArrayOutputStream {

	private DataOutputStream outchannel;

	public XMLOutputStream(OutputStream outchannel) {
		super();
		this.outchannel = new DataOutputStream(outchannel);
	}

	public void send() throws IOException {
		byte[] data = toByteArray();
		outchannel.writeInt(data.length);
		outchannel.write(data);
		reset();
	}
}