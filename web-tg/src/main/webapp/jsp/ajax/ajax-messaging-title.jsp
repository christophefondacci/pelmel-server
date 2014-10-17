<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="messagingSupport.newMessagesCount" var="msgCount"/>
<s:if test="'USER'.equals(overviewSupport.overviewObject.key.type) && #msgCount == 0">
	<s:text name="messages.box.title.newMsg"/>
</s:if><s:else>
	<s:text name="messages.box.title">
		<s:param><s:property value="#msgCount"/></s:param>
	</s:text>
</s:else>