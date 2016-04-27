package objectSaver;

import db.ConnectionParam;
import db.MasterDBConnection;
import db.SlaveDBConnection;
import java.net.HttpURLConnection;
import java.sql.ResultSet;
import java.util.Random;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import proxy.DBCustomProxy;
import proxy.DBRandomProxy;
import proxy.DBRobinProxy;
import serializer.ObjectSerializer;
import storage.MyClass;

@Path("/db")
public class ProxyRestServer {

    private static ProxyRestServer instance = new ProxyRestServer();
    DBRandomProxy randomInstance = new DBRandomProxy();
    MyClass randomProx;
    DBRobinProxy robinInstance = new DBRobinProxy();
    MyClass robinProx;
    DBCustomProxy customInstance = new DBCustomProxy();
    MyClass customProx;

    private static final String complexSelect = "SELECT CONCAT(customer.last_name, "
            + "', ', customer.first_name) AS customer, address.phone, film.title "
            + "FROM rental INNER JOIN customer ON rental.customer_id = customer.customer_id "
            + "INNER JOIN address ON customer.address_id = address.address_id "
            + "INNER JOIN inventory ON rental.inventory_id = inventory.inventory_id "
            + "INNER JOIN film ON inventory.film_id = film.film_id "
            + "WHERE rental.return_date IS NULL AND "
            + "rental_date + INTERVAL film.rental_duration DAY < CURRENT_DATE();";
    
    private static final String complexInsert = "INSERT INTO `ptidejdb`.`film` "
            + "(`title`, `description`, `release_year`, `language_id`, "
            + "`rental_duration`, `rental_rate`, `length`, `replacement_cost`, "
            + "`rating`, `special_features`) VALUES ('EXPENDABLE 3', "
            + "'An action film with the bests actors ever', 2014, '1', '3', "
            + "'8.99', '150', '26.99', 'R', 'Trailers');";
    
    private static final String queryPriority1 = "SELECT release_year FROM sakila.film where film_id = 50;";
    private static final String queryPriority2 = "SELECT ptidejdb.inventory_held_by_customer(9);";
    private static final String queryPriority3 = "SELECT R.customer_id, COUNT(*) AS cnt "
            + "FROM ptidejdb.rental R LEFT JOIN ptidejdb.inventory I ON R.inventory_id = I.inventory_id "
            + "LEFT JOIN ptidejdb.film F ON I.film_id = F.film_id "
            + "LEFT JOIN ptidejdb.film_category FC on F.film_id = FC.film_id "
            + "LEFT JOIN ptidejdb.category C ON FC.category_id = C.category_id "
            + "WHERE C.name = \"Horror\" "
            + "GROUP BY R.customer_id HAVING cnt > 4;";
    private static final String queryPriority4 = "SELECT film.release_year, CONCAT(customer.last_name, "
            + "', ', customer.first_name) AS customer, address.phone, film.title "
            + "FROM rental INNER JOIN customer ON rental.customer_id = customer.customer_id "
            + "INNER JOIN address ON customer.address_id = address.address_id "
            + "INNER JOIN inventory ON rental.inventory_id = inventory.inventory_id "
            + "INNER JOIN film ON inventory.film_id = film.film_id "
            + "WHERE rental.return_date IS NULL AND "
            + "rental_date + INTERVAL film.rental_duration DAY < CURRENT_DATE();";

    private static final String[] sampleQueries = {queryPriority1,queryPriority2,queryPriority3,queryPriority4};

    private int counter = 0;
    private long lastCheck = 0;
    
    public ProxyRestServer() {
        randomProx = (MyClass) randomInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
        robinProx = (MyClass) robinInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
        customProx = (MyClass) customInstance.newInstance(
                MyClass.class, new Class[]{MyClass.class});
    }
    
    private String BuildQuery(int id){
        return String.format("SELECT inventory_held_by_customer(%d);", id);
    }

    @GET
    @Path("/test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testRest() {
        return Response.ok().entity("Just for test - ProxyRestServer/db").build();

    }

    @POST
    @Path("/saveSimpleCustom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response SaveSimple(String param) {
        try {
//            String val = (String) ObjectSerializer.deserialize(param);
            ResultSet res = customProx.query("INSERT INTO sakila.film VALUES (4,'Test','Un test',2014,1,1,6,'0.99',86,'20.99','R','Trailers','2006-02-15 05:03:42')");

            int id = -1;
            if (res.next()) {
                id = res.getInt(1);
            }
            return Response.ok().entity(id).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }

    }

    @POST
    @Path("/saveComplexeCustom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response SaveComplexe() {
        try {
            ResultSet res = customProx.query(complexInsert);

            int id = -1;
            if (res.next()) {
                id = res.getInt(1);
            }
            return Response.ok().entity(id).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }

    }

    @POST
    @Path("/saveSimpleNoProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response SaveSimpleNoProxy(String param) {
        try {
            String req = "INSERT INTO sakila.film VALUES (3,'Test','Un test',2014,1,1,6,'0.99',86,'20.99','R','Trailers','2006-02-15 05:03:42')";
            ResultSet res = MasterDBConnection.getDbCon(
                    new ConnectionParam("host_ip", 3306, "sakila", "root",
                            "pass", "com.mysql.jdbc.Driver", true)).insert(req);

            int id = -1;
            if (res.next()) {
                id = res.getInt(1);
            }

            return Response.ok().entity(id).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }

    }

    @POST
    @Path("/saveComplexNoProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response SaveComplexeNoProxy() {
        try {
            ResultSet res = MasterDBConnection.getDbCon(
                    new ConnectionParam("host_ip", 3306, "sakila", "root",
                            "pass", "com.mysql.jdbc.Driver", true)).insert(complexInsert);

            int id = -1;
            if (res.next()) {
                id = res.getInt(1);
            }

            return Response.ok().entity(id).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/getSimpleCustom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetSimpleFromDbCustom(String param) {
        try {
            ResultSet rs = customProx.query(queryPriority1);

            int holderId = -1;

            if (rs.next()) {
                holderId = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("host_ip", 3306, "sakila", "root",
                                "pass", "com.mysql.jdbc.Driver", true)).query(queryPriority1);

                if (res.next()) {
                    holderId = rs.getInt(1);
                }
            }

            if (holderId == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(holderId).build();
            }
            return Response.ok().entity(holderId).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/getComplexeCustom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetComplexeFromDbCustom() {
        try {
            ResultSet rs = customProx.query(queryPriority2);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("host_ip", 3306, "sakila", "root",
                                "pass", "com.mysql.jdbc.Driver", true)).query(queryPriority2);

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
    @Path("/getSimpleRobin")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetSimpleFromDbRobin(String param) {
        try {
            ResultSet rs = robinProx.query(queryPriority1);

            int holderId = -1;

            if (rs.next()) {
                holderId = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("host_ip", 3306, "sakila", "root",
                                "pass", "com.mysql.jdbc.Driver", true)).query(queryPriority1);

                if (res.next()) {
                    holderId = rs.getInt(1);
                }
            }

            if (holderId == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(holderId).build();
            }
            return Response.ok().entity(holderId).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/getComplexeRobin")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetComplexeFromDbRobin() {
        try {
            ResultSet rs = robinProx.query(queryPriority2);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("host_ip", 3306, "sakila", "root",
                                "pass", "com.mysql.jdbc.Driver", true)).query(queryPriority2);

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
    @Path("/getSimpleRandom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetSimpleFromDbRandom(String param) {
        try {
            ResultSet rs = randomProx.query(queryPriority1);

            int holderId = -1;

            if (rs.next()) {
                holderId = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("host_ip", 3306, "sakila", "root",
                                "pass", "com.mysql.jdbc.Driver", true)).query(queryPriority1);

                if (res.next()) {
                    holderId = rs.getInt(1);
                }
            }

            if (holderId == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(holderId).build();
            }
            return Response.ok().entity(holderId).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/getComplexeRandom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetComplexeFromDbRandom() {
        try {
            ResultSet rs = randomProx.query(queryPriority2);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                ResultSet res = MasterDBConnection.getDbCon(
                        new ConnectionParam("host_ip", 3306, "sakila", "root",
                                "pass", "com.mysql.jdbc.Driver", true)).query(queryPriority2);

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
    @Path("/getSimpleNoProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetSimpleFromDbNoProxy(String param) {
        try {
            ResultSet rs = SlaveDBConnection.getDbCon(
                    new ConnectionParam("host_ip", 3306, "sakila", "root",
                            "pass", "com.mysql.jdbc.Driver", false)).query(queryPriority1);

            int holderId = -1;

            if (rs.next()) {
                holderId = rs.getInt(1);
            }
            if (holderId == -1) {
                return Response.status(HttpURLConnection.HTTP_NO_CONTENT).entity(holderId).build();
            }
            return Response.ok().entity(holderId).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/getComplexeNoProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetComplexeFromDbNoProxy() {
        try {
            ResultSet rs = SlaveDBConnection.getDbCon(
                    new ConnectionParam("host_ip", 3306, "sakila", "root",
                            "pass", "com.mysql.jdbc.Driver", false)).query(queryPriority2);

            int result = -1;

            if (rs.next()) {
                result = rs.getInt(1);
            }

            return Response.ok().entity(result).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
    
    @POST
    @Path("/getRandomNoProxy")
    @Produces(MediaType.TEXT_PLAIN)
    public Response GetRandomFromDbNoProxy() {
        try {
            Random r = new Random();
            int id = r.nextInt(3);

            int result = -1;

            ResultSet res = SlaveDBConnection.getDbCon(
                    new ConnectionParam("host_ip", 3306, "sakila", "root",
                            "pass", "com.mysql.jdbc.Driver", false)).query(sampleQueries[id]);

            if (res.next()) {
                result = res.getInt(1);
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
