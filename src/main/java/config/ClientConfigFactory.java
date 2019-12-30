package config;

import exception.RangePortException;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import util.Mapper;
import util.RangePort;

import java.io.File;
import java.util.Map;

//客户端用来获得配置的类
public class ClientConfigFactory {

    private static DualHashBidiMap map=new DualHashBidiMap();

        static{
            ClientConfig config = Mapper.parseObject(new File("./target/classes/client.json"), ClientConfig.class);
//            ourInstance=Mapper.parseObject(new File(System.getProperty("user.dir")+ LocalProxyRunner.configFileSrc), ClientConfig.class);
            for (ConfigEntity entity : config.getList()) {
                Map<Integer, Integer> port = null;
                try {
                    port = RangePort.getRangePort(entity.getLocalServer(),entity.getPort(), entity.getRemotePort());
                } catch (RangePortException e) {
                    e.printStackTrace();
                }
                map.putAll(port);
            }
            ourInstance=config;
        }

        private static ClientConfig ourInstance ;

        public static ClientConfig getInstance() {
            return ourInstance;
        }

        public static DualHashBidiMap getMap(){
            return map;
        }

        private ClientConfigFactory(){}

}
