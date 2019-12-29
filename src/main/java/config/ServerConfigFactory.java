package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class ServerConfigFactory {

    static{
        ObjectMapper mapper =new ObjectMapper();
        try {
            ourInstance= mapper.readValue(new File("./target/classes/server.json"), ServerConfig.class);
            //ourInstance= mapper.readValue(System.getProperty("user.dir")+ RemoteServerRunner.configFileSrc, ServerConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ServerConfig ourInstance ;

    public static ServerConfig getInstance() {
        return ourInstance;
    }

    private ServerConfigFactory() { }

}
