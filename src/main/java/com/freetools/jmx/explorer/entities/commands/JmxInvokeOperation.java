package com.freetools.jmx.explorer.entities.commands;

import javax.management.JMException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Date: 12/17/13
 *
 * @author Dima Rassin
 */
public class JmxInvokeOperation extends AbstractJmxCommand<JmxInvokeOperation> {
	private static final long serialVersionUID = 1443917694919424580L;

	private String objectName;
	private String operationName;
	private Object[] params;

	private Object result;
	private JMException exception;

	public JmxInvokeOperation(String objectName, String operationName, Object... params) {
		this.objectName = objectName;
		this.operationName = operationName;
		this.params = params;
	}

	@Override
	public void doExecute(MBeanServer mBeanServer) throws JMException{
		result = mBeanServer.invoke(new ObjectName(objectName), operationName, null, null);
	}

	private void parseParams(MBeanServer mBeanServer) throws JMException{
		MBeanInfo beanInfo = mBeanServer.getMBeanInfo(new ObjectName(objectName));
		MBeanOperationInfo operationInfo = null;
		for (MBeanOperationInfo info : beanInfo.getOperations()) {
			if (info.getName().equals(operationName)){
				operationInfo = info;
				break;
			}
		}

		if (operationInfo == null) throw new JMException("Unknown operation: " + operationName);
		MBeanParameterInfo[] parameterInfos = operationInfo.getSignature();
		if (parameterInfos.length != params.length) throw new JMException("Illegal arguments");
		int index=0;
		for (MBeanParameterInfo parameterInfo : parameterInfos) {
			if (params[index] instanceof String && !parameterInfo.getType().equals(String.class.getName())){
				params[index] = parseParam(params[index], parameterInfo.getType());
			}
		}
	}

	private Object parseParam(Object param, String type) {
		return null;
	}

	public Object getResult() throws JMException{
		if (exception == null) return result;
		else throw exception;
	}
}