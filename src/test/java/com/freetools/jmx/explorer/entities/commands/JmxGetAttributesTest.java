package com.freetools.jmx.explorer.entities.commands;

import org.junit.Test;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Date: 12/17/13
 *
 * @author Dima Rassin
 */
public class JmxGetAttributesTest {
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

	@Test
	public void getAttributeOk() throws JMException {
		AttributeList result = new JmxGetAttributes("com.sun.management:type=HotSpotDiagnostic", "ObjectName")
				.execute(server)
				.getResult();

		assertThat(result).hasSize(1).containsExactly(new Attribute("ObjectName", new ObjectName("com.sun.management:type=HotSpotDiagnostic")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void objectNameNull() throws JMException {
		new JmxGetAttributes(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void attrNameNull() throws JMException {
		new JmxGetAttributes("", null);
	}

	@Test(expected = MalformedObjectNameException.class)
	public void badObjectNameFormat() throws JMException {
		new JmxGetAttributes("aaa", "bbb")
				.execute(server)
				.getResult();
	}

	@Test(expected = InstanceNotFoundException.class)
	public void objectNameNotExist() throws JMException {
		new JmxGetAttributes("aaa:bbb=ccc", "bbb")
				.execute(server)
				.getResult();
	}

	@Test
	public void attributeNotExist() throws JMException {
		AttributeList result = new JmxGetAttributes("com.sun.management:type=HotSpotDiagnostic", "aaa", "bbb")
				.execute(server)
				.getResult();

		assertThat(result).isEmpty();
	}
}
