package map;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class ServerProxyMap {
    //记录端口与客户端代理对应关系的代理
    public static Map<Integer, Channel> serverProxyMap=new HashMap<>();
}
