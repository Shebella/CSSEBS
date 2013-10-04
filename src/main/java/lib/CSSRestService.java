package lib;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.UUID; 


import javax.naming.NamingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.http.HttpResponse;
import org.jets3t.service.*;
import org.jets3t.service.impl.rest.httpclient.RepeatableRequestEntity;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.*;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.security.ProviderCredentials;

import rest.Initaitor;

public class CSSRestService extends RestS3Service  {
	private ThreadLocal<Map> threadSafeParamMap=new ThreadLocal<Map>();
	private ThreadLocal<String> threadSafesyncId=new ThreadLocal<String>();
	
	public CSSRestService(ProviderCredentials credentials,Jets3tProperties jetS3) throws S3ServiceException {
		super(credentials,"",null,jetS3);
		this.threadSafeParamMap.set(new HashMap());
	}
	public S3Object cssRenameObject(S3Bucket bucket,S3Object object,String renameto,String account,String instanceId,String syncId) throws NoSuchAlgorithmException, IOException, S3ServiceException{
		this.threadSafesyncId.set(syncId);
		Map paramMap=this.threadSafeParamMap.get();
        paramMap.put("renameto",renameto);
        paramMap.put("account",account);
        paramMap.put("instanceid",instanceId);

		DBTool.getLogger("api_error").info("CSSRestService.cssRenameObject()-prepare to do putObject("+bucket+","+object+")");
        S3Object returnObj=this.putObject(bucket,object);
        this.threadSafesyncId.remove();
        this.threadSafeParamMap.get().clear();
       
        return returnObj;
	}

	
	@Override
	protected void performRequest(HttpMethodBase http_method, int[] arg1) throws ServiceException {
		if(this.threadSafesyncId.get()!=null){
			http_method.setRequestHeader("x-amz-meta-clientType",this.threadSafesyncId.get());
			http_method.setRequestHeader("x-amz-meta-reqId",UUID.randomUUID().toString());
		}
		super.performRequest(http_method, arg1);
	}

	protected StorageObject putObjectImpl(String bucketName,StorageObject object) throws ServiceException{
        boolean isLiveMD5HashingRequired =
            (object.getMetadata(StorageObject.METADATA_HEADER_CONTENT_MD5) == null);

        RequestEntity requestEntity = null;
        if (object.getDataInputStream() != null) {
            if (object.containsMetadata(StorageObject.METADATA_HEADER_CONTENT_LENGTH)) {
                requestEntity = new RepeatableRequestEntity(object.getKey(),
                    object.getDataInputStream(), object.getContentType(), object.getContentLength(),
                    this.jets3tProperties, isLiveMD5HashingRequired);
            } else {
                requestEntity = new InputStreamRequestEntity(
                    object.getDataInputStream(), InputStreamRequestEntity.CONTENT_LENGTH_AUTO);
            }
        }
        putObjectWithRequestEntityImpl(bucketName, object, requestEntity,this.threadSafeParamMap.get());
        
		return object;
		
		
	}
	
	
	
	/*
	protected HttpResponse performRequest(HttpUriRequest httpMethod, int[] expectedResponseCodes)
			throws ServiceException {
		httpMethod.addHeader("x-amz-meta-clientType","1");
		return super.performRequest(httpMethod, expectedResponseCodes);
	}
	
    protected StorageObject putObjectImpl(String bucketName, StorageObject object) throws ServiceException
    {

        HttpEntity requestEntity = null;

        if (object.getDataInputStream() != null) {
            if (object.containsMetadata(StorageObject.METADATA_HEADER_CONTENT_LENGTH)) {
                requestEntity = new RepeatableRequestEntity(
                        object.getKey(),
                        object.getDataInputStream(),
                        object.getContentType(),
                        object.getContentLength(),
                        this.jets3tProperties,
                        isLiveMD5HashingRequired(object));
             } else {
                // Use a BufferedHttpEntity for objects with an unknown content length, as the
                // entity will cache the results and doesn't need to know the data length in advance.
                BasicHttpEntity basicHttpEntity = new BasicHttpEntity();
                basicHttpEntity.setContent(object.getDataInputStream());
                try {
                    requestEntity = new BufferedHttpEntity(basicHttpEntity);
                } catch (IOException ioe) {
                    throw new ServiceException("Unable to read data stream of unknown length", ioe);
                }
            }
        }
        Map<String,String> paramMap=new HashMap<String,String>();
        paramMap.put("renameto","test2.dat/test3.dat");

        paramMap.put("account","User1");
        paramMap.put("instanceid","test");
        putObjectWithRequestEntityImpl(bucketName, object, requestEntity,paramMap);

        return object;
    }
	
	*/
	
	

}

class S3TestClient{
	public CSSRestService s3Service;
	private AWSCredentials awsCredentials;
	

	public S3TestClient(String host,String ak,String sk) throws S3ServiceException, SQLException, NamingException{
		this.s3Service =DBTool.getCSSRestService();
	}
	
	public void putObject(String bucketName,String objectKey,String filePath) throws IOException, ServiceException, NoSuchAlgorithmException{
		S3Bucket buk=null;
		S3Object s3obj=null;
		try{
			buk=new S3Bucket(bucketName);
			s3obj=new S3Object(new File(filePath));
			s3obj.setKey(objectKey);
			this.s3Service.putObject(buk,s3obj);
		} catch (Throwable t) {
			t.printStackTrace();
		}finally{
			this.closeS3ObjectStream(s3obj);
		}
		
	}


	public void renameObject(String bucketName,String objectKey,String renameto,String account,String instanceId,String syncId) throws S3ServiceException, NoSuchAlgorithmException, IOException{
        
        S3Bucket s3buck=new S3Bucket(bucketName);
        S3Object s3obj=new S3Object(objectKey);
        //s3obj.setKey(objectKey);
		
		
		this.s3Service.cssRenameObject(s3buck,s3obj, renameto, account, instanceId, syncId);
		
		try {
			InputStream ins=s3obj.getDataInputStream();
			if(ins!=null)ins.close();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	
	
	public void createBucket(String bucketName) throws S3ServiceException{
		S3Bucket buk=new S3Bucket(bucketName);
		s3Service.createBucket(bucketName);
	}
	
	private void closeS3ObjectStream(S3Object s3obj) throws ServiceException, IOException{
		s3obj.closeDataInputStream();		
	}
	
	
}
