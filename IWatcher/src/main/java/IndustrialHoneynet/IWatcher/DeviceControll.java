package IndustrialHoneynet.IWatcher;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.snmp4j.smi.OID;

/****
 * Class DeviceControll
 * @author JP
 * Desc:
 * 	The DeviceControll controlls all device related functions
 */
public class DeviceControll {
	
	private String workfile = "Devices.txt";		//lokation of the Divicestore file
	private Controll controll;						//Controll to contorll workeflow interaction
	
	/***
	 * Constructor DeviceControll
	 * @param ppush
	 * @param ppm
	 */
	public DeviceControll(Controll pcontroll) {
		this.controll = pcontroll;
	}
	
	/***
	 * public method readDevices
	 * @return
	 * Desc:
	 * 	Reads in all Devices from the workfile and creates a ArrayList
	 * 	with the SmalDevice class as content
	 */
	public ArrayList<SmalDevice> readDevices(){
		
		SmalDevice device = null;										//create empty new device
		ArrayList<SmalDevice> devices = new ArrayList<SmalDevice>();	//create new ArrayList	
		JSONParser parser = new JSONParser();							//create JSONParser
		
		try {
			//create JSON objekt out of File content
			Object obj = parser.parse(new FileReader(this.workfile));	
			JSONObject jsonDevices = (JSONObject) obj;
			
			//prepare to read the JSON-content with interators
			@SuppressWarnings("rawtypes")
			Iterator iterator = jsonDevices.keySet().iterator();
			
			//for each Device (interator)
			while(iterator.hasNext()) {
				
				device = new SmalDevice();					//create new device
				
				//get the device IP
				String ipAddress = (String)iterator.next();
				device.setipAddress(ipAddress);

				//Fix on this Device (interator) for more content
				JSONObject jsonDevice = (JSONObject) jsonDevices.get(ipAddress);
				
				//get the device name
				String name = (String) jsonDevice.get("name");
				device.setName(name);
				
				//get the device lastfingerprint
				String fingerprint = (String) jsonDevice.get("fingerprint");
				device.setLastFingerprint(fingerprint);
				
				//get the device fingerprintIOD
				String fingerprintOID = (String) jsonDevice.get("fingerprintOID");
				device.setFingerprintOID(new OID(fingerprintOID));
				
				//ad this device to the ArrayList
				devices.add(device);
				}//eo for-each
			
			}catch(FileNotFoundException e) {
				this.controll.toPrint("# FEHLER:Devicefetch: " + e );
				this.controll.toPush(3, "FEHLER:Devicefetch: " + e );
		    }catch(IOException e) {
		    	this.controll.toPrint("# FEHLER:Devicefetch: " +e );
		    	this.controll.toPush(3, "FEHLER:Devicefetch:  "+ e);
		    }catch (org.json.simple.parser.ParseException e) {
		    	this.controll.toPrint("# FEHLER:Devicefetch: " +e );
		    	this.controll.toPush(3, "FEHLER:Devicefetch: " + e);
			}
		return devices;
	}//eom readDevices

}//eoc
