package com.freetools.jmx.explorer.entities;

import javax.management.MBeanServer;

/**
 * Date: 12/12/13
 *
 * @author Dima Rassin
 */
public interface JmxCommand<T extends JmxCommand<?>> {
	String getId();
	T execute(MBeanServer mBeanServer);
}
