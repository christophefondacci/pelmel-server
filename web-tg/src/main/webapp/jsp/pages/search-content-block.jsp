<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="#searchSupport.getResultThumbnailUrl(#result)" var="iconUrl"/>
<%--   --%>
<div class="search-result-container main-search">
	<s:set value="#searchSupport.getResultUrl(#result)" var="resultUrl"/>
	<s:set value="#searchSupport.getResultTitle(#result)" var="resultTitle"/>
	<a href="<s:property value="#resultUrl"/>" class="search-result">
		<s:if test="#iconUrl!= null">
			<img class="img-responsive search-thumb" alt="<s:property value="#resultTitle"/>" src="<s:property value="#iconUrl"/>"/>
		</s:if><s:else>
			<div class="full-width <s:property value="#iconUrl==null?('search-empty ' + #searchSupport.getResultThumbnailStyle(#result)):''"/>"></div>
		</s:else>
		<span class="search-thumb-text"><s:property value="#searchSupport.getResultTitle(#result)"/></span>
	</a>
	<s:set value="#searchSupport.getResultDescription(#result)" var="resultDescription"/>
	<s:if test="#resultDescription!=null && !#resultDescription.isEmpty()">
		<div id="search-tooltip-<s:property value="#resultNumber"/>" class="search-tooltip">
			<div class="arrow"><!--  --></div>
			<div class="search-tooltip-container-bg">
				<div class="search-tooltip-container row">
					<h2 class="search-tooltip-title"><a href="<s:property value="#searchSupport.getResultUrl(#result)"/>"><s:property value="#resultTitle"/></a></h2>
					<div class="col-xs-24">
						<span style="display:block;">
							<a href="<s:property value="#searchSupport.getResultLocalizationUrl(#result)"/>">
								<s:property value="#searchSupport.getResultCategoryLinkLabel(#result)"/>
							</a>
						</span>
						<s:if test="nearbySearchSupport.isDistanceAvailable(#result)">
							<span><s:property value="nearbySearchSupport.getDistanceLabel(#result)"/></span>
						</s:if>
					</div>
					<div class="col-xs-24 prepend-top">
						<s:property value="#resultDescription"/>
					</div>
					<div class="col-xs-24 align-right">
						<s:iterator value="tagSupport.getTags(#result)" var="tag">
							<s:set value="tagSupport.getTagIconUrl(#tag)" var="iconUrl"/>
							<s:if test="#iconUrl!=null">
								<img class="search-facet-image" alt="<s:property value="tagSupport.getTagTranslation(#tag)"/>" src="<s:property value="#iconUrl"/>">
							</s:if>
						</s:iterator>
					</div>
				</div>
			</div>
		</div>
	</s:if>
</div>
<s:set value="#resultNumber+1" var="resultNumber"/>
