<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="prepend-top append-bottom col-sm-19 last">
<h1 class="profile section-title section-login"><s:text name="partners.title"/></h1>
</div>
<div class="col-sm-19">
	<div class="col-sm-offset-1 col-sm-18">
		<div class="prepend-top col-sm-18 last">
			<span class="col-sm-18 prepend-top last"><s:text name="partners.desc"/></span>
			<span class="col-sm-18 prepend-top last"><s:property value="getText('partners.newlist')" escapeHtml="false"/></span>
		</div>
	</div>
</div>
