<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="action-bar <s:property value="#headerSupport.getPageStyle()"/>-alpha">
	<div class="toolbar">
		<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('edit','status')"/>" rel="nofollow"><span id="edit" class="tool tool-edit"><!--  --></span></a>
		<div class="tooltip tooltip-edit">
			<div class="tooltip-text"><s:text name="tooltip.edit"><s:param value="#headerSupport.getPageTypeLabel()"/></s:text></div>
			<span class="tooltip-arrow"><!--  --></span>
		</div>
		<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('media','status')"/>" rel="nofollow"><span id="addphoto" class="tool tool-addphoto"><!--  --></span></a>
		<div class="tooltip tooltip-addphoto">
			<div class="tooltip-text"><s:text name="tooltip.addphoto"/></div>
			<span class="tooltip-arrow"><!--  --></span>
		</div>
		<tiles:insertTemplate template="/jsp/toolbars/toolbar-picture-map-actions.jsp"/>
		<s:iterator value="overviewSupport.additionalActions" var="action">
			<s:set value="overviewSupport.getToolbarActionUrl(#action,'status')" var="url"/>
			<s:if test="#url!=null">
				<a href="<s:property value="#url"/>" rel="nofollow"><span id="<s:property value="#action"/>" class="tool tool-<s:property value="#action"/>"><!--  --></span></a>
				<div class="tooltip tooltip-<s:property value="#action"/>">
					<div class="tooltip-text"><s:property value="overviewSupport.getToolbarLabel(#action)"/></div>
					<span class="tooltip-arrow"><!--  --></span>
				</div>
			</s:if>
		</s:iterator>
		<s:if test="rightsManagementService.canDelete(#currentUser,overviewSupport.getOverviewObject())">
			<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('delete','status')"/>" rel="nofollow"><span id="addevent" class="tool tool-delete" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.delete"/>"><!--  --></span></a>
		</s:if>
	</div>
	<div class="action-page-title hidden-xs">
		<s:property value="#overviewSupport.getTitle(#obj)"/>
	</div>
</div>


