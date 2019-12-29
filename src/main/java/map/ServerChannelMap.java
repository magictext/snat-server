package map;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class ServerChannelMap {

    //记录sessionId(ChannelHashcode.getChannelHashcode(ctx))和客户端对应的chnnel的容器
    public static Map<Integer, Channel> serverChannelMap=new HashMap<>();

}
