<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="propertiesEditionSupport" var="propertiesEditionSupport"/>
<div>
	<span id="prop-add-65" class="control-add  <s:property value="headerSupport.getPageStyle()"/>-add"><!-- --></span>
	<label><s:text name="property.form.title"/></label>
</div>
<div id="prop-container" style="height:<s:property value="(#propertiesEditionSupport.properties.size()==0 ? 1 :#propertiesEditionSupport.properties.size()) *65"/>px;">
<s:iterator value="#propertiesEditionSupport.properties" var="prop">
<tiles:insertTemplate template="/jsp/edition/block-edit-property-line.jsp"/>
</s:iterator>
<s:set value="null" var="prop"/>
<tiles:insertTemplate template="/jsp/edition/block-edit-property-line.jsp"/>
<div id="prop-template" class="hidden">
<tiles:insertTemplate template="/jsp/edition/block-edit-property-line.jsp"/>
</div>
</div>