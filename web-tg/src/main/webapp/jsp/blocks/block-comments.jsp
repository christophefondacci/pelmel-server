<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set var="commentSupport" value="commentSupport"/>
<s:if test="#commentSupport!=null && (#commentSupport.enabled || !#commentSupport.comments.isEmpty())">
<div id="comments-contents">
<tiles:insertTemplate template="/jsp/ajax/ajax-comments-contents.jsp"/>
</div></s:if><s:else>
	<s:if test="!#commentSupport.isEnabled()">
		<s:text name="comments.nocontent"/>
	</s:if>
</s:else>