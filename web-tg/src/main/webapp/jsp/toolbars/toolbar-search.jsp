<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="action-bar <s:property value="#headerSupport.getPageStyle()"/>-alpha">
<%-- 	<s:if test="childSupport.canAddChildFor('PLAC')"> --%>
		<div class="hidden-xs add-container-bg add-search">
			<div class="add-container">
				<span class="add-text <s:property value="#headerSupport.getPageStyle()"/>-text"><s:text name="action.newplace"><s:param><s:property value="#headerSupport.getPageTypeLabel()"/></s:param></s:text></span>
				<a data-toggle="modal" data-target="#myModal" href="<s:property value="childSupport.getAddUrl(#headerSupport.getPageStyle())"/>" class="button <s:property value="#headerSupport.getPageStyle()"/>" rel="nofollow"><s:text name="action.add"/></a>
			</div>
		</div>
<%-- 	</s:if> --%>
	<div class="action-page-title font-bold">
		<s:if test="rightsManagementService.canModify(currentUserSupport.getCurrentUser(),localizationSupport.getCurrentItem())">
			<a data-toggle="modal" data-target="#myModal" href="/ajaxAddMediaDialog?id=<s:property value="localizationSupport.getCurrentItem().getKey().toString()"/>" rel="nofollow"><span id="edit" class="tool tool-addphoto"><!--  --></span></a>
			<a data-toggle="modal" data-target="#myModal" href="/ajaxGetDescForm?parentId=<s:property value="localizationSupport.getCurrentItem().getKey().toString()"/>" rel="nofollow"><span id="edit" class="tool tool-edit"><!--  --></span></a>
		</s:if>
		<s:property value="localizationSupport.getName(localizationSupport.getCurrentItem())"/>
	</div>
</div>
