package IndustrialHoneynet.IWatcher;

/*	Copied from 
 * 	http://www.jitendrazaa.com/blog/java/snmp/create-snmp-client-in-java-using-snmp4j/
 * 	Author: Jitendra Zaa
 * 	Date: 16.05.2018
 */

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPManager {
	
	Snmp snmp = null;
	String address = null;
	
	/**
	* Constructor
	* @param add
	*/
	public SNMPManager(String add)
	{
		address = add;
	}
	
	/**
	* Start the Snmp session. If you forget the listen() method you will not
	* get any answers because the communication is asynchronous
	* and the listen() method listens for answers.
	* @throws IOException
	*/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start() throws IOException {
		TransportMapping transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		// Do not forget this line!
		transport.listen();
	}
	
	/**
	* Method which takes a single OID and returns the response from the agent as a String.
	* @param oid
	* @return
	* @throws IOException
	*/
	public String getAsString(OID oid) throws IOException {
		ResponseEvent event = get(new OID[] { oid });
		return event.getResponse().get(0).getVariable().toString();
	}
	
	/**
	* This method is capable of handling multiple OIDs
	* @param oids
	* @return
	* @throws IOException
	*/
	public ResponseEvent get(OID oids[]) throws IOException {
		PDU pdu = new PDU();
		for (OID oid : oids) {
			pdu.add(new VariableBinding(oid));
		}
		pdu.setType(PDU.GET);
		ResponseEvent event = snmp.send(pdu, getTarget(), null);
		
		if(event != null) {
			return event;
		}
		throw new RuntimeException("GET timed out");
	}
	
	public void set(OID oid, int var){
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(oid, new Integer32(var)));
		pdu.setType(PDU.SET);
		try {
			snmp.send(pdu, getTarget(), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* This method returns a Target, which contains information about
	* where the data should be fetched and how.
	* @return
	*/
	private Target getTarget() {
		Address targetAddress = GenericAddress.parse(address);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("private"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version2c);
		return target;
	}
	
}