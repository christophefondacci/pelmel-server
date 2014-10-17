<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="!#popularSupport.popularElements.isEmpty()">
<div id="popular" class="fl">
	<h2 id="popular-title" class="boxTitle right-col"><s:property value="#popularSupport.title"/></h2>
	<ul id="popular-list" class="ul-facets clearfix box right-col">
		<s:iterator value="#popularSupport.popularElements" id="elt">
			<li class="li-facet"><span>
				<span class="li-text"><a href="<s:property value="#popularSupport.getUrl(#elt)"/>"><img class="icon" src="<s:property value="#popularSupport.getIconUrl(#elt)"/>"></a><a href="<s:property value="#popularSupport.getUrl(#elt)"/>"><s:property value="#popularSupport.getName(#elt)"/></a></span>
				<s:set value="#popularSupport.getCount(#elt)" var="count"/>
				<s:if test="#count>0"><span class="li-count"><s:property value="#count"/></span></s:if>
			</span></li>
		</s:iterator>
	</ul>
</div>
</s:if>