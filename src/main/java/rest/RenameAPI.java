package rest;

import lib.CSSRestService;
import lib.Config;
import lib.DBTool;
import lib.QueueManager;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import rest.exception.EBSException;
import rest.representation.RenameObjectResponse;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: mac
 * Date: 2013/5/23
 * Time: 上午 9:46
 * To change this template use File | Settings | File Templates.
 */
public class RenameAPI {

    public RenameObjectResponse renameObject(@QueryParam("account") String account,
                                             @QueryParam("instanceid") String instanceid, @QueryParam("syncid") Integer syncid,
                                             @QueryParam("src_object") String src_object, @QueryParam("dest_object") String dest_object,
                                             @QueryParam("bucket_name") String bucket_name) throws EBSException {
try{


        RenameObjectResponse eventResp = new RenameObjectResponse();


        if (account == null || instanceid == null || syncid == null || null == src_object
                || null == dest_object || null == bucket_name) {
//0009

            eventResp.addError("0009");
            return eventResp;
        }

        if ("webApp".equals(instanceid)) {
        } else if ("mobileApp".equals(instanceid)) {
        } else {
            QueueManager qm = EBSAPI.getQueuesMap().getQueueManager(account);

            if (!qm.isLocked(instanceid)) {
//0801  
                eventResp.setMessage("need to get lock fisrt");
                eventResp.addError("0801");
                return eventResp;
            }
        }

        //=====================
        eventResp.setSuccess(true);
        try{
            S3Bucket s3buck=new S3Bucket(bucket_name);
            S3Object s3obj=new S3Object(src_object);

            CSSRestService cssService=DBTool.getCSSRestService(account);
//0008
            if(cssService==null){
                  DBTool.getLogger("api_error").info("DBTool.getCSSRestService() return null");
                eventResp.addError("0008");
                return eventResp;
            }

            cssService.cssRenameObject(s3buck,s3obj,dest_object,account,instanceid,syncid.toString());

            InputStream ins=null;
            try {
                ins=s3obj.getDataInputStream();
//0008
            } catch (ServiceException e) {
            	DBTool.getLogger("api_error").info(e.toString());
            	
            	if(e.getErrorCode().equals("403")&&e.getErrorMessage().contains("TooManyRequests")){
            		eventResp.addError("0010","TooManyRequests");
            	}else {
            		eventResp.addError("0008");
            	}
                return eventResp;
            }finally{
            	if(ins!=null)ins.close();
            	
            }


        }catch(Throwable t){
            DBTool.getLogger("api_error").info(t.toString());
            eventResp.setMessage(t.getMessage());
//0008
            eventResp.setSuccess(false);
            eventResp.addError("0008");
            return eventResp;
        }
        return eventResp;
}catch(Throwable t){
	RenameObjectResponse r=null;
	
    try{DBTool.getLogger("api_error").info(t.toString());}catch(Throwable t2){/*can not record log ,not thing here.*/}
    
    r.setMessage(t.getMessage());
//0008
    r.setSuccess(false);
    r.addError("0008");
    return  r;
}
    }
    
    
}
