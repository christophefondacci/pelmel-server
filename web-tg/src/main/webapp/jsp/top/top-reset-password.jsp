<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="prepend-top append-bottom col-xs-24">
<h1 class="bar section-title section-login"><s:text name="nav.title.resetPassword"/></h1>
</div>
<form action="/resetPassword" method="POST">
	<input type="hidden" name="nxtpUserToken" value="<s:property value="nxtpUserToken"/>">
	<div class="col-xs-24">
		<div class="col-sm-offset-1 col-sm-23">
			<p class="col-xs-24"><s:text name="nav.resetPassword.desc"/></p>
			<div class="clearfix"></div>
			<div class="col-xs-24 col-sm-offset-6 col-sm-12 form-group">
				<input class="col-xs-24 form-control" type="password" name="password" placeholder="<s:text name="index.login.password"/>">
			</div>
			<div class="col-xs-24 col-sm-offset-6 col-sm-12 form-group">
				<input class="col-xs-24 form-control" type="password" name="passwordConfirm" placeholder="<s:text name="index.register.passwordConfirm"/>">
			</div>
			<div class="col-xs-24 col-sm-offset-12 col-sm-6 form-group">
				<input class="col-xs-24 button bar" type="submit" value="<s:text name="index.password.changeButton"/>">
			</div>
		</div>
	</div>
</form>