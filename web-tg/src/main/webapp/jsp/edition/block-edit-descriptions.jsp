<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="#spanclass==null || 'col-sm-6'.equals(#spanclass) ? 212 : 184" var="descheight"/>
<div>
	<span id="desc-add-<s:property value="#descheight"/>" class="control-add <s:property value="headerSupport.getPageStyle()"/>-add"><!-- --></span>
	<label><s:text name="place.form.description"/></label>
</div>
<s:set value="descriptionsEditionSupport" var="descriptionsEditionSupport"/>
<div id="desc-container" style="height:<s:property value="(#descriptionsEditionSupport.descriptions.size()==0?1:#descriptionsEditionSupport.descriptions.size())*#descheight"/>px;">
	<s:if test="!#descriptionsEditionSupport.descriptions.isEmpty()">
		<s:iterator value="#descriptionsEditionSupport.descriptions" var="desc">
			<tiles:insertTemplate template="/jsp/edition/block-edit-description-line.jsp"/>
		</s:iterator>
	</s:if><s:else>
		<s:set value="null" var="desc"/>
		<tiles:insertTemplate template="/jsp/edition/block-edit-description-line.jsp"/>
	</s:else>
	<s:set value="null" var="desc"/>
	<div id="desc-template" class="hidden">
		<tiles:insertTemplate template="/jsp/edition/block-edit-description-line.jsp"/>
	</div>
</div>