<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="favoritePlacesSupport" var="favoriteSupport"/>
<tiles:insertTemplate template="/jsp/right-column/generic-overview-right.jsp"></tiles:insertTemplate>
<tiles:insertTemplate template="/jsp/blocks/block-events-list.jsp"/>