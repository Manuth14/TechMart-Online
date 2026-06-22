package lk.techmart.web;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DataSourceDefinition(
        name = "java:app/jdbc/TechMart",
        className = "com.mysql.cj.jdbc.MysqlDataSource",
        url = "jdbc:mysql://localhost:3606/techmart_online",
        user = "root",
        password = "M@14Manuth.",
        properties = {
                "allowPublicKeyRetrieval=true",
                "useSSL=false",
                "serverTimezone=UTC"
        })
public class RestApplication extends Application {
}
