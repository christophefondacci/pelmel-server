<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="popup-title" class="boxTitle"><s:text name="nav.error.dialog.title"/></div>
<table><tr><td class="top"><img src="<s:property value="getImage('/images/dialog_warning.png')"/>"/></td><td><h2><s:text name="nav.error.dialog.subTitle"/></h2>
<p><s:text name="nav.error.dialog.desc"/></p>
</td></tr></table>