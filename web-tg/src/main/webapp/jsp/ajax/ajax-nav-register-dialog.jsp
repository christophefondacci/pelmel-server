<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="homepage.dialog.title"/></h1>
<div id="mini-dialog" class="row">
	<div class="col-xs-offset-1 col-xs-22">
		<s:set value="true" var="hideTitle"/>
		<tiles:insertTemplate template="/jsp/blocks/block-register-form.jsp"/>
	</div>
</div>