package lib.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.junit.BeforeClass;
import org.junit.Test;





public class HttpTest {


	private String host="127.0.0.1";
	static HttpClient client=null;
	
	
	private static String send(HttpClient client,HttpMethod method) throws HttpException, IOException{
			client.executeMethod(method);
			BufferedReader ins2=null;
			StringBuffer strb=new StringBuffer();
			
			
			try{
				ins2=new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
				String str2=null;
				while((str2=ins2.readLine())!=null){
					strb.append(str2);
				}
			}finally{
				ins2.close();
			}
			method.releaseConnection();
			
			return strb.toString();
			
	}
	
	@BeforeClass
	public static void Prepare(){
		 
		MultiThreadedHttpConnectionManager mth=new MultiThreadedHttpConnectionManager();
			mth.getParams().setMaxTotalConnections(1000);
			mth.getParams().setDefaultMaxConnectionsPerHost(1000);
			mth.getParams().setStaleCheckingEnabled(false);
		client=new HttpClient(mth);
			client.getHttpConnectionManager().getParams().setTcpNoDelay(true);
		
	}
	
	
	@Test
	public void testA() throws HttpException, IOException{
		PostMethod method2=new PostMethod();
		method2.setURI(new URI("http://"+host+"/sbx_svr/rest/EBS/renameobject?account=User1&instanceid=test&syncid=1&src_object=folder1234&dest_object=folderabc&bucket_name=test2"));
		send(client,method2);
		
		method2.setURI(new URI("http://"+host+"/sbx_svr/rest/EBS/renameobject?account=User1&instanceid=test&syncid=1&src_object=folderabc&dest_object=folder1234&bucket_name=test2"));
		send(client,method2);
	}
	
	
	
	
	
	
	
	
	
}