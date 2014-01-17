package com.freetools.jmx.explorer.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Date: 1/15/14
 *
 * @author Dima Rassin
 */
public class JmxNode implements Serializable{
	private static final long serialVersionUID = 7974441498080027521L;

	private String id;
	private String pid;
	private String host;
	private String path;

	public JmxNode(String id, String pid, String host, String path) {
		this.id = id;
		this.pid = pid;
		this.host = host;
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public String getPid() {
		return pid;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JmxNode jmxNode = (JmxNode) o;

		if (host != null ? !host.equals(jmxNode.host) : jmxNode.host != null) return false;
		if (id != null ? !id.equals(jmxNode.id) : jmxNode.id != null) return false;
		if (path != null ? !path.equals(jmxNode.path) : jmxNode.path != null) return false;
		if (pid != null ? !pid.equals(jmxNode.pid) : jmxNode.pid != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id,pid,host,path);
	}
}
