<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="mediaProvider.getCurrentMedia()" var="currentMedia"/>
<input type="hidden" id="mediaKey" name="mediaKey" value="<s:property value="#currentMedia.key"/>">
<img id="preview-image" style="<s:property value="mediaProvider.getOverviewFitStyle(#currentMedia)"/>" src="<s:property value="#currentMedia.originalUrl"/>" title="<s:property value="#currentMedia.title"/>"/>
