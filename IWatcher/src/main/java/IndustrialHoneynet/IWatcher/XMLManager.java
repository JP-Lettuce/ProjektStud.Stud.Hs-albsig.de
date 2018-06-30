package IndustrialHoneynet.IWatcher;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/****
 * Class XMLManager
 * @author JP
 * Desc:
 * 	Manages all xml funktions
 * 	reads in the content of the Options.xml file
 */
public class XMLManager {
	private String options;
	
	public XMLManager(String poptions) {
		
		this.options = poptions;
		
	}
	
	public void openFile(String pfile) {
		this.options = pfile;
	}
	
	public boolean checkUse() {
		boolean use = false;
		String useFlag = "";
		
		File fXmlFile = new File(this.options);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("options");
			Node nNode = nList.item(0);
			
			Element eElement = (Element) nNode;
			useFlag = eElement.getElementsByTagName("use").item(0).getTextContent();
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//eo try
				
		if(useFlag.equals("true")) {
			use = true;
		}
		
		return use;
	}
	
	public String[] readOptions() {
		String[] options = new String[10];
		String value = "";
	
		
		for(int i = 0; i<10; i++) {
			options[i] = "";
		}

		 
		try {
			File fXmlFile = new File(this.options);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();
					
			NodeList nList = doc.getElementsByTagName("options");
			Node nNode = nList.item(0);
			
			Element eElement = (Element) nNode;
			String[] tagNames = {"printTime","cycleTime", "maxCycle", "apiToken", "userToken", "sftpHost", "sftpUser", "sftpPw", "sshUser", "sshPW"};
			String tagName = ""; 
			
			 for(int i=0; i < 10; i++ ) {
				 	tagName = tagNames[i];
				 	System.out.print("  >> Option: " + tagName);
				 	
				 	nList = doc.getElementsByTagName(tagName);
					nNode = nList.item(0);
					
					eElement = (Element) nNode;
					
					value = eElement.getElementsByTagName("value").item(0).getTextContent();
					
					if(value.equals("")) 
						value = eElement.getElementsByTagName("default").item(0).getTextContent();;
					options[i] = value;
					
					System.out.println(" = " +value);
					
					value = "";
					tagName = "";
			 }
		
		}catch(EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return options;
	}

}
