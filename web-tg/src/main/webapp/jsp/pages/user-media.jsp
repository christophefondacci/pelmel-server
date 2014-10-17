<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="mainContent"><div id="mainCol" class="mainCol">
<h1><s:text name="media.panel.manageMediaTitle"/></h1>
<div id="media-list">
<tiles:insertTemplate template="/jsp/blocks/block-media-list.jsp"/>
</div>
<h1><s:text name="media.panel.addMediaTitle"/></h1>
<div id="progressNumber"></div>
<tiles:insertTemplate template="/jsp/blocks/block-add-media.jsp"/>
</div></div>
<div id="overlay" class="overlay"><div id="contentWrap" class="contentWrap"></div></div>
<script type="text/javascript">
$(function() {
	bindDefaultOverlays();
});
</script>