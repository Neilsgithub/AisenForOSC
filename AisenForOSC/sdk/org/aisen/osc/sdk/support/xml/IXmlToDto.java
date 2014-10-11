package org.aisen.osc.sdk.support.xml;

import com.m.support.task.TaskException;

public interface IXmlToDto {

	public <T> T parse(String xml, Class<T> clazz) throws TaskException;
	
}
