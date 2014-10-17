<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="col-sm-19">
<div class="row">
	<div class="col-md-17 col-sm-24 col-lg-17">
		<s:set var="popularSupport" value="latestChangesPopularSupport"/>
		<s:set var="count" value="#popularSupport.popularElements.size()"/>
		<s:set var="i" value="0"/>
		<s:set value="1" var="resultNumber"/>
		<div class="row">
		<s:iterator value="#popularSupport.popularElements" id="elt">
			<s:set value="#popularSupport.getObjectFromActivity(#elt)" var="result"/>
			<s:set value="searchSupport" var="searchSupport"/>
			<tiles:insertTemplate template="/jsp/pages/search-content-block.jsp"/>
		</s:iterator>
		</div>
	</div>
	<div class="col-md-7 col-sm-12 hidden-sm">
		<s:set value="activitySupport" var="activitySupport"/>
		<div class="main-info-container">
			<h3 class="section-title main-info-title app-title"><s:text name="app.block.title"/><span class="arrow"></span></h3>
			<div class="main-info-container-content center">
				<span class="app-desc"><s:text name="app.block.desc"/></span>
				<img class="hp-app" src="/images/V3/phone.jpg" alt="<s:text name="app.block.img.alt"/>">
				<tiles:insertTemplate template="/jsp/blocks/block-apps.jsp"/>
			</div>
		</div>
	</div>
</div>
</div>
