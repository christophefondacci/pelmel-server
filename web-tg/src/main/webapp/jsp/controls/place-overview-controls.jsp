<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:if test="mapSupport != null">
	<s:set value="true" var="display"/>
	<tiles:insertTemplate template="/jsp/controls/map-control.jsp"/>
	<s:set value="false" var="display"/>
	<tiles:insertTemplate template="/jsp/controls/image-control.jsp"/>
</s:if>
