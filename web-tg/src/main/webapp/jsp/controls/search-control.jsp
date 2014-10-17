<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="searchSupport" var="searchSupport"/>
<s:set value="#searchSupport.getSearchDescription()" var="desc"/>
<s:if test="#desc!=null">
	<div class="col-sm-6 action-desc-container last">
		<div class="action-desc">
			<s:property value="#desc" escapeHtml="false"/>
		</div>
	</div>
</s:if>
