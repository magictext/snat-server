package util;

import exception.RangePortException;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import java.net.InetSocketAddress;
import java.util.Map;

public class RangePort {

	public static Map<Integer,Integer> getRangePort(String Server,String value1, String value2) throws RangePortException {
		DualHashBidiMap ports = new DualHashBidiMap();
		String[] range1 = value1.split(",");
		String[] range2 = value2.split(",");
		if (range1.length!=range2.length) throw new RangePortException("端口格式不合法");
		for (int i = 0; i < range1.length; i++) {
			String[] split1 = range1[i].split("-");
			String[] split2 = range2[i].split("-");
			if(split1.length!=split2.length) throw new RangePortException("端口格式不合法");
			if(split1.length==1){
				ports.put(new InetSocketAddress(Server, Integer.parseInt(split1[0])),Integer.parseInt(split2[0]));
			}else {
				int from1=Integer.parseInt(split1[0]);
				int from2=Integer.parseInt(split2[0]);
				int to1=Integer.parseInt(split1[1]);
				int to2 = Integer.parseInt(split2[1]);
				if (from2-from1!=to2-to1) throw new RangePortException("端口格式不合法");
				for(int j=from1,k=from2 ;j<=to1&&k<=to2;j++,k++){
					ports.put(new InetSocketAddress(Server, j), k);
				}
			}
		}
		return ports;
	}
}
