<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="myMessagingSupport" var="myMessagingSupport"/>
<div id="mainContent"><div id="mainCol" class="mainCol">
<!--<h1><img class="overview-icon" src="/images/mail.png"/><s:text name="message.panel.manageMessagesTitle"/></h1>-->
<s:if test="#myMessagingSupport!=null">
<s:if test="!#myMessagingSupport.messages.isEmpty()">
<div class="message-title"><s:property value="#myMessagingSupport.getMessageTitle()"/></div>
<tiles:insertTemplate template="/jsp/ajax/ajax-messages.jsp"/>
</s:if><s:else><s:text name="block.noinfo"/></s:else>
</s:if>
</div></div>
<div id="overlay" class="overlay"><div id="contentWrap" class="contentWrap"></div></div>
<script type="text/javascript">
$(function() {
	bindDefaultOverlays();
});
</script>