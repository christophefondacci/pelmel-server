<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="mainCol" class="mainCol">
<h1><s:text name="user.profile.h1"/></h1>
<form action="userRegister.action" enctype="multipart/form-data" method="post">
<tiles:insertTemplate template="/jsp/blocks/block-user-profile.jsp"/>
<br>
<tiles:insertTemplate template="/jsp/blocks/block-edit-properties.jsp"/>
<br>
<tiles:insertTemplate template="/jsp/blocks/block-edit-descriptions.jsp"/>
<input id="register" type="submit" class="big button right" value="<s:text name="profile.submit.button"/>"/>
</form>
</div>
