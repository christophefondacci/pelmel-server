<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="messagingSupport" var="messagingSupport"/>
<div class="prepend-top msg-bubble-container">
	<div>
		<textarea id="msgInstantTextArea" name="instantMsgText" class="form-control msg-reply-container" placeholder="<s:text name="message.area.defaultReplyText"/>"></textarea>
	</div>
	<div class="msg-arrow-container">
		<div id="instant-post-spinner">&nbsp;</div>
		<div class="msg-arrow msg-bottom-arrow"><!--  --></div>
	</div>
	<div class="msg-buttons last">
		<s:if test="!#hideNextMsgButton">
			<a class="col-xs-6 button <s:property value="#messagingSupport.getMessagePageStyle()"/>" href="<s:property value="#nextMsgUrl"/>"><s:text name="messages.box.nextButtonText"/></a>
		</s:if><s:else>
			<span>&nbsp;</span>
		</s:else>
		<input type="submit" class="col-xs-12 button <s:property value="#messagingSupport.getMessagePageStyle()"/>" value="<s:text name="message.submit.shortLabel"/>">
	</div>
</div>