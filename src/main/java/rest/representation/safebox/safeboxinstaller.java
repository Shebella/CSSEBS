package rest.representation.safebox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.xml.bind.annotation.XmlRootElement;

import lib.DBTool;


@XmlRootElement
public class safeboxinstaller {
	public String version;
	
	public safeboxinstaller() throws SQLException, NamingException{
		
		Connection walruscon = null;
		ResultSet rs = null;

		try {
			walruscon = DBTool.getWalrusConnection();
			String query = "select versionID from installer_info order by release_date desc limit 1";
			PreparedStatement stmt = walruscon.prepareStatement(query);
			rs = stmt.executeQuery();
			if (rs.next()) {
				this.version=rs.getString(1);
			}

		}finally {
			try {rs.close();} catch (Throwable e) {}
			DBTool.closeConnection(walruscon);
		}
	}

}
