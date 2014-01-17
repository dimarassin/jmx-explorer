package com.freetools.jmx.explorer.entities.commands.beans;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import java.lang.reflect.Method;

/**
 * Date: 12/22/13
 *
 * @author Dima Rassin
 */
@SuppressWarnings("unused")
public class TestBean implements DynamicMBean {
	private final String ATTR_NAME = "myAttr";
	private int value;

	public void setValue(int value){
		this.value = value;
	}

	@Override
	public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
		if (ATTR_NAME.equals(attribute)) return value;
		else throw new AttributeNotFoundException();
	}

	@Override
	public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public AttributeList getAttributes(String[] attributes) {
		AttributeList result = new AttributeList();
		for (String attrName : attributes) {
			if (ATTR_NAME.equals(attrName)) result.add(new Attribute(ATTR_NAME, value));
		}
		return result;
	}

	@Override
	public AttributeList setAttributes(AttributeList attributes) {
			/*do nothing*/
		return new AttributeList();
	}

	@Override
	public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
		Class<?>[] classes = new Class[signature.length];
		int index = 0;
		for (String className : signature) {
			try {
				classes[index++] = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new ReflectionException(e);
			}
		}

		try{
			Method method = this.getClass().getMethod(actionName, classes);
			return method.invoke(this, params);
		} catch (Exception ex){
			throw new ReflectionException(ex);
		}
	}

	@Override
	public MBeanInfo getMBeanInfo() {
		MBeanAttributeInfo attrInfo = new MBeanAttributeInfo("value", int.class.getName(), "the value", true, false, false);
		MBeanOperationInfo opInfo = null;
		try {
			opInfo = new MBeanOperationInfo("bla bla", this.getClass().getMethod("setValue", int.class));
		} catch (Exception ex) {/*do nothing*/}

		return new MBeanInfo(this.getClass().getName(),
				"bla, bla",
				new MBeanAttributeInfo[]{attrInfo},
				null,
				new MBeanOperationInfo[]{opInfo},
				null);
	}
}
