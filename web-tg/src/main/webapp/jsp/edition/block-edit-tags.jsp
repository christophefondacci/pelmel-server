<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="facet-container" id="tags-container">
	<s:set value="tagSupport" var="tagSupport"/>
	<s:iterator value="#tagSupport.getAvailableTags()" id="tag">
		<div class="search-facet <s:property value="#tagSupport.isTagSelected(#tag) ? 'search-facet-selected':''"/>">
			<label class="hidden" for="<s:property value="#tag.key"/>"><s:property value="#tagSupport.getTagTranslation(#tag)"/></label>
			<s:if test="#tagSupport.isTagSelected(#tag)">
				<input type="checkbox" class="hidden" id="<s:property value="#tag.key"/>" name="tags" value="<s:property value="#tag.key"/>" checked>
			</s:if><s:else>
				<input type="checkbox" class="hidden" id="<s:property value="#tag.key"/>" name="tags" value="<s:property value="#tag.key"/>">
			</s:else>
			<img id="i-<s:property value="#tag.key"/>" class="search-facet-image tag" src="<s:property value="#tagSupport.getTagIconUrl(#tag)"/>">
			<div id="t-<s:property value="#tag.key"/>" class="search-facet-text tag"><s:property value="#tagSupport.getTagTranslation(#tag)"/></div>
		</div>
	</s:iterator>
</div>