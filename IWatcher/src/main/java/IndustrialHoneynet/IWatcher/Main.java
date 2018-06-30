package IndustrialHoneynet.IWatcher;

public class Main {
	
	public static void main(String[] args) {
		
		if(args[0].equals("GUI")) {
			//start the GUI
		}else {
			if(args[0].equals("NW")) {
				new Controll();	//starting the Network Watcher
			}else {
				System.out.println("!-- Fehler: Unbekannte Funktion: " + args[0]);
			}
		}
	}

}
