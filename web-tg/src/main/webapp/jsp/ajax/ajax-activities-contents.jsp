<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:if test="#activitySupport==null"><s:set value="activitySupport" var="activitySupport"/></s:if>
<s:if test="!#activitySupport.activities.isEmpty()">
<s:iterator value="#activitySupport.activities" var="activity">
	<s:set value="#activitySupport.getActivityIconUrl(#activity)" var="imgUrl"/>
	<div class="media" id="activity-<s:property value="#activity.key"/>">
			<s:if test="#imgUrl!=null && !''.equals(#imgUrl)">
				<a class="pull-left" href="<s:property value="#activitySupport.getActivityIconLinkUrl(#activity)"/>"><img class="activity-thumb" src="<s:property value="#activitySupport.getActivityIconUrl(#activity)"/>" alt='<s:property value="#activitySupport.getFrom(#activity)"/>'></a>
			</s:if><s:else>
				<span class="pull-left activity-thumb">&nbsp;</span>
			</s:else>
		<div class="media-body"><div class="activity-body">
			<s:property value="#activitySupport.getActivityHtmlLine(#activity)" escapeHtml="false"/></div>
			<span class="timestamp"><s:property value="#activitySupport.getDateTimeLabel(#activity)"/></span>
		</div>
	</div>
</s:iterator>
<s:set var="paginationSupport" value="#activitySupport"/> 
<s:if test="#paginationSupport.pagesList.size()>1">
<s:set var="theme" value="'blue'"/>
<div class="comment-pages prepend-top"><tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"/></div>
</s:if>
</s:if><s:else><div class="padded"><s:text name="block.noinfo"/></div></s:else>