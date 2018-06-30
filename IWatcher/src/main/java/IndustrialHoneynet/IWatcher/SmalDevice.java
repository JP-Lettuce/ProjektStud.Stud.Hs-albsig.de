package IndustrialHoneynet.IWatcher;

import org.snmp4j.smi.OID;

/****
 * class SmalDevice
 * @author JP
 * Desc:
 * 	A shorted version of the Device class
 */
public class SmalDevice {
	private String name; //get via snmp
	private String ipAddress;
	private String lastFingerprint;
	private OID fingerprintOID;
	

	public String getLastFingerprint() {
		return lastFingerprint;
	}

	public String getName() {
		return name;
	}

	public String getipAddress() {
		return ipAddress;
	}


	public OID getFingerprintOID() {
		return fingerprintOID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setipAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setLastFingerprint(String lastFingerprint) {
		this.lastFingerprint = lastFingerprint;
	}
	
	public void setFingerprintOID(OID fingerprintOID) {
		this.fingerprintOID = fingerprintOID;
	}


}
