<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="#favoriteSupport.categories" var="categories"/>
<s:if test="!#categories.isEmpty()">
<s:iterator value="categories" var="categ">
	<s:set value="#favoriteSupport.getCategoryTitle(#categ)" var="title"/>
	<div class="main-info-container bordered"><h2 class="main-info-title <s:property value="#favoriteSupport.getCategoryStyle(#categ)"/>-text section-title"><s:property value="#favoriteSupport.getCategoryTitle(#categ)"/><span class="arrow"></span></h2>
		<div class="ul-nearby">
			<div class="row row-nearby">
				<s:iterator value="#favoriteSupport.getFavorites(#categ)" var="fav">
					<div class="col-xs-6 col-lg-8 col-sm-8 nearby-thumb">
						<tiles:insertTemplate template="/jsp/blocks/block-favorite-thumb.jsp"/>
					</div>
				</s:iterator>
			</div>
		</div>
	</div> 
</s:iterator>
</s:if>