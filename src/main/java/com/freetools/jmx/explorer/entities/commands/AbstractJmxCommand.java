package com.freetools.jmx.explorer.entities.commands;

import com.freetools.jmx.explorer.entities.JmxCommand;

import javax.management.JMException;
import javax.management.MBeanServer;
import java.io.Serializable;
import java.util.UUID;

/**
 * Date: 1/15/14
 *
 * @author Dima Rassin
 */
abstract class AbstractJmxCommand<T extends JmxCommand> implements JmxCommand<T>, Serializable {
	private String id;
	private JMException exception;

	AbstractJmxCommand(){
		id = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public final T execute(MBeanServer mBeanServer) {
		if (mBeanServer == null) throw new IllegalArgumentException("MBean Server is null");

		try {
			doExecute(mBeanServer);
		} catch (JMException ex) {
			exception = ex;
		}
		//noinspection unchecked
		return (T)this;
	}

	protected abstract void doExecute(MBeanServer mBeanServer) throws JMException;

	protected void checkResult() throws JMException{
		if (exception != null) throw exception;
	}
}
