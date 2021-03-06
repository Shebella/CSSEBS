package rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
 
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
 
public class ResponseCorsFilter implements ContainerResponseFilter {
 
    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
 
        ResponseBuilder builder = Response.fromResponse(response.getResponse());
        builder.header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
 
        String requestHeaders = request.getHeaderValue("Access-Control-Request-Headers");
 
        if (null != requestHeaders && !requestHeaders.equals("")) {
        	builder.header("Access-Control-Allow-Headers", requestHeaders);
        }
 
        response.setResponse(builder.build());
        
        return response;
    }
 
}
