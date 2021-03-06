/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.jsonwebservice.action;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONIncludesManagerUtil;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceAction;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionMapping;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionsManagerUtil;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jodd.bean.BeanUtil;

import jodd.servlet.ServletUtil;

import jodd.util.NameValue;

/**
 * @author Igor Spasic
 * @author Eduardo Lundgren
 */
public class JSONWebServiceInvokerAction implements JSONWebServiceAction {

	public JSONWebServiceInvokerAction(HttpServletRequest request) {
		_request = request;

		_command = request.getParameter("cmd");

		if (_command == null) {
			try {
				_command = ServletUtil.readRequestBody(request);
			}
			catch (IOException ioe) {
				throw new IllegalArgumentException(ioe);
			}
		}
	}

	public JSONWebServiceActionMapping getJSONWebServiceActionMapping() {
		return null;
	}

	public Object invoke() throws Exception {
		Object command = JSONFactoryUtil.looseDeserializeSafe(_command);

		List<Object> list = null;

		boolean batchMode = false;

		if (command instanceof List) {
			list = (List<Object>)command;

			batchMode = true;
		}
		else if (command instanceof Map) {
			list = new ArrayList<Object>(1);

			list.add(command);

			batchMode = false;
		}
		else {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < list.size(); i++) {
			Map<String, Map<String, Object>> map =
				(Map<String, Map<String, Object>>)list.get(i);

			if (map.isEmpty()) {
				throw new IllegalArgumentException();
			}

			Set<Map.Entry<String, Map<String, Object>>> entrySet =
				map.entrySet();

			Iterator<Map.Entry<String, Map<String, Object>>> iterator =
				entrySet.iterator();

			Map.Entry<String, Map<String, Object>> entry = iterator.next();

			Statement statement = _parseStatement(
				null, entry.getKey(), entry.getValue());

			Object result = _executeStatement(statement);

			list.set(i, result);
		}

		Object result = null;

		if (batchMode == false) {
			result = list.get(0);
		}
		else {
			result = list;
		}

		return new InvokerResult(result);
	}

	public class InvokerResult implements JSONSerializable {

		public String toJSONString() {
			if (_result == null) {
				return JSONFactoryUtil.getNullJSON();
			}

			JSONSerializer jsonSerializer =
				JSONFactoryUtil.createJSONSerializer();

			jsonSerializer.exclude("*.class");

			for (Statement statement : _statements) {
				if (_includes != null) {
					for (String include : _includes) {
						jsonSerializer.include(include);
					}
				}

				String name = statement.getName();

				if (name == null) {
					continue;
				}

				String includeName = name.substring(1);

				_checkJSONSerializerIncludeName(includeName);

				jsonSerializer.include(includeName);
			}

			return jsonSerializer.serialize(_result);
		}

		public Object getResult() {
			return _result;
		}

		private InvokerResult(Object result) {
			_result = result;
		}

		private Object _result;

	}

	private void _addInclude(Statement statement, String name) {
		if (_includes == null) {
			_includes = new ArrayList<String>();
		}

		StringBuilder sb = new StringBuilder();

		while (statement._parentStatement != null) {
			String statementName = statement.getName().substring(1);

			sb.insert(0, statementName + StringPool.PERIOD);

			statement = statement._parentStatement;
		}

		sb.append(name);

		_includes.add(sb.toString());
	}

	private Object _addVariableStatement(
			Statement variableStatement, Object result)
		throws Exception {

		Statement statement = variableStatement.getParentStatement();

		result = _populateFlags(statement, result);

		String name = variableStatement.getName();

		Object variableResult = _executeStatement(variableStatement);

		Map<String, Object> map = _convertObjectToMap(statement, result, null);

		if (!variableStatement.isInner()) {
			map.put(name.substring(1), variableResult);

			return map;
		}

		int index = name.indexOf(".$");

		String innerObjectName = name.substring(0, index);

		if (innerObjectName.contains(StringPool.PERIOD)) {
			throw new IllegalArgumentException(
				"Inner properties with more than 1 level are not supported");
		}

		Object innerObject = map.get(innerObjectName);

		String innerPropertyName = name.substring(index + 2);

		if (innerObject instanceof List) {
			List<Object> innerList = (List<Object>)innerObject;

			List<Object> newInnerList = new ArrayList<Object>(innerList.size());

			for (Object innerListElement : innerList) {
				Map<String, Object> newInnerListElement = _convertObjectToMap(
					statement, innerListElement, innerObjectName);

				newInnerListElement.put(innerPropertyName, variableResult);

				newInnerList.add(newInnerListElement);
			}

			map.put(innerObjectName, newInnerList);
		}
		else {
			Map<String, Object> innerMap = _convertObjectToMap(
				statement, innerObject, innerObjectName);

			innerMap.put(innerPropertyName, variableResult);

			map.put(innerObjectName, innerMap);
		}

		return map;
	}

	private Object _addVariableStatementList(
			Statement variableStatement, Object result, List<Object> results)
		throws Exception {

		List<Object> list = _convertObjectToList(result);

		for (Object object : list) {
			if (object instanceof List) {
				Object value = _addVariableStatementList(
					variableStatement, object, results);

				results.add(value);
			}
			else {
				Object value = _addVariableStatement(variableStatement, object);

				results.add(value);
			}
		}

		return results;
	}

	private void _checkJSONSerializerIncludeName(String includeName) {
		if (includeName.contains(StringPool.STAR)) {
			throw new IllegalArgumentException(
				includeName + " has special characters");
		}
	}

	private List<Object> _convertObjectToList(Object object) {
		if (!(object instanceof List)) {
			String json = JSONFactoryUtil.looseSerialize(object);

			object = JSONFactoryUtil.looseDeserialize(json, ArrayList.class);
		}

		return (List<Object>)object;
	}

	private Map<String, Object> _convertObjectToMap(
		Statement statement, Object object, String prefix) {

		if (object instanceof Map) {
			return (Map<String, Object>)object;
		}

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		jsonSerializer.exclude("class");

		String json = jsonSerializer.serialize(object);

		Class<?> clazz = object.getClass();

		object = JSONFactoryUtil.looseDeserialize(json, HashMap.class);

		String[] includes = JSONIncludesManagerUtil.lookupIncludes(clazz);

		for (String include : includes) {
			if (Validator.isNotNull(prefix)) {
				include = prefix + StringPool.PERIOD + include;
			}

			_addInclude(statement, include);
		}

		return (Map<String, Object>)object;
	}

	private Object _executeStatement(Statement statement) throws Exception {
		JSONWebServiceAction jsonWebServiceAction =
			JSONWebServiceActionsManagerUtil.getJSONWebServiceAction(
				_request, statement.getMethod(), null,
				statement.getParameterMap());

		Object result = jsonWebServiceAction.invoke();

		result = _filterResult(statement, result);

		List<Statement> variableStatements = statement.getVariableStatements();

		if (variableStatements == null) {
			return result;
		}

		for (Statement variableStatement : variableStatements) {
			boolean innerStatement = variableStatement.isInner();

			if (innerStatement) {
				result = variableStatement.push(result);
			}

			if (result instanceof List) {
				result = _addVariableStatementList(
					variableStatement, result, new ArrayList<Object>());

				variableStatement.setExecuted(true);

				if (innerStatement) {
					result = variableStatement.pop(result);
				}
			}
			else {
				if (innerStatement) {
					result = variableStatement.pop(result);
				}

				result = _addVariableStatement(variableStatement, result);

				variableStatement.setExecuted(true);
			}
		}

		return result;
	}

	private Object _filterResult(Statement statement, Object result) {
		if (result instanceof List) {
			result = _filterResultList(
				statement, result, new ArrayList<Object>());
		}
		else {
			result = _filterResultObject(statement, result);
		}

		return result;
	}

	private Object _filterResultList(
		Statement statement, Object result, List<Object> results) {

		List<Object> list = _convertObjectToList(result);

		for (Object object : list) {
			Object value = _filterResultObject(statement, object);

			results.add(value);
		}

		return results;
	}

	private Object _filterResultObject(Statement statement, Object result) {
		if (result == null) {
			return result;
		}

		String[] whitelist = statement.getWhitelist();

		if (whitelist == null) {
			return result;
		}

		Map<String, Object> map = _convertObjectToMap(statement, result, null);

		Map<String, Object> whitelistMap = new HashMap<String, Object>(
			whitelist.length);

		for (String key : whitelist) {
			Object value = map.get(key);

			whitelistMap.put(key, value);
		}

		return whitelistMap;
	}

	private Statement _parseStatement(
		Statement parentStatement, String assignment,
		Map<String, Object> statementBody) {

		Statement statement = new Statement(parentStatement);

		_statements.add(statement);

		int x = assignment.indexOf(StringPool.EQUAL);

		if (x == -1) {
			statement.setMethod(assignment.trim());
		}
		else {
			String name = assignment.substring(0, x).trim();

			int y = name.indexOf(StringPool.OPEN_BRACKET);

			if (y != -1) {
				String whitelistString = name.substring(
					y + 1, name.length() - 1);

				String[] whiteList = StringUtil.split(whitelistString);

				for (int i = 0; i < whiteList.length; i++) {
					whiteList[i] = whiteList[i].trim();
				}

				statement.setWhitelist(whiteList);

				name = name.substring(0, y);
			}

			statement.setName(name);

			statement.setMethod(assignment.substring(x + 1).trim());
		}

		HashMap<String, Object> parameterMap = new HashMap<String, Object>(
			statementBody.size());

		statement.setParameterMap(parameterMap);

		for (String key : statementBody.keySet()) {
			if (key.startsWith(StringPool.AT)) {
				String value = (String)statementBody.get(key);

				List<Flag> flags = statement.getFlags();

				if (flags == null) {
					flags = new ArrayList<Flag>();

					statement.setFlags(flags);
				}

				Flag flag = new Flag();

				flag.setName(key.substring(1));
				flag.setValue(value);

				flags.add(flag);
			}
			else if (key.startsWith(StringPool.DOLLAR) || key.contains(".$")) {
				Map<String, Object> map =
					(Map<String, Object>)statementBody.get(key);

				List<Statement> variableStatements =
					statement.getVariableStatements();

				if (variableStatements == null) {
					variableStatements = new ArrayList<Statement>();

					statement.setVariableStatements(variableStatements);
				}

				Statement variableStatement = _parseStatement(
					statement, key, map);

				variableStatements.add(variableStatement);
			}
			else {
				Object value = statementBody.get(key);

				parameterMap.put(CamelCaseUtil.normalizeCamelCase(key), value);
			}
		}

		return statement;
	}

	private Object _populateFlags(Statement statement, Object result) {
		if (result instanceof List) {
			result = _populateFlagsList(
				statement.getName(), result, new ArrayList<Object>());
		}
		else {
			_populateFlagsObject(statement.getName(), result);
		}

		return result;
	}

	private List<Object> _populateFlagsList(
		String name, Object result, List<Object> results) {

		List<Object> list = _convertObjectToList(result);

		for (Object object : list) {
			if (object instanceof List) {
				Object value = _populateFlagsList(name, object, results);

				results.add(value);
			}
			else {
				_populateFlagsObject(name, object);

				results.add(object);
			}
		}

		return results;
	}

	private void _populateFlagsObject(String name, Object object) {
		if (name == null) {
			return;
		}

		String pushedName = null;

		int index = name.indexOf(CharPool.PERIOD);

		if (index != -1) {
			pushedName = name.substring(0, index + 1);
		}

		name = name.concat(StringPool.PERIOD);

		for (Statement statement : _statements) {
			if (statement.isExecuted()) {
				continue;
			}

			List<Flag> flags = statement.getFlags();

			if (flags == null) {
				continue;
			}

			for (Flag flag : flags) {
				String value = flag.getValue();

				if (value == null) {
					continue;
				}

				if (value.startsWith(name)) {
					Map<String, Object> parameterMap =
						statement.getParameterMap();

					Object propertyValue = BeanUtil.getDeclaredProperty(
						object, value.substring(name.length()));

					parameterMap.put(flag.getName(), propertyValue);
				}
				else if (statement.isPushed() && value.startsWith(pushedName)) {
					Map<String, Object> parameterMap =
						statement.getParameterMap();

					Object propertyValue = BeanUtil.getDeclaredProperty(
						statement._pushTarget,
						value.substring(pushedName.length()));

					parameterMap.put(flag.getName(), propertyValue);
				}
			}
		}
	}

	private String _command;
	private List<String> _includes;
	private HttpServletRequest _request;
	private List<Statement> _statements = new ArrayList<Statement>();

	private class Flag extends NameValue<String, String> {
	}

	private class Statement {

		public List<Flag> getFlags() {
			return _flags;
		}

		public String getMethod() {
			return _method;
		}

		public String getName() {
			return _name;
		}

		public Map<String, Object> getParameterMap() {
			return _parameterMap;
		}

		public Statement getParentStatement() {
			return _parentStatement;
		}

		public List<Statement> getVariableStatements() {
			return _variableStatements;
		}

		public String[] getWhitelist() {
			return _whitelist;
		}

		public boolean isExecuted() {
			return _executed;
		}

		public boolean isInner() {
			return _inner;
		}

		public boolean isPushed() {
			if (_pushTarget != null) {
				return true;
			}

			return false;
		}

		public Object push(Object result) {
			if (_parentStatement == null) {
				return null;
			}

			_pushTarget = result;

			Statement statement = getParentStatement();

			String variableName = getName();

			int index = variableName.indexOf(".$");

			String beanName = variableName.substring(0, index);

			result = BeanUtil.getDeclaredProperty(result, beanName);

			statement.setName(
				statement.getName() + StringPool.PERIOD + beanName);

			variableName = variableName.substring(index + 1);

			setName(variableName);

			return result;
		}

		public Object pop(Object result) {
			if (_pushTarget == null) {
				return null;
			}

			Statement statement = getParentStatement();

			String statementName = statement.getName();

			int index = statementName.lastIndexOf('.');

			String beanName = statementName.substring(index + 1);

			statementName = statementName.substring(0, index);

			statement.setName(statementName);

			setName(beanName + StringPool.PERIOD + getName());

			BeanUtil.setDeclaredProperty(_pushTarget, beanName, result);

			result = _pushTarget;

			_pushTarget = null;

			return result;
		}

		public void setExecuted(boolean executed) {
			_executed = executed;
		}

		public void setFlags(List<Flag> flags) {
			_flags = flags;
		}

		public void setMethod(String method) {
			_method = method;
		}

		public void setName(String name) {
			if (name.contains(".$")) {
				_inner = true;
			}
			else {
				_inner = false;
			}

			_name = name;
		}

		public void setParameterMap(Map<String, Object> parameterMap) {
			_parameterMap = parameterMap;
		}

		public void setVariableStatements(List<Statement> variableStatements) {
			_variableStatements = variableStatements;
		}

		public void setWhitelist(String[] whitelist) {
			_whitelist = whitelist;
		}

		private Statement(Statement parentStatement) {
			_parentStatement = parentStatement;
		}

		private boolean _executed;
		private List<Flag> _flags;
		private boolean _inner;
		private String _method;
		private String _name;
		private Map<String, Object> _parameterMap;
		private Statement _parentStatement;
		private Object _pushTarget;
		private List<Statement> _variableStatements;
		private String[] _whitelist;

	}

}