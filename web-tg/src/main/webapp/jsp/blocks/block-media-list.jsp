<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="currentUserSupport.currentUser" var="currentUser"/>
<s:iterator value="mediaProvider.media" var="media">
	<div class="user-media">
		<%-- Checking if user can administrate the current media --%>
		<s:if test="rightsManagementService.canDelete(#currentUser,#media)">
			<%-- Adding the action toolbox on every image --%>
			<div class="media-actions"><div class="over-toolbox"><a class="but-right img-button" href="<s:property value="mediaProvider.getMoveUrl(#media,1)"/>"><img src="/images/right.png"/></a><a class="but-middle img-button"  rel="#overlay" href="<s:property value="mediaProvider.getDeletionUrl(#media)"/>"><img src="/images/delete.png"/></a><a class="but-left img-button" href="<s:property value="mediaProvider.getMoveUrl(#media,-1)"/>"><img src="/images/left.png"/></a></div></div>
		</s:if>
		<img class="thumb-inner box-shadow" src="<s:property value="#media.miniThumbUrl"/>" />
	</div>
</s:iterator>