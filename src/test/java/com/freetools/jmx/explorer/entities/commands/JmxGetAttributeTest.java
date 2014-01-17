package com.freetools.jmx.explorer.entities.commands;

import org.junit.Test;

import javax.management.AttributeNotFoundException;
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
public class JmxGetAttributeTest {
	private MBeanServer server = ManagementFactory.getPlatformMBeanServer();

	@Test
	public void getAttributeOk() throws JMException {
		Object result = new JmxGetAttribute("com.sun.management:type=HotSpotDiagnostic", "ObjectName")
				.execute(server)
				.getResult();

		assertThat(result).isEqualTo(new ObjectName("com.sun.management:type=HotSpotDiagnostic"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void objectNameNull() throws JMException {
		new JmxGetAttribute(null, "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void attrNameNull() throws JMException {
		new JmxGetAttribute("", null);
	}

	@Test(expected = MalformedObjectNameException.class)
	public void badObjectNameFormat() throws JMException {
		new JmxGetAttribute("aaa", "bbb")
				.execute(server)
				.getResult();
	}

	@Test(expected = InstanceNotFoundException.class)
	public void objectNameNotExist() throws JMException {
		new JmxGetAttribute("aaa:bbb=ccc", "bbb")
				.execute(server)
				.getResult();
	}

	@Test(expected = AttributeNotFoundException.class)
	public void attributeNotExist() throws JMException {
		new JmxGetAttribute("com.sun.management:type=HotSpotDiagnostic", "bbb")
				.execute(server)
				.getResult();
	}
}
