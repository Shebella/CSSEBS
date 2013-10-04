package lib;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.jersey.api.json.*;
import com.sun.jersey.api.json.JSONConfiguration.MappedBuilder;
import com.sun.jersey.spi.inject.Errors.ErrorMessage;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;

import org.jets3t.service.model.S3Object;

import rest.representation.BaseResponse;
import rest.representation.BucketInfo;
import rest.representation.Event;
import rest.representation.FolderInfo;
import rest.representation.HeartbeatResponse;
import rest.representation.ObjectList;
import rest.representation.ObjectsNumofUserResponse;
import rest.representation.RenameObjectResponse;
import rest.representation.SystemInfo;
import rest.representation.UserInfo;
import rest.representation.addEventResponse;
import rest.representation.changeSyncIdStatusResponse;
import rest.representation.confirmsyncResponse;
import rest.representation.existingeventsResponse;
import rest.representation.getObjectsMD5Response;
import rest.representation.pageInfo;
import rest.representation.queryVersionResponse;
import rest.representation.queryeventResponse;
import rest.representation.querylastsyncidResponse;
import rest.representation.querylastsyncseq4mreadResponse;
import rest.representation.syncRequestResponse;
import rest.representation.unLockResponse;

//This class is for resolve json array format issue.
//see ContextResolver  in document of jersey. 
@Provider
public class EBSContextResolver implements ContextResolver<JAXBContext> {

	
	
    private JAXBContext context;
    private Set<Class> types = new HashSet<Class>();

    public EBSContextResolver() throws Exception {

    	
        MappedBuilder builder = JSONConfiguration.mapped();
        builder.rootUnwrapping(true);
        builder.arrays("record");
        builder.arrays("objBeanList");
        builder.arrays("errors");
        
        
        
        
        types.add(queryeventResponse.class);
        types.add(ObjectList.class);
        types.add(getObjectsMD5Response.class);
        types.add(existingeventsResponse.class);
       //types.add(BaseResponse.class);
        types.add(addEventResponse.class);
        types.add(BucketInfo.class);
        types.add(changeSyncIdStatusResponse.class);
        types.add(confirmsyncResponse.class);
        types.add(Event.class);
        types.add(FolderInfo.class);
        types.add(getObjectsMD5Response.class);
        types.add(HeartbeatResponse.class);
        types.add(ObjectList.class);
        types.add(ObjectsNumofUserResponse.class);
        types.add(pageInfo.class);
        types.add(queryeventResponse.class);
        types.add(querylastsyncidResponse.class);
        types.add(queryeventResponse.class);
        types.add(querylastsyncseq4mreadResponse.class);
        types.add(queryVersionResponse.class);
        types.add(RenameObjectResponse.class);
        //types.add(S3Object.class);
        types.add(syncRequestResponse.class);
        types.add(SystemInfo.class);
        types.add(unLockResponse.class);
        types.add(UserInfo.class);
        
        Class[] cla=new Class[types.size()];
        this.context = new JSONJAXBContext(builder.build(), types.toArray(cla));
    }
    
    
    public JAXBContext getContext(Class<?> objectType) {
    	
        return  types.contains(objectType)? context : null;

    }

}
