<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="searchSupport" var="searchSupport"/>
<s:if test="#searchSupport!=null">
<s:if test="!#searchSupport.searchResults.isEmpty()">
<s:iterator value="#searchSupport.searchResults" var="item">
	<s:set value="#searchSupport.getResultUrl(#item)" var="itemUrl"/>
	<li><a class="list-link" href="<s:property value="#itemUrl"/>" data-transition="none"><img class="thumb" src="<s:property value="#searchSupport.getResultMiniThumbUrl(#item)"/>"/>
		<h5 class="li-title" ><s:property value="#searchSupport.getResultTitle(#item)"/></h5>
		<s:set value="#item" var="obj"/>
		<span class="distance"><s:property value="nearbySearchSupport.getDistanceLabel(#item)"/></span><!--tiles:insertTemplate template="/jsp/blocks/block-tags.jsp"/ -->
		<p style="margin-top:5px;"><s:property value="#searchSupport.getResultDescription(#item)"/></p>
	</a></li>
</s:iterator>
</s:if><s:else><s:text name="block.noinfo"/></s:else>


</s:if>