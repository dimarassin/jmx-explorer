package com.freetools.jmx.explorer.entities.commands;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Date: 12/17/13
 *
 * @author Dima Rassin
 */
public class JmxGetTopology extends AbstractJmxCommand<JmxGetTopology> {
	private static final long serialVersionUID = 5038286820433391096L;

	private String result;

	@Override
	public void doExecute(MBeanServer mBeanServer) throws JMException{
		Map<String, MBeanDomain> domains = new TreeMap<>();

		for (String domainName : mBeanServer.getDomains()) {
			domains.put(domainName, new MBeanDomain().name(domainName));
		}

		Set<ObjectInstance> beans = mBeanServer.queryMBeans(null, null);
		for (ObjectInstance beanInst : beans) {
			domains.get(beanInst.getObjectName().getDomain())
					.addBean(new MBean(mBeanServer.getMBeanInfo(beanInst.getObjectName()), beanInst));
		}

		try {
			result = new ObjectMapper()
					.configure(SerializationConfig.Feature.INDENT_OUTPUT, true)
					.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
					.writeValueAsString(domains.values());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getResult() throws JMException {
		checkResult();
		return result;
	}


	private class MBeanDomain {
		public String name;
		public Set<MBean> beans = new TreeSet<>();

		MBeanDomain name(String name) {
			this.name = name;
			return this;
		}

		public void addBean(MBean bean) {
			this.beans.add(bean);
		}
	}

	private class MBean implements Comparable<MBean>{
		public String objectName;
		public String className;
		public String description;
		public Set<MBeanAttribute> attributes = new TreeSet<>();
		public Set<MBeanOperation> operations = new TreeSet<>();

		MBean (MBeanInfo beanInfo, ObjectInstance beanInst){
			objectName = beanInst.getObjectName().getCanonicalName();
			className = beanInfo.getClassName();
			description = beanInfo.getDescription();
			for (MBeanAttributeInfo info : beanInfo.getAttributes()) attributes.add(new MBeanAttribute(info));
			for (MBeanOperationInfo info : beanInfo.getOperations()) operations.add(new MBeanOperation(info));
		}

		@Override
		public int compareTo(MBean o) {
			return objectName.compareTo(o.objectName);
		}
	}

	private class MBeanAttribute implements Comparable<MBeanAttribute>{
		public String name;
		public String description;
		public boolean is;
		public boolean readable;
		public boolean writable;
		public String type;

		MBeanAttribute(MBeanAttributeInfo info){
			name = info.getName();
			description = info.getDescription();
			is = info.isIs();
			readable = info.isReadable();
			writable = info.isWritable();
			type = info.getType();
		}

		@Override
		public int compareTo(MBeanAttribute o) {
			return name.compareTo(o.name);
		}
	}

	private class MBeanOperation implements Comparable<MBeanOperation>{
		public String name;
		public String description;
		public String impact;
		public String returnType;
		public List<MBeanOperationParam> params = new ArrayList<>();

		MBeanOperation(MBeanOperationInfo info){
			name = info.getName();
			description = info.getDescription();
			impact = impactToString(info.getImpact());
			returnType = info.getReturnType();
			for (MBeanParameterInfo parameterInfo : info.getSignature()) {
				params.add(new MBeanOperationParam(parameterInfo));
			}
		}

		private String impactToString(int val){
			switch (val) {
				case 0: return "info";
				case 1: return "action";
				case 2: return "action/info";
				case 3: return "unknown";
				default:return "(" + val + ")";
			}
		}

		@Override
		public int compareTo(MBeanOperation o) {
			return name.compareTo(o.name);
		}
	}

	private class MBeanOperationParam{
		public String name;
		public String description;
		public String type;

		MBeanOperationParam(MBeanParameterInfo info){
			name = info.getName();
			description = info.getDescription();
			type = info.getType();
		}
	}
}
