package runner;

import config.ServerConfig;
import config.ServerConfigFactory;

public class RemoteServerRunner {

	public  static String configFileSrc;
	public static void main(String[] args) {
		ServerConfig config = ServerConfigFactory.getInstance();
		Thread ToProxy = new Thread(new RemoteServerToProxy(config.getPort()));
		ToProxy.run();
	}
}
