package com.freetools.jmx.explorer;

import com.hazelcast.config.Config;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.freetools.jmx.explorer.entities.JmxCommand;
import com.freetools.jmx.explorer.entities.JmxNode;
import org.fest.assertions.MapAssert;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import javax.management.MBeanServer;

import java.io.Serializable;

import static org.fest.assertions.Assertions.assertThat;


public class JmxAgentTest {
	private String id = "myAgent";
	private ExplorerAgent agent;
	private HazelcastInstance hc;

	@Before
	public void before() throws Exception {
		hc = Hazelcast.newHazelcastInstance(new Config());
		agent = new ExplorerAgent()
				.setId(id);
	}

	@After public void after(){
		hc.shutdown();
	}

	@Test
	public void testRegister() throws Exception {
		assertThat(hc.getMap("topology")).isEmpty();
		agent.setId("id").setPid("pid").setHost("host").setPath("path").register("locally");
		assertThat(hc.getMap("topology")).isNotEmpty().includes(MapAssert.entry("id", new JmxNode("id", "pid", "host", "path")));
	}

	@Test(timeout = 5000)
	public void testCommand() throws Exception {
		agent.register("127.0.0.1:5701");
		hc.<String,TestJmxCommand>getMap("results").addEntryListener(new EntryAdapter<String,TestJmxCommand>(){
			@Override
			public void entryAdded(EntryEvent<String,TestJmxCommand> event) {
				assertThat(event.getValue().getResult()).isEqualTo("my result");
			}
		}, "id", true);
		hc.getTopic(id).publish(new TestJmxCommand("id", "my result"));

		TestJmxCommand command;
		while((command = hc.<String,TestJmxCommand>getMap("results").remove("id")) == null) Thread.sleep(200);
		assertThat(command.getResult()).isEqualTo("my result");
	}

	static class TestJmxCommand implements JmxCommand<TestJmxCommand>, Serializable{
		private String id;
		private String param;
		private String result;

		private TestJmxCommand(String id, String param) {
			this.id = id;
			this.param = param;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public TestJmxCommand execute(MBeanServer mBeanServer) {
			result = param;
			System.out.println("command [" + id + "] is done!!!");
			return this;
		}

		public String getResult(){
			return result;
		}
	}
}
