package IndustrialHoneynet.IWatcher;

import java.io.IOException;

import org.snmp4j.smi.OID;

/****
 * CLass SNMPControll
 * @author JP
 * Desc:
 * 	Controlls all funktions related to snmp
 */
public class SNMPControll {
	//other "need to work with" classes
	private SNMPManager snmpM;		//SNMPManager manages all snmp funtkions
	private Controll controll;		//Controll to contorll workeflow interaction
	
	private String name;			//Name of the target
	private String ip;				//IP of the target
	
	//standart snmp variables
	private OID discoOID = new OID(".1.3.6.1.4.1.248.11.20.1.4.3.0");	//OID for Visual marking
	private OID statusOID = new OID(".1.3.6.1.4.1.248.11.21.1.3.1.0");	//OID for synflag
	private String lastfingerprint;										//lastfingerprint of target
	private OID fingerprintOID;											//OID of the fingerprint

	/***
	 * Constructor SCNMPControll
	 */
	public SNMPControll() {
		
	}
	
	/***
	 * Constructor SNMPControll
	 * @param pname
	 * @param pip
	 * @param plastfp
	 * @param pfingOID
	 * @param ppush
	 */
	public SNMPControll(String pname, String pip, String plastfp, OID pfingOID, Controll pcontroll) {
		//set global variables for target
		this.name = pname;
		this.ip = pip;
		this.lastfingerprint = plastfp;
		this.fingerprintOID = pfingOID;
		this.controll = pcontroll;
		
		this.snmpM = new SNMPManager("udp:" + this.ip + "/161");	//creat snmp Session to target
		
		try {
			this.snmpM.start();		//start the snmp session
		} catch (IOException e) {
			this.controll.toPrint("!--- FEHLER:SNMP-MANAGER: " + e);
		}
		
	}
	
	/***
	 * public method finderprint
	 * @return int status
	 * Desc:
	 * 	checks the Finegrprint of the given machine
	 */
	public int fingerprintCheck(){
	    
	    this.controll.toPrint(" --- Kontroliere Fingerprint von " + this.name);
	    String def = this.lastfingerprint.toString();	//fingerprint default
	    String acc = " ";					//fingerprint actual
	    int status = 900;					//set status to malfunction
		
		//fingerprint
		try {
			acc = this.snmpM.getAsString(this.fingerprintOID);
		} catch (IOException e) {
			this.controll.toPrint("!--- FEHLER:SNMP-MANAGER:FINGERPRINT: " + e);
		}

	    
	    if(acc.equals(def)){		//do if fingerprint is OK
	      status = 2;				//status clearing, next is Syn-check
	      this.controll.toPrint(" ---> Keine Abweichung gefunden.");
	    }
	    else{						//do if fingerprint differs
	      status = 4;				//status update, next is Confcheck
	      this.controll.toPrint(" ---> Abweichung gefunden."); 
	      this.controll.toPush(6, "Momentaner Fingerprint: " + acc, this.name, this.ip);
	    }
	    return status;				//return next to do
	  }//eom fingerprint
	
	
	/***
	 * public method syncheck
	 * @return in status
	 * Desc:
	 * 	Checking if the Syn flack is "ok"
	 */
	public int synCheck(){
	    
	    this.controll.toPrint(" --- Kontrolliere Synkronitaet von " + this.name);
	    int status = 900; 		//set the status to malfunction
	    int syn = 900;			//preset the synflag to an error    
		String flag = "";		//buffer for synflag
		
		try {
			flag = this.snmpM.getAsString(this.statusOID);		//load the synflag from traget
		} catch (IOException e) {
			this.controll.toPrint("!--- FEHLER:SNMP-MANAGER:SYN: " + e);
		}
		
		syn = Integer.parseInt(flag);
	    
	    switch(syn) {			//switch syn flag status (1=OK, 2=warning, 3=fatal)
	    case 1 : status = 3;
	    		this.controll.toPrint(" ---> Geraet ist in Synk");
	    		break;
	    case 2:	status = 4;
	    		this.controll.toPrint(" ---> Geraet ist nicht in Synk");
	    		this.controll.toPush(7, "none", this.name, this.ip);
	    		break;
	    case 3: status = 4;
	    		this.controll.toPrint("!---> WARNUNG! Synflag ist auf hoechster Problemstufe!");
	    		this.controll.toPush(5, "none", this.name, this.ip);
	    		break;
	    default:status = 4;
	    		this.controll.toPrint("!---> Synflag wurde fehlerhaft empfangen");
	    }//eo switch, synflag
	    
	    return status;		//return next to do
	    
	  }//eom syncheck
	
	/***
	 * public method discEng
	 * Desc:
	 * 	aktivates the visual marker of the target
	 */
	public void discEng() {	
		
		this.controll.toPrint(" ---- Aktiviere visuellen Marker.");
		int var = 1;
		this.snmpM.set(this.discoOID, var);
	}//eom discEng()
	
	/***
	 * public method discDisEng
	 * Desc:
	 * 	deaktivates the vistal marker of the target
	 */
	public void discDisEng() {
		
		this.controll.toPrint(" ---- Deaktiviere visuellen Marker.");
		//Variable var = new OctetString("2");
		int var = 2;
		this.snmpM.set(this.discoOID, var);
	}//eom discDiEng

}//eoc
