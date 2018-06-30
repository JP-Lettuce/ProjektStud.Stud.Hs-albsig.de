package IndustrialHoneynet.IWatcher;

public class PrintManager {
	
	//Global Variables throu Options
	private int sleepTimePrint;
	private int sleepNextCycle;
	
	/***
	 * default Constructor PrintManager
	 */
	public PrintManager() {
		
		this.setOptionsDefault();
		this.bootScreen();
	}
	
	public void setOptions(int pSleepTimePrint, int pSleepNextCycle) {
		this.sleepNextCycle = pSleepNextCycle;
		this.sleepTimePrint = pSleepTimePrint;
		
	}
	
	public void setOptionsDefault() {
		this.sleepTimePrint = 600;
		this.sleepNextCycle = 120000;
	}
	
	private void bootScreen() {
		System.out.println("/------------------------------------------------------------\\");
		System.out.println("|              HIRSCHMANN - INDUSTRIAL HONEYNET              |");
		System.out.println("|                    NETWORK WATCHER v1.0                    |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("|          AUTOREN: Lukas Voetsch und Janek Pelzer           |");
		System.out.println("|            Unter der Leitung von Andreas Kompter           |");
		System.out.println("|          Mit Unterstützung von:                            |");
		System.out.println("|           - Alex Pangui                                    |");
		System.out.println("|           - Fabio Oehme                                    |");
		System.out.println("|           - Max Rink                                       |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("|        Betreuender Professor: Prof. Dr. Tobias Heer        |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("|          In zusammenarbeit mit: HIRSCHMANN GMBH            |");
		System.out.println("|- - - - - - - - - - - - - - -- - - - - - - - - - - - - - - -|");
		System.out.println("| Enstanden im Rahmen des Projekstudiums: Industrialhoneynet |");
		System.out.println("| der Hochschule Albstadt-Sigmaringen im SS 2018.            |");
		System.out.println("| Alle Rechte vorbehalten.                                   |");
		System.out.println("X------------------------------------------------------------X");
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/***
	 * public method print
	 * @param pstring
	 * Desc:
	 * 	print shortcut method
	 */
	public void print(String pstring) {
		System.out.println(pstring);
		try {
			Thread.sleep(this.sleepTimePrint);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//eom print
	
	
	/***
	 * public method endM
	 * Desc:
	 * 	outprint after finished working on a machine
	 */
	public void endM(String name) {
		this.print(" -- Ende der bearbeitung von " + name);
		this.print("\n ------------------------------------------------------------ \n");
	}//eom endM
	
	/***
	 * public method startM
	 * Desc:
	 * 	outprint at start on wokring on a machine
	 */
	public void startM(String name, String ip) {
		this.print(" -- Beginne Arbeit an: " + name + " (" + ip + ")");
	}// eom startM
	
	/***
	 * public method endC
	 * Desc:
	 * 	outprint after finishing a work cycle
	 */
	public void endC() {
		
		int ncInMin = this.sleepNextCycle / 60000;
		
		this.print(" - Ende des Arbeitszyclus");
		this.print(" - Naechster Zyklus in " + ncInMin + " Minuten");
		this.gap();
		this.print("|------------------------------------------------------------|");
		this.waitNextCycle();
	}// oem endC
	
	private void waitNextCycle() {
		
		int progress = this.sleepNextCycle / 60;
		System.out.print("|");
		
		for( int i = 0; i< 60; i++) {
			try {
				Thread.sleep(progress);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.print("x");
		}
		
		System.out.print("| \n");
		this.print("|------------------------------------------------------------|");
		this.gap();
	}
	
	/***
	 * public method gap
	 * Desc:
	 * 	Print en empty line on the console
	 */
	public void gap() {
		this.print("");
	}//eom gap

}
