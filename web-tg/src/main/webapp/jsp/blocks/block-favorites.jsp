<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="favorites" class="favorites"> 
	<h2 id="favoritesTitle" class="boxTitle"><s:property value="#favoriteSupport.title"/></h2>
<s:set value="#favoriteSupport.categories" var="categories"/>
<s:if test="!#categories.isEmpty()">
<s:iterator value="categories" var="categ"><s:set value="#favoriteSupport.getCategoryTitle(#categ)" var="title"/>
	<s:if test="#title!=null"><h3 class="boxSection"><s:property value="#favoriteSupport.getCategoryTitle(#categ)"/>&nbsp;:</h3></s:if>
	<ul id="styles" class="ul-facets clearfix box">
		<s:iterator value="#favoriteSupport.getFavorites(#categ)" var="fav">
			<li class="li-facet"><span>
				<s:set value="#favoriteSupport.getFavoriteName(#fav)" var="favoriteName"/>
				<span class="li-text"><a href="<s:property value="#favoriteSupport.getFavoriteLinkUrl(#fav)"/>"><img id="fav-icon" class="icon" alt="<s:property value="#favoriteName"/>" src="<s:property value="#favoriteSupport.getFavoriteImageUrl(#fav)"/>"></a><s:property value="#favoriteSupport.getFavoriteNamePrefix(#fav)" escapeHtml="false"/><a href="<s:property value="#favoriteSupport.getFavoriteLinkUrl(#fav)"/>"><s:property value="#favoriteName"/></a></span>
			</span></li>
		</s:iterator>
	</ul> 
</s:iterator>
<div id="styles-bottom" class="ul-facets clearfix box"></div>
</s:if><s:else><p class="facet-text clearfix box"><s:text name="block.noinfo"/></p>
</s:else>
</div>
