<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="col-sm-19">
	<div class="row">
		<s:set value="searchSupport" var="searchSupport"/>
		<s:set value="1" var="resultNumber"/>
		<div class="col-md-17 col-sm-24 col-lg-17">
			<s:if test="!#searchSupport.searchResults.isEmpty()">
				<div class="row">
					<s:iterator value="#searchSupport.searchResults" var="result">
						<tiles:insertTemplate template="/jsp/pages/search-content-block.jsp"/>
					</s:iterator>
				</div>
				<s:set value="#searchSupport" var="paginationSupport"/>
				<s:set var="theme" value="'blue'"/>
				<div class="row search-pages prepend-top">
					<tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"/>
				</div> 
			</s:if><s:else>
				<s:text name="search.results.empty">
					<s:param><s:property value="headerSupport.getPageTypeLabel()"/></s:param>
					<s:param><s:property value="localizationSupport.getName(localizationSupport.getCurrentItem())"/></s:param>
					<s:param><s:property value="headerSupport.getPageTypeLabel()"/></s:param>
				</s:text>
			</s:else>
		</div>
		<tiles:insertTemplate template="/jsp/blocks/block-search-content-facets.jsp"/>
		<div class="col-md-24 hidden-xs hidden-sm">
			<div class="ads-center">
				<s:property value="adBannerSupport.getHorizontalBannerHtml()" escapeHtml="false"/>
			</div>
		</div>
	</div>
</div>
