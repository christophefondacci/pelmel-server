<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="overviewToolbox" class="overviewToolbox">
<tiles:insertTemplate template="/jsp/blocks/block-ilike-button.jsp"/>
	<a class="button tool" rel="#overlay" href="<s:property value="overviewSupport.getToolbarActionUrl('edit','status')"/>"><img class="icon" src="<s:property value="overviewSupport.getToolbarActionIconUrl('edit')"/>"/><s:text name="toolbar.edit"/></a>
	<s:iterator value="overviewSupport.additionalActions" var="action">
		<a class="button tool" href="<s:property value="overviewSupport.getToolbarActionUrl(#action,'status')"/>"><img class="icon" src="<s:property value="overviewSupport.getToolbarActionIconUrl(#action)"/>"/><s:property value="overviewSupport.getToolbarLabel(#action)"/></a>
	</s:iterator>
</div>
<div id="status"></div>
<div id="overlay" class="overlay">
	<div id="contentWrap" class="contentWrap"></div>
</div>
<div id="msgOverlay" class="overlay"><div class="contentWrap"><tiles:insertTemplate template="/jsp/blocks/block-write-message.jsp"/></div></div>