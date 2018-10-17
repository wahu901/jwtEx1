
import fish.payara.examples.model.TestData;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Peter.pan
 */
public class TestMain {
    public final static Logger logger = LoggerFactory.getLogger(TestMain.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        logger.debug("=== testJWTAuth ======================");
        String authStr = testJWTAuth();
        logger.debug("=== testJWT1 ======================");
        testJWT1(authStr);
        logger.debug("=== testJWT2 ======================");
        testJWT2(authStr);
        
        logger.debug("=== testJWTAuth2 ======================");
        Cookie cookie = testJWTAuth2();
        logger.debug("=== testJWT3 ======================");
        testJWT3(cookie);
    }
    
    /**
     * http://localhost:8080/jwtEx1/api/auth/login?name=payara&password=fish
     * http://localhost:8080/jwtEx1/api/sample/read
     * http://localhost:8080/jwtEx1/api/sample/write
     * http://localhost:8080/jwtEx1/api/sample/delete
     * @return 
     */
    public static String testJWTAuth(){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/jwtEx1/api/auth/login?name=payara&password=fish");
        Invocation.Builder reqBuilder = target.request();
        Response res = reqBuilder.get();
        int status = res.getStatus();
        String authStr = res.getHeaderString(HttpHeaders.AUTHORIZATION);
        String msg = res.readEntity(String.class);
        
        logger.debug("status = "+status);
        logger.debug("authStr = "+authStr);
        logger.debug("msg = "+msg);
        
        client.close();
        return authStr;
    }
    
    public static Cookie testJWTAuth2(){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/jwtEx1/api/auth/login?name=payara&password=fish&rememberme=true");
        Invocation.Builder reqBuilder = target.request();
        Response res = reqBuilder.get();
        int status = res.getStatus();
        String authStr = res.getHeaderString(HttpHeaders.AUTHORIZATION);
        String msg = res.readEntity(String.class);
        
        logger.debug("status = "+status);
        logger.debug("authStr = "+authStr);
        logger.debug("msg = "+msg);

        Cookie cookie = null;
        if( res.getCookies()!=null ){
            for(String key : res.getCookies().keySet()){
                logger.debug("Cookies Key = "+key);
                logger.debug("Cookies Name = "+res.getCookies().get(key).getName());
                logger.debug("Cookies Domain = "+res.getCookies().get(key).getDomain());
                logger.debug("Cookies Path = "+res.getCookies().get(key).getPath());
                logger.debug("Cookies Value = "+res.getCookies().get(key).getValue());
                logger.debug("Cookies Expiry = "+res.getCookies().get(key).getExpiry());
                logger.debug("Cookies MaxAge = "+res.getCookies().get(key).getMaxAge());
                
                if( "JREMEMBERMEID".equals(key) ){
                    cookie = res.getCookies().get(key);
                    logger.debug("authStr = "+res.getCookies().get(key).getValue());
                }
            }
        }
        
        client.close();
        return cookie;
    }
    
    public static void testJWT1(String authStr){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/jwtEx1/api/sample/write");
        Invocation.Builder reqBuilder = target.request().header(HttpHeaders.AUTHORIZATION, authStr);
        
        TestData entity = new TestData("p1", "p2");
        Response res = reqBuilder.post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
        
        int status = res.getStatus();
        String msg = res.readEntity(String.class);
        
        logger.debug("status = "+status);
        logger.debug("msg = "+msg);
        
        client.close();
    }
    
    public static void testJWT2(String authStr){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/jwtEx1/api/sample/delete");
        Invocation.Builder reqBuilder = target.request().header(HttpHeaders.AUTHORIZATION, authStr);
        Response res = reqBuilder.delete();
        
        int status = res.getStatus();
        String msg = res.readEntity(String.class);
        
        logger.debug("status = "+status);
        logger.debug("msg = "+msg);
        
        client.close();
    }
    
    public static void testJWT3(Cookie cookie){
        Client client = ClientBuilder.newBuilder().build();
        WebTarget target = client.target("http://localhost:8080/jwtEx1/api/sample/write");
        Invocation.Builder reqBuilder = target.request().cookie(cookie);
        
        TestData entity = new TestData("p1", "p2");
        Response res = reqBuilder.post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE));
        
        int status = res.getStatus();
        String msg = res.readEntity(String.class);
        
        logger.debug("status = "+status);
        logger.debug("msg = "+msg);
        
        client.close();
    }
}
