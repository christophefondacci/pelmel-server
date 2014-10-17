<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="currentUserSupport.currentUser" var="currentUser"/>
<div class="well sidebar-nav">
<h4><s:property value="#currentUser.pseudo"/></h4>
<a class="thumbnail" href="<s:property value="currentUserSupport.getCurrentUserOverviewUrl()"/>"><img class="thumb-inner box-shadow" src=<s:property value="currentUserSupport.currentUserMedia.thumbUrl"/>></a>
<ul class="nav nav-list">
	<li class="nav-header">Menu</li>
	<li><a href="/myMessages.action"><img src="images/email.png" width="16" height="17"/>&nbsp;<s:text name="nav.menu.myMessages"/></a></li>
	<li><a href="/myMedia.action"><img src="images/video.png" width="16" height="17"/>&nbsp;<s:text name="nav.menu.myMedia"/></a></li>
	<li><a href="/myProfile.action"><img src="images/profile.png" width="16" height="17"/>&nbsp;<s:text name="nav.menu.myProfile"/></a></li>
</ul>
<form id="instantMsgForm" method="post" >
	<div id="instantUserMsg" class="fl"><tiles:insertTemplate template="/jsp/blocks/block-messaging.jsp"/></div>
</form>
</div>