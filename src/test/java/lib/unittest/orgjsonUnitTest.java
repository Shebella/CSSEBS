package lib.unittest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.http.client.methods.HttpHead;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;


public class orgjsonUnitTest {
	
	private String[][] strA=new String[][]{
			{"existingevents","GET","account=User1&instanceid=2&lastsyncid=5"},
			{"syncrequest","PUT","account=User1&instanceid=2"},
			{"queryevent","GET","account=User1&instanceid=2&thisysncid=5&lastsyncid=3"},
			{"unlock","PUT","account=User1&instanceid=2&result=SUCC"},
			{"querylastsyncid","GET","account=User1&instanceid=2"},
			{"querybucketinfo","GET","account=User1&bucketname=Bucket1"},
			{"heartbeat","POST","account=User1&instanceid=2&thisysncid=5"}
	};
	
	@Test
	public void checkNULLValue() throws JSONException{
		String response=null;
		JSONObject json=null;
		
		
		for(int i=0;i<strA.length;i++){
			try{
			
				response=SendRequest(strA[i][0],strA[i][1],strA[i][2]);
				json=new JSONObject(response);
				
				String[] args=json.getNames(json);
				
				
				assert(json.optString("result")!=null);
				
				
				
				
			}catch(Throwable t){
				t.printStackTrace();
				
			}

		}
		
		
		
		
	}

	
	
	private static HttpClient client=new HttpClient();
	private static String serviceURL="http://10.218.43.1";
	private String SendRequest(String EBS_API, String httpMethod,String queryString) {

		String result = null;

		// if (!JNotifyHandler.getInstance().isRunning())
		// return result;

		String URL = serviceURL + "/sbx_svr/rest/EBS/" + EBS_API+"/";
		HttpMethod method = null;

		try {
			// client = new HttpClient();
			if (httpMethod.compareToIgnoreCase("get") == 0) {
				method = new GetMethod(URL);
			} else if (httpMethod.compareToIgnoreCase("put") == 0) {
				method = new PutMethod(URL);
			} else if (httpMethod.compareToIgnoreCase("post") == 0) {
				method = new PostMethod(URL);
				//method.addRequestHeader(new Header("Connection", "close"));
			}
			
				if (null != queryString) {
					method.setQueryString(queryString);
				}

				if (EBS_API.compareToIgnoreCase("addevent") == 0) {}

				// Execute the method.
				int statusCode = client.executeMethod(method);

				if (statusCode == HttpStatus.SC_OK) {
					// Read the response body.
					StringBuffer stb = new StringBuffer();

					InputStream ins = method.getResponseBodyAsStream();
					InputStreamReader insReader = new InputStreamReader(ins);
					BufferedReader br = new BufferedReader(insReader);
					String buffText = br.readLine();
					while (null != buffText) {
						stb.append(buffText);
						buffText = br.readLine();
					}
					

					
					result = stb.toString();

				} else {
					System.out.println("HttpStatusCode:"+ statusCode +" Method failed: " + method.getStatusLine());
				}
		}catch(Throwable t){
			t.printStackTrace();
			
			
		} finally {
			// Release the connection.
			if (method != null) {
				method.releaseConnection();
			}
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
}
