<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="mediaProvider.getCurrentMedia()" var="currentMedia"/>
<img class="main-image" style="<s:property value="mediaProvider.getOverviewFitStyle(#currentMedia)"/>" src="<s:property value="#currentMedia.url"/>" title="<s:property value="#currentMedia.title"/>"/>
