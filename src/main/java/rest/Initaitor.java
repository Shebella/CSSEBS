package rest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lib.Config;
import lib.DBTool;




public class Initaitor implements ServletContextListener{

	
	
	public void contextDestroyed(ServletContextEvent ce) {
	}	

	

	public static Properties loadEbsConfig(String path) throws IOException{
		Properties pro=new Properties();
		FileInputStream fin=null;
		
		try {
			fin=new FileInputStream(path);
			pro.load(fin);
		}finally{
			fin.close();
		}
		return pro;
	}

	
	public void contextInitialized(ServletContextEvent ce) {
		
		try {
			Config.ebsPorperties=this.loadEbsConfig(ce.getServletContext().getRealPath("WEB-INF/classes/ebs.properties"));
			Config.EUCA_IP=ce.getServletContext().getInitParameter("EUCA_IP");
			Config.DB_IP=ce.getServletContext().getInitParameter("DB_IP");
			Config.LDAP=ce.getServletContext().getInitParameter("LDAP");
			Config.ldapUrl=ce.getServletContext().getInitParameter("ldapUrl");
			Config.ldapAccount=ce.getServletContext().getInitParameter("ldapAccount");
			
			DBTool.getLogger("Initaitor").info("Initiate ebs...");
			DBTool.getLogger("Initaitor").info("dbMaxconnect="+Config.ebsPorperties.getProperty("dbMaxCon"));
			DBTool.getLogger("Initaitor").info("MaxEventLimit="+Config.ebsPorperties.getProperty("maxEventsLimit"));
			DBTool.getLogger("Initaitor").info("RUNAWAYDAYS="+Config.ebsPorperties.getProperty("RUNAWAYDAYS"));
			DBTool.getLogger("Initaitor").info("timeoutValueinmillSecond="+Config.ebsPorperties.getProperty("timeoutValueinmillSecond"));
			
		}catch (IOException e) {
			try {DBTool.getLogger("Initaitor").info("Initaitor error:"+e);}catch (IOException e1) {}
		}
		
		
		
	}
	
	

}
