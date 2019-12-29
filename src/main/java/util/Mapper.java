package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ClientConfig;

import java.io.File;
import java.io.IOException;

public class Mapper {

    private static ObjectMapper mapper=new ObjectMapper();

    public static byte[] getJsonByte(Object obj){
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getJsonString(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseObject(String var1, Class<T> var2){
        try {
            return mapper.readValue(var1, var2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parseObject(byte[] var1, Class<T> var2){
        try {
            return mapper.readValue(var1, var2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ClientConfig parseObject(File file, Class<ClientConfig> clientConfigClass) {
        try {
            return mapper.readValue(file, clientConfigClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
