package lib.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lib.DBTool;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

public class DBToolTest {
	
	@Test
	public void Test_getConnection() throws NamingException, SQLException{
		Connection con=DBTool.getConnection();
		DBTool.closeConnection(con);
		if(con==null||!con.isClosed()){assertFalse(true);}
		
		
		
		
		
		con=DBTool.getWalrusConnection();
		DBTool.closeConnection(con);
		if(con==null||!con.isClosed()){assertFalse(true);}
	}
	
	@Test
	public void Test_Logger() throws SQLException, NamingException, IOException{
		Logger logger=DBTool.getLogger("testLog");
		logger.getEffectiveLevel();
		logger.getAdditivity();
		logger.getLevel();
	}
	
	
	@Test
	public void Test_UTC(){
		DBTool.getUTC0();
	}
	
	
	
	
	
	
	
}
