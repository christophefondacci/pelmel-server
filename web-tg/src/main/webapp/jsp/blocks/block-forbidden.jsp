<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="rights.forbidden.title"/></h1>
<div id="dialog" class="container">
	<div class="prepend-top col-sm-offset-1 col-sm-14 append-bottom last">
		<div class="col-sm-4">
			<img src="/images/dialog_warning.png"/>
		</div>
		<div class="col-sm-19"><s:text name="rights.forbidden.desc"/></div>
	</div>
</div>
