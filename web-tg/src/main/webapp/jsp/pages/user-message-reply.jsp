<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="myMessagingSupport" var="myMessagingSupport"/>
<div id="mainCol" class="mainCol">
<div class="message-title"><s:property value="#myMessagingSupport.getMessageTitle()"/></div>
<div id="message-post-container" class="message-wrapper"><form id="message-post-form" method="post">
<input type="hidden" name="to" value="<s:property value="from"/>"/>
<img class="list-item-thumb" src="<s:property value="currentUserSupport.currentUserMedia.miniThumbUrl"/>">
<div class="minithumb-text"><textarea id="comment-text" class="comment-text field" name="msgText" onfocus="javascript:Pelmel.focusComment(this,'<s:text name="message.area.defaultReplyText"/>');" onblur="javascript:Pelmel.blurComment(this,'<s:text name="message.area.defaultReplyText"/>');"><s:text name="message.area.defaultReplyText"/></textarea></div>	
<div id="message-post-spinner">&nbsp;</div><input id="msg-submit" type="submit" class="button" value="<s:text name="message.submit.label"/>">
</form></div>
<tiles:insertTemplate template="/jsp/ajax/ajax-messages.jsp"/>
</div>