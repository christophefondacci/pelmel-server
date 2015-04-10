<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="messagingSupport" var="messagingSupport"/>
<s:set value="#messagingSupport.ajaxNextMessage" var="nextMsgUrl"/>
<s:set value="#messagingSupport.currentMessage" var="currentMsg"/>
<s:set value="mediaProvider" var="mediaProvider"/>
<s:set value="overviewSupport" var="overviewSupport"/>
<div id="msgCount" class="hidden"><s:property value="#messagingSupport.newMessagesCount"/></div>
<input type="hidden" name="pageStyle" id="pageStyle" value="<s:property value="#messagingSupport.getMessagePageStyle()"/>">
<s:if test="#messagingSupport.newMessagesCount>0">
	<input type="hidden" name="toKey" id="toKey" value="<s:property value="#messagingSupport.currentUserKey"/>">
	<input type="hidden" name="fromKey" id="fromKey" value="<s:property value="#messagingSupport.getFromKey(#currentMsg)"/>">
	<input type="hidden" name="readMsg" id="readMsg" value="<s:property value="#messagingSupport.getMessageKey(#currentMsg)"/>">
	<div id="hasMsg" class="hidden">true</div>
	<div class="media">
		<a class="pull-left" href="<s:property value="#messagingSupport.getFromUrl(#currentMsg)"/>"><img class="thumb-logged-user" src=<s:property value="#messagingSupport.getFromIconUrl(#currentMsg)"/>></a>
		<div class="media-body">
			<a class="msg-from-link" href="<s:property value="#messagingSupport.getFromUrl(#currentMsg)"/>">
				<s:property value="#messagingSupport.getFromText(#currentMsg)" escapeHtml="false"/>
			</a>
			<span class="msg-date"><s:property value="#messagingSupport.getDateText(#currentMsg)"/></span>
		</div>
	</div>
	<div class="msg-bubble-container">
		<div class="msg-arrow msg-top-arrow"><!--  --></div>
		<div>
			<div class="margin-span msg-container">
				<s:iterator value="#messagingSupport.getMessageText(#currentMsg)" var="msgLine">
					<s:property value="#msgLine" escapeHtml="false"/><br>
				</s:iterator>
			</div>
		</div>
	</div>
	<tiles:insertTemplate template="/jsp/ajax/ajax-messaging-compose.jsp"/>
</s:if><s:else>
	<s:set value="messagingSupport.newMessagesCount" var="msgCount"/>
	<s:if test="'USER'.equals(#overviewSupport.overviewObject.key.type) && #msgCount == 0">
		<input type="hidden" name="toKey" id="toKey" value="<s:property value="#messagingSupport.currentUserKey"/>">
		<input type="hidden" name="fromKey" id="fromKey" value="<s:property value="#overviewSupport.overviewObject.key.toString()"/>">
		<div id="hasMsg" class="hidden">true</div>
		<div class="media">
			<div class="msg-thumb pull-left">
				<img class="thumb-logged-user" src="<s:property value="#mediaProvider.getMediaMiniThumbUrl(#mediaProvider.getCurrentMedia())"/>">
			</div>
			<div class="media-body">
				<a class="msg-from-link" href="#">
					<s:property value="#overviewSupport.getTitle(#overviewSupport.overviewObject)" escapeHtml="false"/>
				</a>
			</div>
		</div>
		<s:set value="true" var="hideNextMsgButton"/>
		<tiles:insertTemplate template="/jsp/ajax/ajax-messaging-compose.jsp"/>
	</s:if><s:else>
		<div class="msg-padding"><s:text name="message.nomessage"/></div>
		<div id="hasMsg" class="hidden">false</div>
	</s:else>
</s:else>