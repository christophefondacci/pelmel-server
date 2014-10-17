<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="prepend-top append-bottom col-sm-19 last">
<h1 class="profile section-title"><s:text name="page.error.title"/></h1>
</div>
<div class="col-sm-19">
	<div class="col-sm-offset-1 col-sm-18">
		<s:set value="errorMessage" var="msg"/>
		<s:if test="#msg == null">
			<span class="col-sm-18 prepend-top"><s:text name="page.error.description"/></span>
		</s:if><s:else>
			<span class="col-sm-18 prepend-top login-error"><s:property value="errorMessage"/></span>
		</s:else>		
	</div>
</div>
