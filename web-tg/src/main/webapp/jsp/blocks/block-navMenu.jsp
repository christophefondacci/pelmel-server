<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="menu" class="col-sm-5">
<s:set value="currentUserSupport.currentUser" var="currentUser"/>
<div class="currentUser"><h4><s:property value="#currentUser.pseudo"/></h4></div>
<div id="user-thumb" class="border nofloat"><a href="<s:property value="currentUserSupport.getCurrentUserOverviewUrl()"/>"><img class="thumb-inner box-shadow" src=<s:property value="currentUserSupport.currentUserMedia.thumbUrl"/>></a></div>
<div class="fl"><h4 class="boxTitle">Menu</h4>
<ul id="user-menu" class="ul-facets clearfix box">
	<li class="li-nav"><a href="<s:property value="menuSupport.menCityUrl"/>"><img class="icon" src="/images/male.png" width="16" height="17"/><s:text name="nav.menu.myCity"/></a></li>
	<li class="li-nav"><a href="<s:property value="menuSupport.venuesCityUrl"/>"><img class="icon" src="/images/marker.png" width="16" height="17"/><s:text name="nav.menu.cityVenues"/></a></li>
	<li class="li-nav"><a href="<s:property value="menuSupport.eventsCityUrl"/>"><img class="icon" src="/images/calendar.png" width="16" height="17"/><s:text name="nav.menu.cityEvents"/></a></li>
	<li class="li-nav"><a href="/myMessages.action"><img class="icon" src="/images/email.png" width="16" height="17"/><s:text name="nav.menu.myMessages"/></a></li>
	<li class="li-nav"><a href="/myMedia.action"><img class="icon" src="/images/video.png" width="16" height="17"/><s:text name="nav.menu.myMedia"/></a></li>
	<li class="li-nav"><a href="/myProfile.action"><img class="icon" src="/images/profile.png" width="16" height="17"/><s:text name="nav.menu.myProfile"/></a></li>
</ul></div>
<form id="instantMsgForm" method="post" >
	<div id="instantUserMsg" class="fl"><tiles:insertTemplate template="/jsp/blocks/block-messaging.jsp"/></div>
</form>
</div>