<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:if test="tabbedSearchSupport != null">
<div class="tab-parent clearfix"><div class="tab-container"><ul class="tabs"><s:iterator value="tabbedSearchSupport.tabs" var="tab">
<s:if test="tabbedSearchSupport.isCurrentTab(#tab)">
<li class="search-type-selected"><a class="white" href="<s:property value="tabbedSearchSupport.getTabUrl(#tab)"/>"><s:property value="tabbedSearchSupport.getTabLabel(#tab)"/></a></li></s:if>
<s:else>
<li class="search-type"><a href="<s:property value="tabbedSearchSupport.getTabUrl(#tab)"/>"><s:property value="tabbedSearchSupport.getTabLabel(#tab)"/></a></li>
</s:else></s:iterator></ul></div></div>
</s:if>