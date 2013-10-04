package lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Config {
	public static Properties ebsPorperties;
	
	private Config(){}
	
	//set defualt value to be 127.0.0.1 for unit testing,this value will be read from web.xml when deploying the web to tomcat server.
	public static String EUCA_IP="127.0.0.1",DB_IP="127.0.0.1";
	public static String LDAP="itri.ds",ldapUrl,ldapAccount;
	
	
	
	public static String getLDAPUrl(String ldapv,String user){
		return Config.ldapUrl.replace("%ip%",ldapv).replace("%user%",user);
	}	
	public static String getLDAPAccount(String user){
		return Config.ldapAccount.replace("%user%",user);
	}	
	public static class syncIdStatus{
		private syncIdStatus(){}
		public final static String LOCK="LOCK",SUCC="SUCCESS",FAIL="FAIL", SYNC_UPD="SYNC-UPD";
	}
	
	
	public static class fileOperationStatus{
		private fileOperationStatus(){}
		
		public final static String PUT_OBJ="PUB_OBJ",GET_OBJ="GET_OBJ",DEL_OBJ="DEL_OBJ"
									,GETREMOTELY="GetRemotely",DELETELOCALLY="DeleteLocally";
		
	}
	
	public static class ERRORCODE{
		public final static String CONS_MISSING_PARAMETER="missing parameter",CONS_PARAMETER_NUMBER_CAN_NOT_IN_NEGTIVE="page or pagesize can not be in negtive";
	

		private ERRORCODE(){}
		
	}
}
