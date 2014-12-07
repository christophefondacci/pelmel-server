<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<tiles:insertTemplate template="/jsp/header/ajax-light-header.jsp"/>
<tiles:insertTemplate template="/jsp/header/mymedia-header.jsp"/>
<tiles:insertTemplate template="/jsp/header/map-header.jsp"/>
<style>
	<s:set value="mediaProvider" var="mediaProvider"/>
	<s:set value="0" var="image"/>
	<s:iterator value="#mediaProvider.getMedia()" var="media">
		.image-<s:property value="#image"/> {
			background: url('<s:property value="urlService.getMediaUrl(#media.url)"/>') no-repeat center center;
		}
		<s:set value="#image+1" var="image"/>
	</s:iterator>
</style>