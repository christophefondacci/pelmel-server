<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="quickInfoSupport" var="quickInfoSupport"/>
<s:set value="#quickInfoSupport.thumbnailUrl" var="thumb"/>
<div class="media" id="infowindow">
	<s:if test="#thumb!=null">
		<img class="pull-left" src="<s:property value="#thumb"/>"/>
	</s:if>
	<div class="media-body">
		<h2 class="info-tooltip-title"><a href="<s:property value="#quickInfoSupport.overviewUrl"/>"><s:property value="#quickInfoSupport.title"/></a></h2>
		<s:set value="#quickInfoSupport.getLocalization()" var="localization"/>
		<s:if test="#localization!=null">
				<span style="display:block;">
					<a href="<s:property value="#quickInfoSupport.getLocalizationUrl()"/>">
						<s:property value="#localization"/>
					</a>
				</span>
		</s:if>
		<div class="align-right">
			<s:iterator value="#quickInfoSupport.tags" var="tag">
				<img class="search-facet-image info-window" src="<s:property value="#quickInfoSupport.getTagIconUrl(#tag)"/>">
			</s:iterator>
		</div>
	</div>
</div>