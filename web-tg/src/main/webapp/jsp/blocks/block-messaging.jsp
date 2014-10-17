<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:if test="currentUserSupport.getCurrentUser()!=null">
	<form id="instantMsgForm" method="post" >
		<div class="main-info-container instant-container">
			<h3 id="instantMsgTitle" class="section-title"><tiles:insertTemplate template="/jsp/ajax/ajax-messaging-title.jsp"/></h3>
			<div id="instantMsgBody" class="prepend-top">
				<tiles:insertTemplate template="/jsp/ajax/ajax-messaging-body.jsp"/>
			</div>
		</div>
	</form>
</s:if><s:else>
	&nbsp;
</s:else>