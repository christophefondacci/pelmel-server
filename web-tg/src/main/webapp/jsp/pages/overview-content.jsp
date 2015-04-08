<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="headerSupport.canonical" var="canonical"/>
<div class="col-xs-24 col-sm-10 col-md-13">
	<div class="row">
		<div class="col-md-12 col-xs-24">
			<div class="main-info-container bordered">
				<h1 class="category-title section-title ov-title"><s:property value="overviewSupport.getTitle(#obj)"/><span class="arrow"></span></h1>
				<div class="main-info-container-content">
					<s:if test="#canonical!=null">
						<div class="row">
							<div class="col-xs-24 social-bar">
								<div class="fb-like" data-href="<s:property value="#canonical"/>" data-layout="button" data-action="like" data-show-faces="false" data-share="false"></div>
								<div class="g-plusone" data-size="medium" data-annotation="none"></div>
								<a href="https://twitter.com/share" class="twitter-share-button" data-count="none" data-lang="en">Tweet</a>
							</div>
						</div>
					</s:if>
					<s:set value="overviewSupport.getAddress(#obj)" var="address"/>
					<s:if test="#address!=null">
						<div class="address append-bottom font-bold"><s:property value="#address"/></div>
					</s:if>
					<tiles:insertTemplate template="/jsp/blocks/block-properties.jsp"/>
					<div class="prepend-top">
						<s:iterator value="descriptionSupport.items" var="desc">
							<p><s:property value="descriptionSupport.getItemDescription(#desc)" escapeHtml="false"/></p>
						</s:iterator>
					</div>
					<tiles:insertTemplate template="/jsp/blocks/block-openings.jsp"/>
				</div>
			</div>
			<tiles:insertAttribute name="central-block"/>
			<div class="main-info-container bordered prepend-top">
				<h3 class="section-title"><s:text name="block.comments.title"/></h3>
				<div class="main-info-container-content">
					<tiles:insertTemplate template="/jsp/blocks/block-comments.jsp"/>						
				</div>
			</div>
		</div>
		<div class="col-md-12 hidden-sm hidden-xs">
			<tiles:insertAttribute name="mosaic-subtitle"/>
			<s:set value="adBannerSupport.getSquareBannerHtml()" var="adHtml"/>
			<s:if test="#adHtml!=null">
				<div class="no-overflow-ads">
					<div class="ads-center">
						<s:property value="#adHtml" escapeHtml="false"/>
					</div>
				</div>
			</s:if><s:else>
				<tiles:insertTemplate template="/jsp/blocks/block-mosaic.jsp"/>
			</s:else>
		</div>
	
		<div class="col-xs-24">
			<s:set value="activitySupport" var="activitySupport"/>
			<tiles:insertTemplate template="/jsp/blocks/block-activities.jsp"/>
		</div>
	</div>
</div>
<div class="col-md-6 col-xs-24 col-sm-9">
	<tiles:insertAttribute name="right-col"/>
</div> 

	