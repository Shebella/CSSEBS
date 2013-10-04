package rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import rest.TESTAPI;

public class FakeTest {


	private static String tmpFile="/tmp/fakeresponse.cfg";
	public final Map<String,String> fakeValues=initFakeValues();
	
	private Map<String,String> initFakeValues(){
		Map<String,String> map=new HashMap<String,String>();
		
		try {
			BufferedReader fw=new BufferedReader(new FileReader(new File(tmpFile)));
			String line=null;
			while((line=fw.readLine())!=null){
				String[] v=line.split("=");
				if(v.length<2)continue;
				String key=v[0].trim(),value=v[1].trim();
				map.put(key, value);
			}
			
			fw.close();
		} catch (IOException e) {
			return new HashMap<String,String>();
		}
		
		return map;
	}
	
	private boolean writeFakeValues(){
		
		try {
			FileWriter fw=new FileWriter(new File(tmpFile));
			Iterator<String> it=fakeValues.keySet().iterator();
			while(it.hasNext()){
				String key=it.next(),value=fakeValues.get(key);
				if(value!=null)fw.write(key+"="+value+"\n");
			}
			
			fw.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public String put(String key,String value){
		
		this.fakeValues.put(key,value);
		this.writeFakeValues();
		
		
		return key+"="+this.fakeValues.get(key);
	}
	
	
	public String get(String key){
		return this.fakeValues.get(key);
	}
	
}
;