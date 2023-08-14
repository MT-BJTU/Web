package mycom.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class JdbcUtils {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    public HashMap<String, String> getDBInfo() {
        String[] split = url.split(":");
        String host = String.format("%s:%s:%s", split[0], split[1], split[2]);
        String[] portSplit = split[3].split("/");
        String port = portSplit[0];

        String[] databaseSplit = portSplit[1].split("\\?");
        String dbName = databaseSplit[0];
        HashMap<String, String> result = new HashMap<>();
        result.put("url",url);
        result.put("host",host);
        result.put("port",port);
        result.put("dbName",dbName);
        result.put("userName",username);
        result.put("passWord",password);

        return result;
    }
}