package IndustrialHoneynet.IWatcher;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import org.snmp4j.smi.OID;


public class Controll {
	//used classes
	private PrintManager pm;			//PrintManager controlls all Console interaction, simply prints
	private PushManager push;			//PushManager manages all push Notification related functions
	private SNMPControll snmp;			//SNMPControll controlls all snmp related functions
	private DeviceControll divcon;		//DeviceControll controlls all Device related funktions and acts as a file interface
	private ConfigManager confman;		//ConfigManager manages all Configfile related funktions
	private XMLManager xml;				//XMLManager manages the options read in from xml file
	
	//Device related Variables
	private String twaName;				//the name of current "to work at" device
	private String twaIP;				//the ip  of the current twa device
	private int numDiv;					//numbers of devices to work on in the current cycle
	private String lastfingerprint;		//latest fingerprint (hash of condiguration) of the twa device
	private OID fingerprintOID;			//the snmp OID "where to find the fingerprint" of the twa device
	
	//Optionsfile Location
	private String optionFile = "Options.xml";
	
	/***
	 * Constructor Controll
	 */
	public Controll(){
		System.out.println("Running...");
		//creating objekcts
		this.pm = new PrintManager();
		this.push = new PushManager(this);
		this.snmp = new SNMPControll();
		this.divcon = new DeviceControll(this);
		this.confman = new ConfigManager(this);
		this.xml = new XMLManager(this.optionFile);
		
		//setting global variables to default
		this.twaIP = "127.0.0.1";
		this.twaName = "localhost";
		this.numDiv= 0;
		
		//starting to work
		this.pm.gap();
		this.loadOptions();
		this.work();
	}//eo Constructor
	
	/***
	 * private mehtod loadOptions
	 * Desc:
	 * 	determines if there are Options, the user wants to use them and applies these
	 */
	private void loadOptions() {
		
			this.pm.print(" - Suche Benutzeroptionen");
			//Options for PrintManager
			int timePrint;						//time between prints (enhance readability)
			int timeCycle;						//time between cycles 
			int timeCycleMin;					//time between cycles in minutes
			
			//Options PushManager
			int maxCycle;						//max Cycles till Pushlog reset
			String apiToken;					//Pushover Token for this Aplication
			String userToken;					//Pushover Token of user
			
			//Options ConfigManager
			String sftpHost;					//IP adress sftp-server
			String sftpUser;					//name of sftp-server user
			String sftpPw;						//password of sftp-sever user
			String sshUser;						//name of ssh user
			String sshPw;						//password of ssh suer
			
		    if(new File(this.optionFile).exists()) { 	//check if options file exists
		    	if(this.xml.checkUse()) {				//check if user wants to use the Option.xml
		    		
			    	this.pm.print(" - Lade Benutzeroptionen");
			    	String[] options = this.xml.readOptions();		//call the xmlManager to read the options from file
			    		
					//Options for PrintManager
					timePrint = Integer.parseInt(options[0]);
					timeCycleMin = Integer.parseInt(options[1]);
					timeCycle = timeCycleMin * 60000;						
					
					//Options PushManager
					maxCycle = Integer.parseInt(options[2]);						
					apiToken = options[3];		
					userToken = options[4];				
					
					//Options ConfigManager
					sftpHost = options[5];				
					sftpUser = options[6];				
					sftpPw = options[7];						
					sshUser = options[8];						
					sshPw = options[9];					
					
					//set Options for all
					this.pm.setOptions(timePrint, timeCycle);							//set options for the PrintManager
					this.push.setOptions(maxCycle, apiToken, userToken);				//set options for the PushManager
					this.confman.setOption(sftpHost, sftpUser, sftpPw, sshUser, sshPw); //set options for the ConfigManager
					this.pm.print(" - Benutzeroptionen Geladen");
		    	}else {
		    		this.pm.print(" - Benutzeroptionen sollen nicht geladen werden");
		    		this.pm.print(" - Lade Default Optionen");
		    	}//eo if-else, "use the options?"
		    	
		    }else {
			    this.pm.print("!- Benutzeroptionen nicht gefunden");
			    this.pm.print(" - Lade Default Optionen");
			}//eo if-else, "is there a Options.xml?"
		    this.pm.gap();		//print a blank line
	}//eom loadOptions
	
	/***
	 * private method work
	 * Desc:
	 * 	Controlls and manages the workflow
	 */
	private void work() {
		int status = 0;													//controle state, starting condition
		boolean work = true;											//work state on Device, starting condition
		this.pm.print(" - Alle Konfigurationen finden sie in: ");
		this.pm.print(" -> Defaults: " + System.getProperty("user.dir") + "\\conf\\default\\");
		this.pm.print(" -> Logfiles: " + System.getProperty("user.dir") + "\\conf\\store\\");
		this.pm.gap();
		
		//main loop, ensures to runn the programm for "ever"
		while(true) {
			
			this.pm.print(" - Beginne Arbeitszyklus");		
			this.push.incCount();										//increas the count of workcycles for push resets
			
			ArrayList<SmalDevice> devices = this.divcon.readDevices();	//fetching all saved Devices
			this.numDiv = devices.size();								//determine the number of Devices
			
			if(this.numDiv > 0) {		//"are there any Devices to work on?"
				this.pm.print(" -- Beginne arbeit an " + this.numDiv + " Geraeten.");
			}else {
				this.pm.print("!-- Es wurden keine zu ueberwachenden Maschinen gefunden");
			}
			
			//Workcycle Loop, "for each in"
			for(SmalDevice device: devices) { 
				this.twaIP = device.getipAddress();					//set IP of current machnie
				this.twaName = device.getName();					//set Name of current machine
				this.lastfingerprint = device.getLastFingerprint(); //set last fingerprint of twa Device
				this.fingerprintOID = device.getFingerprintOID();	 //set the fingerprint OID of twa Device 
				
				
				status = 0;											//reset status to starting condition			
				work = true;										//reset work state to starting condition
				this.pm.startM(this.twaName, this.twaIP);			//print Machine start message
				
				//work on the twa Device
				while(work) {
							
				this.pm.gap();					//print a blank line
				this.snmp = new SNMPControll(this.twaName, this.twaIP, this.lastfingerprint, this.fingerprintOID, this.push, this.pm);
				
					//workflow controll switch
					switch(status) {
					case 0: status = this.pingcheck();		//check if reachable with ping, reachable returns 1 - ! returns 404
							break;	
							
					//check the fingerprint, OK status = 2 - ! OK status = 4
					case 1: this.snmp.discEng(); 						//enable a visial marker on twa device		
							status =  this.snmp.fingerprintCheck();		//check the fingerprint
							break;	
							
					//check the synflag, OK status = 3 - ! Ok status = 4
					case 2: status = this.snmp.synCheck(); 	//check the sync						
							break; 
							
					//check if everifing is OK if we need the default Config of the twa Device		
					case 3: this.confman.checkDeafult(this.twaName, this.twaIP); 		//check for a safed default or save as new default
							status = 5; 												//everithing was OK, end of work
							break; 
					
					//check the configuration, OK status = 5 - !OK status = 6
					case 4: status = this.confman.configcheck(this.twaName, this.twaIP);//analyse the Config file, OK returns 5 - ! returns 6
							break;
					
					//Everithing was OK, disengage marker
					case 5: this.snmp.discDisEng(); 
					
					//exit workflow, standart
					case 6: work = false; 	
							break;		
						
					//exit workflow, host not reacheblae
					case 404: work = false; 
							  break;	
							  
					//if status out of workflow index, abort and kill
					default: this.push.push(900, "Kontorl switch out of index.", this.twaName, this.twaIP);	//Error in control switch, push and kill
								System.exit(-1);
					}//eo switch, workflow control
					
				}//eo while, work on twa device
				
				this.pm.endM(this.twaName);			//print Machine end message
				
			}//eo for each, workcycle
			
			this.pm.endC();							//print Cycle end message and wait for next cycle
			
		}//eo while, main loop	
	}//eom work
	
	/***
	 * private method pingcheck
	 * @return int status
	 * Desc:
	 * 	Checking if acIP is reachable via ping
	 */
	private int pingcheck(){
		
		this.pm.print(" --- Pinging " + this.twaName);
	    int status = 900;						//set status to malfunction
	    boolean reachable = false;				//set reachability to "not reachable"
	    
	    try {
	    	InetAddress inet = InetAddress.getByName(this.twaIP);	//set destination for pinging
	    	reachable = inet.isReachable(5000);						//check if reachable, returns true if ping returns
	    	
	    }catch(IOException ex) {
	    	this.push.push(900, "" + ex);		//push given error and move on
	    	
	    }finally{
	    							//there's nothing left to say now~~
	    }// eo try, pinging
	    if(reachable){							//do if reachable
		      status = 1;							//status clearing, next is fingerprint
		      this.pm.print(" ---> Host war zu erreichen.");

		    }
		    else{									//do if !reachable

		      this.pm.print(" ---> Host antwortet nicht.");
		      this.push.push(1,"none", this.twaName, this.twaIP);					//send push
		      status = 404;							//status not reachable
		    }//eo if-else, status change
		    
		    return status; //return next to do
	}// eom pingcheck
	
	/**
	 * public method toPrint
	 * @param pString
	 * Desc:
	 * 	Allowes the workflow to parse a print
	 */
	public void toPrint(String pString) {
		this.pm.print(pString);
	}//eom toPrint
	
	/**
	 * public method toPush
	 * @param perrcode
	 * @param pdetails
	 * Desc: 
	 * 	Allowes the workflow to parse a push
	 */
	public void toPush(int perrcode, String pdetails) {
		this.push.push(perrcode, pdetails);
	}//eom toPush
	
	/**
	 * public method toPush
	 * @param perrcode
	 * @param pdetails
	 * @param pname
	 * @param pip
	 * Desc:
	 * 	Allowes the workflow to parse a push
	 */
	public void toPush(int perrcode,String pdetails, String pname, String pip) {
		this.push.push(perrcode, pdetails, pname, pip);
	}//eom toPush
	
	/**
	 * public method makeGap
	 * Desc:
	 * 	Allows the workflow to request a blank console line
	 */
	public void makeGap() {
		this.pm.gap();
	}//eom makeGap
}//eoc
