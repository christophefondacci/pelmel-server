<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="prepend-top append-bottom col-xs-24">
<h1 class="profile section-title section-login"><s:text name="nav.login.title"/></h1>
</div>
<div class="col-sm-24">
	<div class="col-sm-offset-1 col-sm-23">
		<span class="prepend-top"><s:text name="nav.login.desc"/></span>
		<s:if test="loginErrorMessage!=null && !''.equals(loginErrorMessage)">
			<span class="login-error prepend-top append-bottom clearfix"><s:property value="loginErrorMessageLabel"/></span>
		</s:if>
		<div class="clearfix"></div>
		<div class="col-xs-24 col-sm-12">
			<tiles:insertTemplate template="/jsp/blocks/block-login-form.jsp"/>
		</div>
		<div class="col-xs-24 col-sm-12">
			<tiles:insertTemplate template="/jsp/blocks/block-register-form.jsp"/>
		</div>
	</div>
</div>