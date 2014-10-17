<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="menu" class="col-sm-5">
<img src="<s:property value="getImage('/images/chat-128.png')"/>"/>
<span class="login-title"><s:text name="nav.login.title"/></span>
<p class="line-label"><s:text name="nav.login.desc"/></p>
<tiles:insertTemplate template="/jsp/blocks/block-login-form.jsp"/>
</div>