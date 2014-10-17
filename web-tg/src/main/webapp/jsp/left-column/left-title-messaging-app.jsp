<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="instantUserMsg">
	<s:set value="searchSupport.getSearchTitle()" var="title"/>
	<s:set value="currentUserSupport" var="currentUserSupport"/>
	<s:if test="#title!=null && !#title.isEmpty()">
		<h1 class="side-text <s:property value="getHeaderSupport().getPageStyle()"/>-text"><s:property value="searchSupport.getSearchTitle()"/></h1>
		<s:set value="geoPlaceTypesSupport" var="placeTypeSupport"/>
		<s:if test="#placeTypeSupport!=null">
			<div class="row">
				<s:iterator value="#placeTypeSupport.availablePlaceTypes" var="placeType">
					<s:if test="!#placeTypeSupport.getCurrentPlaceType().equals(#placeType)">
						<span class="col-xs-offset-1 col-xs-2 col-sm-offset-2 col-sm-4 side-sublink side-count"><s:property value="#placeTypeSupport.getPlaceTypeCount(#placeType)"/></span>
						<span class="col-xs-5 col-sm-18 side-sublink last"><a class="<s:property value="getHeaderSupport().getPageStyle()"/>-text" href="<s:property value="#placeTypeSupport.getPlaceTypeUrl(#placeType)"/>">
							<s:property value="#placeTypeSupport.getPlaceTypeLabel(#placeType)"/>
						</a></span>
					</s:if>
				</s:iterator>
			</div>
		</s:if>
	</s:if><s:elseif test="currentUserSupport.getCurrentUser()==null">
		<div class="ads-center hidden-xs">
			<s:property value="adBannerSupport.getVerticalBannerHtml()" escapeHtml="false"/>
		</div>
		<div class="ads-center hidden-sm hidden-md hidden-lg">
			<s:property value="adBannerSupport.getHorizontalBannerHtml()" escapeHtml="false"/>
		</div>
	</s:elseif>
	<s:set value="headerPopularSupport" var="popularSupport"/>
	<tiles:insertTemplate template="/jsp/left-column/block-left-popular.jsp"/>
	<tiles:insertTemplate template="/jsp/blocks/block-messaging.jsp"/>
</div>