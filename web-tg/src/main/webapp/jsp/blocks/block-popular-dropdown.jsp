<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="headerPopularSupport" var="popularSupport"/>
<s:iterator value="#popularSupport.getPopularElements()" var="elt">
	<li class="geo-option">
		<a href="<s:property value="#popularSupport.getUrl(#elt)"/>"><s:property value="#popularSupport.getName(#elt)"/></a>
	</li>
</s:iterator>