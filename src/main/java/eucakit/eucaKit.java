package eucakit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.internet.MimeUtility;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import lib.Config;
import lib.DBTool;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.log4j.Logger;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

public class eucaKit {
	private String DB_IP,EUCA_IP;
	private static Logger log=null;
	
	private String ak, sk;
	private static SecureRandom KEY_GENERATOR=null;

	private static void initStatic() throws IOException, NoSuchAlgorithmException{
		if(log==null){log=DBTool.getLogger("AccountService");}
		if(KEY_GENERATOR==null){KEY_GENERATOR = SecureRandom.getInstance("SHA1PRNG");}
	}

	public eucaKit(String EUCA_IP,String DB_IP) throws Exception {
		this.DB_IP=DB_IP;this.EUCA_IP=EUCA_IP;
		eucaKit.initStatic();
		this.ak = "WKy3rMzOWPouVOxK1p3Ar1C2uRBwa2FBXnCw";
		this.sk = this.getAdminSecretKey();
	}

	public String AddUser(String userName) throws Throwable {
		return this.sendURL(this.getAddUserURL(userName));
	}

	public String DeleteUser(String userName) throws Throwable {
		return this.sendURL(this.getDeleteUserURL(userName));
	}

	public String getKeyPair(Connection con, String userName) throws SQLException {
		String str = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			stmt = con
					.prepareStatement("select auth_user_name||'\t'||auth_user_query_id||'\t'||auth_user_secretkey from auth_users WHERE auth_user_name=?;");
			stmt.setString(1, userName);
			result = stmt.executeQuery();

			result.next();
			str = result.getString(1);

			if (!con.getAutoCommit()) {con.commit();}

		} finally {
			    try {result.close();} catch (Exception e) {e.printStackTrace();}			
				try {stmt.close();} catch (Exception e) {e.printStackTrace();}
		}

		return str;
	}

	public String getAdminSecretKey() throws NamingException, SQLException {
		String str = null;
		DataSource source = DBTool.getWalrusDataSource();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			con = source.getConnection();
			con.setAutoCommit(false);
			stmt = con.prepareStatement("select auth_user_secretkey from auth_users where auth_user_name='admin'");
			result = stmt.executeQuery();

			result.next();
			try{
				str = result.getString(1);
			}catch(SQLException sqle){
				throw new SQLException("query admin key and got no data.");
			}

			
			if (!con.getAutoCommit()) {
				con.commit();
			}
		}finally {
				try {result.close();} catch (Exception e) {e.printStackTrace();}
				try {stmt.close();} catch (Exception e) {e.printStackTrace();}
				try {con.close();} catch (Exception e) {e.printStackTrace();}
		}

		return str;
	}

	public void EnableUser(String userName) throws NamingException, SQLException {
		DataSource source = DBTool.getWalrusDataSource();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try {
			con = source.getConnection();
			con.setAutoCommit(false);
			stmt = con
					.prepareStatement("update AUTH_USERS set auth_user_is_enabled='true' where auth_user_name=?");
			stmt.setString(1, userName);
			stmt.executeUpdate();

			if (!con.getAutoCommit()) {
				con.commit();
			}

		}finally {
				try {result.close();} catch (Exception e) {e.printStackTrace();}
				try {stmt.close();} catch (Exception e) {e.printStackTrace();}
				try {con.close();} catch (Exception e) {e.printStackTrace();}
		}

	}

	/***
	 * By original approach first,revising after ,refactor code
	 * 
	 * @param user_name
	 * @return
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	synchronized public String genInstanceKey(String user_name, String hardware_id, String instance_name) throws SQLException, NamingException {

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet result = null;

		try { 

			String instanceKey = DigestUtils.shaHex(DigestUtils.sha256(String
					.valueOf(KEY_GENERATOR.nextInt()).getBytes()));

			con = DBTool.getWalrusDataSource().getConnection();
			con.setAutoCommit(false);
			stmt = con
					.prepareStatement("insert into CLIENT_INSTANCES (create_date, edit_date, hardware_id, instance_key, instance_name, is_enabled, user_id) values (?, ?, ?, ?, ?, ?, ?)");
			stmt.setTimestamp(1, new Timestamp((new Date()).getTime())); // create_date
			stmt.setTimestamp(2, new Timestamp((new Date()).getTime())); // edit_date
			stmt.setString(3, hardware_id); // hardware_id
			stmt.setString(4, instanceKey); // instance_key
			stmt.setString(5, instance_name); // instance_name
			stmt.setBoolean(6, true); // is_enabled
			stmt.setString(7, user_name); // user_id
			stmt.executeUpdate();

			if (!con.getAutoCommit()) {
				con.commit();
			}

			return instanceKey;

		}finally {
			try {
				if (null != result) {result.close();}
				if (null != stmt) {stmt.close();}
				if (null != con) {con.close();}
			} catch (SQLException e) {e.printStackTrace();}
		}
	}

	public boolean updateEmail(String userName,String email,Connection conn) throws SQLException{
		PreparedStatement stmt=null;
		
		try{
			stmt=conn.prepareStatement("update users set user_email=? where user_name=?");
			stmt.setString(1,email);
			stmt.setString(2,userName);
			stmt.executeUpdate();
			if (!conn.getAutoCommit()) {conn.commit();}
		}finally{
			stmt.close();
		}
		
		return true;
	}
	
	
	public String queryKey(String userName, String hardware_id, String instance_name,String newEmail) throws NamingException, SQLException {

		DataSource source = DBTool.getWalrusDataSource();
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String value = "";

		try {
			con = source.getConnection();
			if(newEmail!=null&&!newEmail.equals("")){this.updateEmail(userName, newEmail, con);}
			
			
			stmt = con
					.prepareStatement("select auth_user_name||'\t'||auth_user_query_id||'\t'||auth_user_secretkey from auth_users WHERE auth_user_name=?;");
			stmt.setString(1, userName);
			rs = stmt.executeQuery();
			if(rs.next()){value = rs.getString(1);}

			if (!con.getAutoCommit()) {con.commit();}

			if (null != value && !"".equals(value)) {
				value = value + String.format("%c", '\t')
						+ genInstanceKey(userName, hardware_id, instance_name);
			}

		}finally {
				try {rs.close();} catch (Exception e) {e.printStackTrace();}
				try {stmt.close();} catch(Exception e) {e.printStackTrace();}
				try {con.close();} catch (Exception e) {e.printStackTrace();}
		}

		return value;
	}

	public String sendURL(String url) throws Throwable {
		url = URLDecoder.decode(url);
		Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
		HttpConnectionManager hcm = new SimpleHttpConnectionManager();
		hcm.getParams().setSoTimeout(100000);
		hcm.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpRequestRetryHandler(0, false));

		HttpClient client = new HttpClient(hcm);

		HttpMethod method = new GetMethod();
		method.setURI(new URI(url));

		try{
			client.executeMethod(method);
		}catch(HttpException het){
			throw new Throwable("the Http request send to walrus has some error.");
		}catch(IOException ioe){
			throw new Throwable("Can not create socket connection to walrus.");
		}
		
		InputStream in = method.getResponseBodyAsStream();

		StringBuffer strbuf = new StringBuffer();
		byte[] data = new byte[1024];
		int datal = 0;
		while ((datal = in.read(data)) != -1) {
			String str = new String(data, 0, datal);
			strbuf.append(str);
		}
		in.close();
		method.releaseConnection();

		return strbuf.toString();

	}

	public String getDeleteUserURL(String userName) throws Exception {
		StringBuffer strbuf = new StringBuffer("https://");
		strbuf.append(this.EUCA_IP + ":8773/services/Accounts/?");
		strbuf.append("AWSAccessKeyId=" + this.ak + "&Action=DeleteUser&");
		strbuf.append("SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=");
		strbuf.append(this.getGMTTime() + "&");
		strbuf.append("UserName=" + userName + "&Version=eucalyptus");

		String url = strbuf.toString() + "&Signature=" + this.getSignature(strbuf.toString());
		return url;

	}

	public String getAddUserURL(String userName) throws Exception {

		StringBuffer strbuf = new StringBuffer("https://");
		strbuf.append(this.EUCA_IP + ":8773/services/Accounts/?");
		strbuf.append("AWSAccessKeyId=" + this.ak + "&Action=AddUser&Admin=False&");
		strbuf.append("Email=N%2FA&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=");
		strbuf.append(this.getGMTTime() + "&");
		strbuf.append("UserName=" + userName + "&Version=eucalyptus");

		String url = strbuf.toString() + "&Signature=" + this.getSignature(strbuf.toString());
		return url;
	}

	public String getSignature(String url) throws Exception {

		String[] urlA = url.replaceAll("https://", "").split("/");
		String host = urlA[0], para = urlA[3].replaceAll("\\?", "");

		String strToSign = "GET\n" + host + "\n/services/Accounts/\n" + para;

		String signStr = getHash(strToSign, this.sk);

		return signStr;
	}

	// ===================getGMTTime==================
	private static SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
	static {
		dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public String getGMTTime() {
		return dateFormatGmt.format(new Date()).replaceAll(":", "%3A");
	}

	// ===============================================

	private static String getHash(String strToSign, String sk) throws Exception {

		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKey key = new SecretKeySpec(sk.getBytes(), "HmacSHA256");
		sha256_HMAC.init(key);
		byte[] mac_data = sha256_HMAC.doFinal(strToSign.getBytes());

		return URLEncoder.encode(new String(base64Encode(mac_data)), "UTF-8");
	}

	private static byte[] base64Encode(byte[] b) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream b64os = MimeUtility.encode(baos, "base64");
		b64os.write(b);
		b64os.close();
		return baos.toByteArray();
	}

	private static byte[] base64Decode(byte[] b) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		InputStream b64is = MimeUtility.decode(bais, "base64");
		byte[] tmp = new byte[b.length];
		int n = b64is.read(tmp);
		byte[] res = new byte[n];
		System.arraycopy(tmp, 0, res, 0, n);
		return res;
	}

}
