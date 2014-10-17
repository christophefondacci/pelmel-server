<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="nav.login.dialog.title"/></h1>
<div id="mini-dialog">
	<div class="row">
		<div class="col-sm-offset-1 col-sm-24 col-xs-offset-1 col-xs-22">
			<s:text name="nav.login.dialog.required"/>
		</div>
		<div class="row">
			<div class="col-sm-offset-1 col-sm-10 col-xs-offset-1 col-xs-22">
				<tiles:insertTemplate template="/jsp/blocks/block-login-form.jsp"/>
			</div>
			<div class="col-sm-offset-1 col-sm-11 col-xs-offset-1 col-xs-22">
				<tiles:insertTemplate template="/jsp/blocks/block-register-form.jsp"/>
			</div>
		</div>
	</div>
</div>