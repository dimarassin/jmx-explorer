package com.freetools.jmx.explorer;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
//import com.hazelcast.core.IMap;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.freetools.jmx.explorer.entities.JmxCommand;
import com.freetools.jmx.explorer.entities.JmxNode;

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
public class JmxAgent{
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

	private String id;
	private String pid;
	private String host;
	private String path;

	private HazelcastInstance instance;

	public void register() {
		if (id == null || id.isEmpty()) id = UUID.randomUUID().toString();
		if (pid == null || pid.isEmpty()) pid = resolvePid();
		if (host == null || host.isEmpty()) host = resolveHost();
		if (path == null || path.isEmpty()) path = resolvePath();

		Set<HazelcastInstance> hcInstances = Hazelcast.getAllHazelcastInstances();
		if (!hcInstances.isEmpty()){
			instance = hcInstances.iterator().next();
		} else {
			instance = HazelcastClient.newHazelcastClient(new ClientConfig());
		}

		instance.getMap("topology").put(id, new JmxNode(id, pid, host, path));

		instance.<JmxCommand>getTopic(id).addMessageListener(new MessageListener<JmxCommand>() {
			@Override
			public void onMessage(Message<JmxCommand> message) {
				JmxCommand command = message.getMessageObject();
				command.execute(server);
				instance.getMap("results").put(command.getId(), command);
			}
		});
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

	public JmxAgent setId(String id) {
		this.id = id;
		return this;
	}
	public JmxAgent setPid(String pid) {
		this.pid = pid;
		return this;
	}

	public JmxAgent setHost(String host) {
		this.host = host;
		return this;
	}

	public JmxAgent setPath(String path) {
		this.path = path;
		return this;
	}
}
