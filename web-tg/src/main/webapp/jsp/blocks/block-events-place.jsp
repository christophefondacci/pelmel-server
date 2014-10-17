<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="eventsListSupport" var="eventsListSupport"/>
<s:if test="#eventsListSupport!=null && !#eventsListSupport.getItems().isEmpty()"> 
	<div class="col-sm-7 main-info-container last prepend-top">
		<tiles:insertTemplate template="/jsp/blocks/block-events-list.jsp"/>
	</div>
</s:if>