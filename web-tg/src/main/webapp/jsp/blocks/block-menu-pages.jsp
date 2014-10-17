<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="#paginationSupport.pagesList.size()>1">
<p class="menuPage">
	<s:set value="0" var="csspage"/>
	<s:set value="1" var="increment"/>
	<s:iterator value="#paginationSupport.pagesList" var="page">
		<s:if test="#paginationSupport.getCurrentPage().equals(#page) ">
			<a class="selectedPage <s:property value="headerSupport.getPageStyle()"/>" href="#"><s:property value="#page"/></a>
		</s:if><s:elseif test="#page == null">
			<span class="empty-pagination">...</span>
		</s:elseif><s:else>
			<a class="pagination" href="<s:property value="#paginationSupport.getPageUrl(#page)"/>"><s:property value="#page"/></a>
		</s:else>
		<s:set value="#csspage+#increment" var="csspage"/>
		<s:if test="(#csspage % 5) == 0">
			<s:set value="-#increment" var="increment"/>
		</s:if>
	</s:iterator>
</p>
</s:if>