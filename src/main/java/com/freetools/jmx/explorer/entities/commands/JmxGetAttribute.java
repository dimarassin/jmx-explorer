package com.freetools.jmx.explorer.entities.commands;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Date: 12/17/13
 *
 * @author Dima Rassin
 */
public class JmxGetAttribute extends AbstractJmxCommand<JmxGetAttribute> {
	private static final long serialVersionUID = 4436078946399077729L;

	private String objectName;
	private String attributeName;

	private Object result;

	public JmxGetAttribute(String objectName, String attributeName) {
		if (objectName == null) throw new IllegalArgumentException("objectName is null");
		if (attributeName == null) throw new IllegalArgumentException("attributeName is null");
		this.objectName = objectName;
		this.attributeName = attributeName;
	}

	@Override
	public void doExecute(MBeanServer mBeanServer) throws JMException{
		result = mBeanServer.getAttribute(new ObjectName(objectName), attributeName);
	}

	public Object getResult() throws JMException{
		checkResult();
		return result;
	}
}