<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/<s:property value='locale'/>/all.js#xfbml=1&appId=302478486488872";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<div class="col-sm-24">
	<div class="col-sm-5">
		<div id="logo" class="col-sm-5"><s:if test="logged"><s:set value="menuSupport.menCityUrl" var="homeUrl"/></s:if><s:else><s:set value="homepageUrl" var="homeUrl"/></s:else>
			<a href="<s:property value="#homeUrl"/>"><img class="logo" src="/images/V2/logo.png"/></a>
		</div>
		<div id="quickLinks" class="col-sm-2">
			<s:if test="logged">
				<a href="<s:property value="menuSupport.menCityUrl"/>" title="<s:text name="nav.menu.myCity"/>"><img class="icon" src="/images/male.png" width="16" height="17"/></a>
				<a href="<s:property value="menuSupport.venuesCityUrl"/>" title="<s:text name="nav.menu.cityVenues"/>"><img class="icon" src="/images/marker.png" width="16" height="17"/></a>
				<a href="<s:property value="menuSupport.eventsCityUrl"/>" title="<s:text name="nav.menu.cityEvents"/>"><img class="icon" src="/images/calendar.png" width="16" height="17"/></a>
			</s:if>
		</div>
	</div>
	<div id="search" class="col-sm-14">
		<form>
			<input type="hidden" id="searchType" value="<s:property value="headerSupport.searchType"/>"><input type="text" class="col-sm-7" name="search-geo" id="search-geo" value="<s:text name="navigation.search.geo"/>" onfocus="javascript:setfocus(this,'<s:text name="navigation.search.geo"/>');" onblur="javascript:lostfocus(this,'<s:text name="navigation.search.geo"/>');"> 
			<input type="text" class="col-sm-7" name="search-user" id="search-user" value="<s:text name="navigation.search.user"/>" onfocus="javascript:setfocus(this,'<s:text name="navigation.search.user"/>');" onblur="javascript:lostfocus(this,'<s:text name="navigation.search.user"/>');">
		</form>
	</div>
	<div id="actions" class="col-sm-5 last">
		<div class="flags"><s:iterator value="headerSupport.availableLanguages" var="lang">
			<a href="<s:property value="headerSupport.getAlternate(#lang)"/>"><img src="/images/flags/<s:property value="#lang"/>.png"></a>
		</s:iterator>
		</div>
		<s:if test="logged">
			<div>
				<a href="/disconnect.action"><img class="icon-border" src="/images/exit.png"/><s:text name="user.logout"/></a>
			</div>
		</s:if>
	</div>
</div>
