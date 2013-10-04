package rest;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import rest.representation.safebox.safeboxinstaller;

import com.sun.jersey.spi.resource.Singleton;




@Path("/Safebox")
@Singleton
public class SafeboxClientAPI {
	
	
	@Path("lastversion")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public safeboxinstaller getLastVersionInstaller() throws SQLException, NamingException{
		return new safeboxinstaller();
	}
	
	
	@Path("installer")
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public safeboxinstaller putInstaller(
			@QueryParam("versionID") String versionID,
			@QueryParam("svnVersion") String svnVersion,
			@QueryParam("buildNumber") String buildNumber,
			@QueryParam("path") String path,
			@QueryParam("releaseTime") String releaseTime
			) throws SQLException, NamingException{
		return new safeboxinstaller();
	}
	
}
