<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="row geo-toolbar-container">
	<div class="geo-bar <s:property value="headerSupport.getPageStyle()"/>">
		<div class="menu col-xs-24 col-md-14 col-sm-19 geo-text">
			<tiles:insertAttribute name="page-breadcrumb"/>
		</div>
		<div class="hidden-xs col-sm-5 geo-subtitle">
			<tiles:insertAttribute name="page-subtitle"/>
		</div> 
	</div>
</div>


