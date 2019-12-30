package config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.log4j.Logger;
import runner.RemoteServerRunner;

import java.io.File;
import java.io.IOException;

@Data
public class ServerConfigFactory {

    static{
        ObjectMapper mapper =new ObjectMapper();
        try {
//            ourInstance= mapper.readValue(new File("./target/classes/server.json"), ServerConfig.class);
            Logger.getLogger("Runner").debug(System.getProperty("user.dir"));
            ourInstance= mapper.readValue(new File(System.getProperty("user.dir")+"/"+RemoteServerRunner.configFileSrc), ServerConfig.class);
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
