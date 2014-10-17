<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="myMessagingSupport" var="myMessagingSupport"/>
<div class="prepend-top append-bottom col-xs-24">
<h1 class="<s:property value="headerSupport.getPageStyle()"/> section-title section-login"><s:text name="messages.title"/></h1>
</div>
<div id="msg-contents" class="prepend-top col-xs-18">
	<div class="col-sm-offset-1 col-sm-12"><s:text name="messages.empty.desc"/></div>
</div>
<div class="prepend-top col-xs-6" id="msg-list">
	<tiles:insertTemplate template="/jsp/ajax/ajax-message-mini-list.jsp"/>
</div>
