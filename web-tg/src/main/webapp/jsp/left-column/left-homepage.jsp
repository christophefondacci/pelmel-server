<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="home-left">
	<div class="main-info-container">
		<h3 class="section-title main-info-title"><s:text name="homepage.whatshot"/><span class="arrow"></span></h3>
		<s:set value="favoritePlacesSupport" var="favoriteSupport"/>
		<div class="main-info-container-content row">
			<s:iterator value="#favoriteSupport.getFavorites(null)" var="fav">
				<div class="col-xs-8 col-sm-12 col-lg-8 hot-thumb">
					<tiles:insertTemplate template="/jsp/blocks/block-favorite-thumb.jsp"/>
				</div>
			</s:iterator>
		</div>
	</div>
	<div class="main-info-container">
		<h3 class="section-title main-info-title whoshot"><s:text name="homepage.whoshot"/><span class="arrow"></span></h3>
		<s:set value="favoriteUsersSupport" var="favoriteSupport"/>
		<div class="main-info-container-content row">
			<s:iterator value="#favoriteSupport.getFavorites(null)" var="fav">
				<div class="col-xs-8 col-sm-12 col-lg-8 hot-thumb">
					<tiles:insertTemplate template="/jsp/blocks/block-favorite-thumb.jsp"/>
				</div>
			</s:iterator>
		</div>
	</div>
<%-- 	<s:set value="citiesPopularSupport" var="popularSupport"/> --%>
<%-- 	<tiles:insertTemplate template="/jsp/left-column/block-left-popular.jsp"/> --%>
<%-- 	<s:set value="countriesPopularSupport" var="popularSupport"/> --%>
<%-- 	<tiles:insertTemplate template="/jsp/left-column/block-left-popular.jsp"/> --%>
</div>

