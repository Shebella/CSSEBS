package rest.representation;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rest.exception.EBSException;

import lib.Config;
import lib.Config.ERRORCODE;
import lib.DBTool;

public class BaseResponse {
	public static final boolean ERROR=false;
	
	public List<ErrorMessage> errors=new LinkedList<ErrorMessage>();
	
	public BaseResponse(){}

	public boolean result=true;
	
	
	public void addError(String code,String msg){

		
		try{
			StackTraceElement[] i=Thread.currentThread().getStackTrace();
			String errorMsg=i[2].getLineNumber()+"@"+i[2].getClassName()+"."+i[2].getMethodName()+":error code="+code;
			DBTool.recordLog("errorcode",errorMsg);
		}catch(Throwable t){
			DBTool.recordLog("errorcode",t.toString());
		}
		
		
		this.result=ERROR;
		ErrorMessage em=new ErrorMessage(code,UUID.randomUUID().toString());
		em.message=msg;
		this.errors.add(em);
		
		
	}
	
	
	
	
	public void addError(String code){
		
		try{
			StackTraceElement[] i=Thread.currentThread().getStackTrace();
			String errorMsg=i[2].getLineNumber()+"@"+i[2].getClassName()+"."+i[2].getMethodName()+":error code="+code;
			DBTool.recordLog("errorcode",errorMsg);
		}catch(Throwable t){
			DBTool.recordLog("errorcode",t.toString());
		}
		
		
		this.result=ERROR;
		this.errors.add(new ErrorMessage(code,UUID.randomUUID().toString()));
	}
	
	public void addError(String code,Throwable  t){
		
		try{
			StackTraceElement[] i=t.getStackTrace();
			String errorMsg=i[0].getLineNumber()+"@"+i[0].getClassName()+"."+i[0].getMethodName()+":error code="+code;
			DBTool.recordLog("errorcode",errorMsg);
		}catch(Throwable t2){
			DBTool.recordLog("errorcode",t2.toString());
		}
		
		
		this.result=ERROR;
		this.errors.add(new ErrorMessage(code,UUID.randomUUID().toString()));
	}
	
	
	
	
	
}




