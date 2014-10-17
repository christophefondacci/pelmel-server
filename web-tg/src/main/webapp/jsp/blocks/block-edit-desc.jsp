<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<form action="/updateDescription" id="update-desc-form" method="post">
<s:set value="descriptionsEditionSupport" var="editionSupport"/>
<input type="hidden" name="parentId" value="<s:property value="#editionSupport.getParentId()"/>"/>
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="desc.form.title"/></h1>
<div id="dialog" class="container">
	<s:set value="'col-sm-14'" var="spanclass"/>
	<div class="prepend-top col-sm-offset-1 col-sm-14">
		<div class="col-sm-14">
			<tiles:insertTemplate template="/jsp/edition/block-edit-descriptions.jsp"/>
		</div>
	</div>
	<div class="prepend-top append-bottom col-sm-offset-9 col-sm-6">
		<div class="col-sm-1" id="place-edit-spinner"></div>
		<input type="submit" class="col-sm-5 last right button <s:property value="headerSupport.getPageStyle()"/>" value="<s:text name="place.form.submit"/>"/>
	</div>
</div>
</form>