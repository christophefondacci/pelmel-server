<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="prepend-top append-bottom col-xs-24">
<h1 class="profile section-title section-login"><s:text name="about.title"/></h1>
</div>
<div class="col-xs-24 col-sm-offset-1 col-sm-11">
	<h2 class="section-title main-info-title"><s:text name="about.legal"/></h2>
	<span class="prepend-top"><s:property value="getText('about.legal.contents')" escapeHtml="false"/></span>
</div>
<div class="col-xs-24 col-sm-offset-1 col-sm-11">
	<h2 class="section-title main-info-title"><s:text name="about.partners"/></h2>
	<span class="prepend-top"><s:text name="about.partners.contents"/></span>
</div>		

<div class="prepend-top col-xs-24 col-sm-offset-1 col-sm-23">
	<h2 class="section-title main-info-title"><s:text name="about.terms"/></h2>
	<span class="prepend-top"><s:text name="about.terms.contents"/></span>
	<span class="prepend-top"><tiles:insertTemplate template="/jsp/top/terms.jsp"/></span>
	
</div>
