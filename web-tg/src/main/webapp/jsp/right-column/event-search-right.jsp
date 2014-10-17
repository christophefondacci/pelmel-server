<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set var="searchSupport" value="searchSupport"/>
<tiles:insertTemplate template="/jsp/blocks/block-searchFacets.jsp"/>
<s:set var="popularSupport" value="popularSupport"/>
<tiles:insertTemplate template="/jsp/blocks/block-popular.jsp"/>