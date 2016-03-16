package objectSaver;

import db.ConnectionParam;
import db.MasterDBConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import mq.RequestPriorityQueueCustomProxy;
import mq.RequestPriorityQueueNoProxy;
import mq.RequestPriorityQueueRandomProxy;
import mq.RequestPriorityQueueRobinProxy;
import mq.ResultQueue;
import proxy.DBCustomProxy;
import proxy.DBRandomProxy;
import proxy.DBRobinProxy;
import db.SqlRequest;
import serializer.ObjectSerializer;
import storage.MyClass;

/**
 *
 * @author benjamin
 */
@Path("/mq")
public class MQRestServer {
    
    DBRandomProxy randomInstance = new DBRandomProxy();
    MyClass randomProx;
    DBRobinProxy robinInstance = new DBRobinProxy();
    MyClass robinProx;
    DBCustomProxy customInstance = new DBCustomProxy();
    MyClass customProx;
    
    private static final String queryPriority1 = "SELECT release_year FROM ptidejdb.film where film_id = 500;";
    private static final String queryPriority2 = "SELECT ptidejdb.inventory_held_by_customer(9);";
    private static final String queryPriority3 = "SELECT R.customer_id, COUNT(*) AS cnt "
            + "FROM ptidejdb.rental R "
            + "LEFT JOIN ptidejdb.inventory I ON R.inventory_id = I.inventory_id "
            + "LEFT JOIN ptidejdb.film F ON I.film_id = F.film_id "
            + "LEFT JOIN ptidejdb.film_category FC on F.film_id = FC.film_id "
            + "LEFT JOIN ptidejdb.category C ON FC.category_id = C.category_id "
            + "WHERE C.name = \"Horror\" "
            + "GROUP BY R.customer_id HAVING cnt > 4;";
    private static final String queryPriority4 = "SELECT film.release_year, CONCAT(customer.last_name, "
            + "', ', customer.first_name) AS customer, address.phone, film.title "
            + "FROM rental "
            + "INNER JOIN customer ON rental.customer_id = customer.customer_id "
            + "INNER JOIN address ON customer.address_id = address.address_id "
            + "INNER JOIN inventory ON rental.inventory_id = inventory.inventory_id "
            + "INNER JOIN film ON inventory.film_id = film.film_id "
            + "WHERE rental.return_date IS NULL AND "
            + "rental_date + INTERVAL film.rental_duration DAY < CURRENT_DATE();";
    
    private static final String complexInsert = "SELECT release_year FROM sakila.film where film_id = 50;";

    private static final String[] sampleQueries = {queryPriority1,queryPriority2};

    public MQRestServer() {
        randomProx = (MyClass) randomInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
        robinProx = (MyClass) robinInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
        customProx = (MyClass) customInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
    }
    
    @POST
    @Path("/placeIntoMessageQueueCustomProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response PlaceIntoMQCustomProxy() {
        try {
            Random r = new Random();
            int queryId = r.nextInt(2);
            UUID uid = UUID.randomUUID();
            
//            RequestQueueCustomProxy.getInstance().AddRequest(uid, 
//                    new SqlRequest(uid, sampleQueries[queryId], queryId+1));
//            RequestQueueCustomProxy.getInstance().ProcessRequests();
            
            RequestPriorityQueueCustomProxy.getInstance().AddRequest(uid, 
                    new SqlRequest(uid, complexInsert, 1));
            RequestPriorityQueueCustomProxy.getInstance().ProcessRequests();

            return Response.ok(uid.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/placeIntoMessageQueueRobinProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response PlaceIntoMQRobinProxy() {
        try {
            Random r = new Random();
            int queryId = r.nextInt(2);
            UUID uid = UUID.randomUUID();
            
            RequestPriorityQueueRobinProxy.getInstance().AddRequest(uid, 
                    new SqlRequest(uid, complexInsert, 1));
            RequestPriorityQueueRobinProxy.getInstance().ProcessRequests();
            
//            RequestQueueRobinProxy.getInstance().AddRequest(uid, 
//                    new SqlRequest(uid, sampleQueries[queryId], queryId+1));
//            RequestQueueRobinProxy.getInstance().ProcessRequests();

            return Response.ok(uid.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/placeIntoMessageQueueRandomProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response PlaceIntoMQRandomProxy(String requestObject) {
        try {
            Random r = new Random();
            int queryId = r.nextInt(2);
            UUID uid = UUID.randomUUID();
            
            RequestPriorityQueueRandomProxy.getInstance().AddRequest(uid, 
                    new SqlRequest(uid, complexInsert, 1));
            RequestPriorityQueueRandomProxy.getInstance().ProcessRequests();
            
//            RequestQueueRandomProxy.getInstance().AddRequest(uid, 
//                    new SqlRequest(uid, sampleQueries[queryId], queryId+1));
//            RequestQueueRandomProxy.getInstance().ProcessRequests();

            return Response.ok(uid.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/placeIntoMessageQueueNoProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response PlaceIntoMQNoProxy(String requestObject) {
        try {
            Random r = new Random();
            int queryId = r.nextInt(2);
            UUID uid = UUID.randomUUID();
            
            RequestPriorityQueueNoProxy.getInstance().AddRequest(uid, 
                    new SqlRequest(uid, complexInsert, 1));
            RequestPriorityQueueNoProxy.getInstance().ProcessRequests();
            
//            RequestQueueNoProxy.getInstance().AddRequest(uid, 
//                    new SqlRequest(uid, sampleQueries[queryId], queryId+1));
//            RequestQueueNoProxy.getInstance().ProcessRequests();

            return Response.ok(uid.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/getFromMessageQueue")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetFromMQ(String uid) {
        try {
            UUID id = UUID.fromString(ObjectSerializer.deserialize(uid).toString());
            Object o = null;
            do {
                o = ResultQueue.getQueue().get(id);
            } while (o == null);
            ResultSet rs = (ResultSet) o;
            ResultQueue.getQueue().remove(id);

            int number = -1;
        
            if (rs.next()) {
                number = rs.getInt(1);
            }

            if (number == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(number).build();
            }
            return Response.ok().entity(number).build();
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(ProxyRestServer.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/getAvgCustom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetAvgFromDbCustom() {
        try {
            Random r = new Random();
            int id = r.nextInt(3);
            
            ResultSet rs = customProx.query(sampleQueries[id]);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("charabia.uqam.ca", 3306, "ptidejdb", "ptidej",
                                "latece", "com.mysql.jdbc.Driver", true)).query(sampleQueries[id]);

                if (res.next()) {
                    result = rs.getInt(1);
                }
            }

            if (result == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(result).build();
            }
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/getAvgRandom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetAvgFromDbRandom() {
        try {
            Random r = new Random();
            int id = r.nextInt(3);
            
            ResultSet rs = randomProx.query(sampleQueries[id]);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("charabia.uqam.ca", 3306, "ptidejdb", "ptidej",
                                "latece", "com.mysql.jdbc.Driver", true)).query(sampleQueries[id]);

                if (res.next()) {
                    result = rs.getInt(1);
                }
            }

            if (result == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(result).build();
            }
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/getAvgRobin")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetAvgFromDbRobin() {
        try {
            Random r = new Random();
            int id = r.nextInt(3);
            
            ResultSet rs = robinProx.query(sampleQueries[id]);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("charabia.uqam.ca", 3306, "ptidejdb", "ptidej",
                                "latece", "com.mysql.jdbc.Driver", true)).query(sampleQueries[id]);

                if (res.next()) {
                    result = rs.getInt(1);
                }
            }

            if (result == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(result).build();
            }
            return Response.ok().entity(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
}
