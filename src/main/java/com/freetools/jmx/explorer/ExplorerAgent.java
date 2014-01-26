package com.freetools.jmx.explorer;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.freetools.jmx.explorer.entities.JmxCommand;
import com.freetools.jmx.explorer.entities.JmxNode;
import com.hazelcast.util.Base64;

import javax.management.MBeanServer;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;

/**
 * Date: 12/12/13
 *
 * @author Dima Rassin
 */
public class ExplorerAgent {
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

	private String id;
	private String pid;
	private String host;
	private String path;

	private HazelcastInstance hc;

	public ExplorerAgent register(String address) {
		if (id == null || id.isEmpty()) id = generateId();
		if (pid == null || pid.isEmpty()) pid = resolvePid();
		if (host == null || host.isEmpty()) host = resolveHost();
		if (path == null || path.isEmpty()) path = resolvePath();

		Set<HazelcastInstance> hcInstances = Hazelcast.getAllHazelcastInstances();
		if (!hcInstances.isEmpty()){
			hc = hcInstances.iterator().next();
		} else {
			ClientConfig clientConfig = new ClientConfig()
					.addAddress(address);
			hc = HazelcastClient.newHazelcastClient(clientConfig);
		}

		hc.getMap("topology").put(id, new JmxNode(id, pid, host, path));

		hc.<JmxCommand>getTopic(id).addMessageListener(new MessageListener<JmxCommand>() {
			@Override
			public void onMessage(Message<JmxCommand> message) {
				JmxCommand command = message.getMessageObject();
				command.execute(server);
				hc.getMap("results").put(command.getId(), command);
			}
		});
		return this;
	}

	private String generateId(){
		UUID id = UUID.randomUUID();
		return Long.toHexString(id.getMostSignificantBits()) + "-" + Long.toHexString(id.getLeastSignificantBits());
	}

	private String resolvePid(){
		return ManagementFactory.getRuntimeMXBean().getName();
	}

	private String resolveHost() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "UNKNOWN";
		}
	}

	private String resolvePath() {
		return new File(".").getAbsolutePath();
	}

	public ExplorerAgent setId(String id) {
		this.id = id;
		return this;
	}
	public ExplorerAgent setPid(String pid) {
		this.pid = pid;
		return this;
	}

	public ExplorerAgent setHost(String host) {
		this.host = host;
		return this;
	}

	public ExplorerAgent setPath(String path) {
		this.path = path;
		return this;
	}
}
