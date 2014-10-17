<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="action-bar <s:property value="#headerSupport.getPageStyle()"/>-alpha">
	<div class="toolbar">
		<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('edit','status')"/>" rel="nofollow"><span id="edit" class="tool tool-edit" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.edit"><s:param value="#headerSupport.getPageTypeLabel()"/></s:text>"><!--  --></span></a>
		<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('media','status')"/>" rel="nofollow"><span id="addphoto" class="tool tool-addphoto" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.addphoto"/>"><!--  --></span></a>
		<tiles:insertTemplate template="/jsp/toolbars/toolbar-picture-map-actions.jsp"/>
		<s:set value="overviewSupport.getToolbarActionUrl('addevent','status')" var="addeventUrl"/>
		<s:if test="#addeventUrl!=null">
			<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('addevent','status')"/>" rel="nofollow"><span id="addevent" class="tool tool-addevent" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.addevent"/>"><!--  --></span></a>
		</s:if>
		<s:if test="rightsManagementService.canDelete(#currentUser,overviewSupport.getOverviewObject())">
			<a data-toggle="modal" data-target="#myModal" href="<s:property value="overviewSupport.getToolbarActionUrl('delete','status')"/>" rel="nofollow"><span id="addevent" class="tool tool-delete" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.delete"/>"><!--  --></span></a>
		</s:if>
	</div>
	<div class="hidden-xs action-page-title font-bold">
		<s:property value="#overviewSupport.getTitle(#obj)"/>
	</div>
</div>