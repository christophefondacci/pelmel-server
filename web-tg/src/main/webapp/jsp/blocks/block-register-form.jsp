<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class='row'>
<form action="/userRegister.action" method="post">
	<s:if test="!#hideTitle"><h3 class="prepend-top section-title main-info-title"><s:text name="homepage.come"/><span class="arrow"></span></h3></s:if>
	<div class="row">
		<div class="col-xs-offset-1 col-xs-22 titled-text"><s:text name="index.register.desc"/></div>
		<s:if test="#hideTitle">
			<div class="col-xs-offset-1 col-xs-22" >
				<div class="col-xs-24 fb-container">
					<tiles:insertTemplate template="/jsp/blocks/block-facebook.jsp"/>
				</div>
				<div class="col-xs-24 login-or">
				OR
				</div>
			</div>
		</s:if>
		<div class="col-xs-offset-1 col-xs-22 form-group" >
			<input class="col-xs-24 form-control" type="text" name="email" placeholder="<s:text name="index.login.email"/>">
		</div>
		<div class="col-xs-offset-1 col-xs-22 form-group" >
			<input class="col-xs-24 form-control" type="text" name="name" placeholder="<s:text name="index.register.username"/>">
		</div>
		<div class="col-xs-offset-1 col-xs-22 form-group">
			<input class="col-xs-24 form-control" type="password" name="password" placeholder="<s:text name="index.login.password"/>">
		</div>
		<div class="col-xs-offset-1 col-xs-22 form-group">
			<input class="col-xs-24 form-control" type="password" name="passwordConfirm" placeholder="<s:text name="index.register.passwordConfirm"/>">
		</div>
		<input type="hidden" name="cityKey" id="cityValue" value="<s:property value="currentUserSupport.currentUserCity.key"/>"/>
		<div class="col-xs-offset-1 col-xs-22 form-group">
			<input class="form-control" type="text" id="city" name="cityLabel" value="<s:property value="cityValue"/>" placeholder="<s:text name="index.register.city"/>"/>
		</div>
		<input type="submit" class="col-xs-offset-13 col-xs-10 button prepend-top append-bottom <s:property value="headerSupport.getPageStyle()"/>" value="<s:text name="index.register.button"/>">
	</div>
</form>
</div>