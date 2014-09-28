package org.peerbox.client;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.network.NetworkTestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkStarter extends AbstractStarter {
	
	private static final Logger logger = LoggerFactory.getLogger(NetworkStarter.class);

	private static final int NETWORK_SIZE = 2;
	private static List<IH2HNode> network;
	private static List<ClientNode> clients = new ArrayList<ClientNode>();

	public static void main(String[] args) {
		try {
			new NetworkStarter().run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public NetworkStarter() throws IOException {
		super();
	}

	private void run() {
		try {

			setup();

			// teardown();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setup() throws Exception {
		network = NetworkTestUtil.createH2HNetwork(NETWORK_SIZE);
		logger.info("Create network with {} nodes", NETWORK_SIZE);

		// register specific user
		credentials = NetworkTestUtil.generateRandomCredentials();
		IH2HNode registerNode = network.get(0);
		registerUser(registerNode, credentials);

		// create and login all clients (same credentials)
		for (int i = 0; i < network.size(); ++i) {
			Path path = Paths.get(BASE_PATH.toString(), String.format("user-%s", i));
			clients.add(new ClientNode(network.get(i), credentials, path));
			logger.info("Created client {} ", i);
		}
	}
	
	private void teardown() {
		NetworkTestUtil.shutdownH2HNetwork(network);
	}
}