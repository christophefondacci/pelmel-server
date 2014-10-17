<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="myMessagingSupport" var="myMessagingSupport"/>
<h2 class="section-title main-info-title"><s:text name="messages.list.title"/></h2>
<s:iterator value="#myMessagingSupport.messages" var="msg">
	<div class="prepend-top msg-list-container media">
		<a class="msg-user msg-action pull-left" href="<s:property value="#myMessagingSupport.getToolUrl(#msg,'reply')"/>"><img class="comment-thumb list-item-thumb" src="<s:property value="#myMessagingSupport.getFromIconUrl(#msg)"/>"></a>
		<div class="media-body">
			<div class="comment-body">
				<a class="msg-user msg-action" href="<s:property value="#myMessagingSupport.getToolUrl(#msg,'reply')"/>"><s:property value="#myMessagingSupport.getFromText(#msg)"/></a>
				<s:property value="#myMessagingSupport.getShortMessageText(#msg)"/>
			</div>
		<span class="timestamp"><s:property value="#myMessagingSupport.getDateText(#msg)"/></span>
		</div>
	</div>
</s:iterator>
<s:set var="paginationSupport" value="#myMessagingSupport"/> 
<div class="search-pages prepend-top">
	<tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"/>
</div>