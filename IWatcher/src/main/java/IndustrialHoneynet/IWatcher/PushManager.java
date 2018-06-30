package IndustrialHoneynet.IWatcher;

import java.util.ArrayList;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverException;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;

/****
 * CLass PushManager
 * @author JP
 * Desc:
 * 	The PushManager manages all pusrealted funktions
 * 	The Pushservice used is "Pushover"
 */
public class PushManager {
	
	private Controll controll;				//Controll to contorll workeflow interaction
	private ArrayList<String> Errlog;		//A list of allready send pushes to reduce traffic, resets all 360 cycles (720min)
	private int countCycle;					//The accounted cycles
	
	//Global Variables throu Options
	private int maxCycle;					//The max cycles befor reset
	private String apiToken;				//Pushover Token of this Application
	private String userToken;				//Pushover Token of the User to recive the push
	
	/***
	 * Constructor PushManager
	 * @param ppm
	 */
	public PushManager(Controll pcontroll) {
		this.controll = pcontroll;
		this.Errlog = new ArrayList<String>();
		this.countCycle=0;
		
		this.setOptionsDefault();
	}
	
	/***
	 * public method setOptions
	 * @param pmaxCycle
	 * @param pApiToken
	 * @param pUserToken
	 * Desc:
	 * 	Set Options for custom usage
	 */
	public void setOptions(int pmaxCycle, String pApiToken, String pUserToken) {
		this.maxCycle = pmaxCycle;
		this.userToken = pUserToken;
		this.apiToken = pApiToken;
	}
	
	/***
	 * public method setOtionsDefault
	 * Desc:
	 * 	set Options as deployed
	 */
	public void setOptionsDefault() {
		this.maxCycle = 360;
		this.apiToken = "ao23nv7oi7g5jtqqtmpbdn72aiwsxb";
		this.userToken = "u3pxnpzmmuoh62f852zvhagc59ch3p";
	}
	
	/***
	 * public method push
	 * @param errcode
	 * @param details
	 * @param name
	 * @param ip
	 * Desc:
	 * 	creates a push notifictaion via given name, ip, erroroce and details
	 */
	public void push(int errcode, String details, String name, String ip) {
		this.controll.toPrint("  ~~~ Erstelle Push-Nachricht");
	    
	    String messagetext= "Dies ist ihr NetworkWatcher.\n\n";  	//Start of the push massages  
	    
	    //switch the errorcode to generate the fitting pushmessage
	    switch(errcode){
	      case 1: messagetext += "Das Geraet " + name + " (" + ip + ") konnte nicht mit einem Ping erreicht werden. \n\n Details: \n" + details;
	      		break;
	      case 2: messagetext += "Die Konfiguration von " + name + " (" + ip+ ") wurde veraendert. Die abweichende Konfig wurde gespeichert. \n\n Details: \n" + details;
	        	break;
	      case 3: messagetext += "Der Fehler \n" + details + " trat auf. Bitte gehen sie so bald wie moeglich diesem Fehler nach, um eine reibungslose Funktion zu ermoeglichen.";
	        	break;
	      case 4: messagetext += "Die Logdatei der angeschlossenen Geraete kann nicht genutzt werden. \n\n Details: \n" + details;
	        	break;
	      case 5: messagetext += "Die Synflag des Geraets " + name + " (" + ip + ") ist auf hoechster Problemstufe! Bitte gehen sie dem umgehend nach!";
	      		break;
	      case 6: messagetext += "Der Fingerprint von " + name + " (" + ip + ") hat sich geaendert. \n\n Details: \n" + details;
	      		break;
	      case 7: messagetext += "Das Geraet " + name + " (" + ip + ") ist nicht mehr Synkron.";
	      		break;
	      default: messagetext += "Es ist ein unbekannter fehler aufgetreten waehrend " + name + " (" + ip + ") in bearbeitung war. \n\n Details: \n" + details;
	        	break;
	    }
	    
	    //check the log if push was allready send to reduce trafffic
	    if(!this.Errlog.contains(name+"-Code:"+errcode)) {
	    	this.sendPush(messagetext);					//send if new
	    	this.Errlog.add(name+"-Code:"+errcode);		//ad to log
	    }else {
	    	this.controll.toPrint("  ~~~> Push wurde bereits versand");
	    }
	}// eom push
	
	/***
	 * public method push
	 * @param errcode
	 * @param details
	 * Desc:
	 * 	creates a push notifictaion via given erroroce and details
	 * 	Name and Ip can be unknown
	 */
	public void push(int perrcode, String pdetails) {
		this.controll.toPrint("  ~~~ Erstelle Push-Nachricht");
		
		String name = "-unkown-";
		String ip = "-unkown-";
	    
		this.push(perrcode, pdetails, name, ip);
	}//eom push
	
	
	/***
	 * private method sendPush
	 * @param messagetext
	 * Desc:
	 * 	sends a Push to the given user via the Pushover service
	 */
	private void sendPush(String messagetext) {
		
		PushoverClient client = new PushoverRestClient(); 		//create net Poshover client session
		
		this.controll.toPrint("  ~~~~ Sende Push via Pushover");
		
		//try to fill in the messagecontents and send the push
		try {
			client.pushMessage(PushoverMessage.builderWithApiToken(this.apiToken)
			        .setUserId(this.userToken)
			        .setMessage(messagetext)
			        .build());
		} catch (PushoverException e) {
			// TODO Auto-generated catch block
			this.controll.toPrint("! ~~~~ FEHLER:Pushover: " + e);
		}
		
		this.controll.toPrint("  ~~~> Push erfolgreich gesendet");
	}//eom sendPush()
	
	/***
	 * public method incCount
	 * Desc:
	 * 	Increas the counter of workcycles in order to reset the log sometimes
	 */
	public void incCount() {
		this.countCycle++;
		
		//if 360 cylces are over, reset the log
		if(this.countCycle >= maxCycle) {
			this.Errlog = new ArrayList<String>();
			this.countCycle = 0;
		}//eo if
	}//eom incCount

}//eoc
