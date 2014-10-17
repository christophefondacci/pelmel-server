<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:if test="tabbedSearchSupport != null">
<div class="clearfix"><div class="tab-container"><s:iterator value="tabbedSearchSupport.tabs" var="tab"><s:if test="tabbedSearchSupport.isCurrentTab(#tab)"><div class="search-type-selected"><a class="white" href="<s:property value="tabbedSearchSupport.getTabUrl(#tab)"/>"><s:property value="tabbedSearchSupport.getTabLabel(#tab)"/></a></div></s:if><s:else><div class="search-type"><a href="<s:property value="tabbedSearchSupport.getTabUrl(#tab)"/>"><s:property value="tabbedSearchSupport.getTabLabel(#tab)"/></a></div></s:else></s:iterator></div></div>
</s:if>