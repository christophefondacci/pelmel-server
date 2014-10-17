<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<tiles:insertTemplate template="/jsp/footer/default-footer.jsp"/>
<tiles:insertTemplate template="/jsp/footer/map-footer.jsp"/>
<script>
$(document).ready(function() {
	Pelmel.initTooltips();
	handleDialogs();
	<s:if test="searchTerm != null">
		Pelmel.search('<s:property value="searchTerm"/>');
	</s:if>
});
</script>