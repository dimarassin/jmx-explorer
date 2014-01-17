package com.freetools.jmx.explorer.entities.commands;

import com.freetools.jmx.explorer.entities.commands.beans.TestBean;
import org.junit.Before;
import org.junit.Test;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static com.jayway.jsonassert.JsonAssert.*;


/**
 * Date: 12/17/13
 *
 * @author Dima Rassin
 */
public class JmxGetTopologyTest {
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

	@Before
	public void registerBean() throws Exception {
		server.createMBean(TestBean.class.getName(), new ObjectName("myDomain", "type", "MyType"));
	}

	@Test
	public void getTopologyOk() throws Exception {
		String json = new JmxGetTopology()
				.execute(server)
				.getResult();

		assertThat(json).isNotEmpty();

		with(json)
				.assertThat("$..name", hasItem("myDomain"))
				.assertThat("$..objectName", hasItem("myDomain:type=MyType"));
	}
}
