<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="headerSupport" var="headerSupport"/>
<s:if test="!#hideTitle"><h3 class="prepend-top section-title main-info-title">Login<span class="arrow"></span></h3></s:if>
<div class="row">
	<form action="/userLogin.action" method="post">
		<s:set value="getUrl()" var="url"/>
		<input type="hidden" name="url" value="<s:property value="#url != null ? url :  #headerSupport.getAlternate(#headerSupport.getLanguage())"/>"/>
		<div class="col-xs-offset-1 col-xs-22 fb-container">
			<tiles:insertTemplate template="/jsp/blocks/block-facebook.jsp"/>
		</div>
		<div class="col-xs-offset-1 col-xs-22 ">
			<div class="col-xs-24 login-or">OR</div>
		</div>
		<div class="col-xs-offset-1 col-xs-22 form-group">
		<input class="col-xs-24 form-control" type="text" name="email" placeholder="<s:text name="index.login.email"/>">
		</div>
		<div class="col-xs-offset-1 col-xs-22 form-group">
		<input class="col-xs-24 form-control" type="password" name="password" placeholder="<s:text name="index.login.password"/>">
		</div>
		<div class="col-xs-offset-1 col-xs-22 form-group">
			<input type="submit" class="col-xs-10 button right <s:property value="#headerSupport.getPageStyle()"/>" value="<s:text name="index.login.button"/>">
		</div>
		<div class="col-xs-offset-1 col-xs-22 signup">
			<s:text name="login.noaccount"/><a href="/ajaxGetRegisterForm" data-dismiss="modal" data-toggle="modal" data-target="#myModal">Sign up</a>
		</div>
		<div class="col-xs-offset-1 col-xs-22 pull-right prepend-top">
		</div>
	</form>
</div>
