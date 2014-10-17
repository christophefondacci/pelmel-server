<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<div id="elementTags" class="elementTags">
	<s:iterator value="tagSupport.getTags(#obj)" id="tag">
		<s:set value="tagSupport.getTagTranslation(#tag)" var="tagLabel"/>
		<div class="space-right space-top"><span class="tag"><img alt="<s:property value="#tagLabel"/>" class="icon" src="<s:property value="tagSupport.getTagIconUrl(#tag)"/>"><s:property value="tagSupport.getTagTranslation(#tag)"/></span></div>
	</s:iterator>
</div>