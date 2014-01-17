package com.freetools.jmx.explorer.entities.commands;

import javax.management.AttributeList;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Date: 12/17/13
 *
 * @author Dima Rassin
 */
public class JmxGetAttributes extends AbstractJmxCommand<JmxGetAttributes> {
	private static final long serialVersionUID = 6205929021750705349L;

	private String objectName;
	private String[] attributeNames;

	private AttributeList result;

	public JmxGetAttributes(String objectName, String... attributeNames) {
		if (objectName == null) throw new IllegalArgumentException("objectName is null");
		if (attributeNames == null) throw new IllegalArgumentException("attributeName is null");
		this.objectName = objectName;
		this.attributeNames = attributeNames;
	}

	@Override
	public void doExecute(MBeanServer mBeanServer) throws JMException{
		result = mBeanServer.getAttributes(new ObjectName(objectName), attributeNames);
	}

	public AttributeList getResult() throws JMException{
		checkResult();
		return result;
	}
}