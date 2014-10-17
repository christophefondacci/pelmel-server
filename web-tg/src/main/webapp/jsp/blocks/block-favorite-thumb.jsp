<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<a href="<s:property value="#favoriteSupport.getFavoriteLinkUrl(#fav)"/>">
	<img class="img-responsive" src="<s:property value="#favoriteSupport.getFavoriteImageUrl(#fav)"/>" alt="<s:property value='#favoriteSupport.getFavoriteName(#fav)'/>">
	<span class="search-thumb-text"><s:property value="#favoriteSupport.getFavoriteName(#fav)"/></span>
	<span class="distance"><s:property value="#favoriteSupport.getFavoriteNamePrefix(#fav)"/></span>
</a>