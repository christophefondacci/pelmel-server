<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="myMessagingSupport" var="myMessagingSupport"/>
<div id="mainCol">
<s:if test="#myMessagingSupport!=null">
<s:if test="!#myMessagingSupport.messages.isEmpty()">
<div class="row-fluid"><div class="span8">
<div class="subnav">
<ul class="nav nav-pills">
<li><s:property value="#myMessagingSupport.getMessageTitle()"/></li>
</ul>
</div></div><div class="span4"></div></div>
<tiles:insertTemplate template="/jsp/ajax/ajax-bs-messages.jsp"/>
</s:if><s:else><s:text name="block.noinfo"/></s:else>
</s:if>
</div>
</div>
<div id="overlay" class="overlay"><div id="contentWrap" class="contentWrap"></div></div>
<script type="text/javascript">
$(function() {
	bindDefaultOverlays();
});
</script>