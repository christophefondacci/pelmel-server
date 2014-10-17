<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:if test="#activitySupport!=null">
<div class="prepend-top">
<div class="main-info-container">
	<h3 class="section-title"><s:property value="#activitySupport.title"/><span class="arrow"></span></h3>
	<div class="main-info-container-content" id="<s:property value="#activitySupport.activityHtmlContentId"/>">
		<tiles:insertTemplate template="/jsp/ajax/ajax-activities-contents.jsp"/>
	</div>
</div>
</div>
</s:if>