<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/<s:property value='locale'/>/all.js#xfbml=1&appId=302478486488872";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<s:set value="mediaProvider" var="mediaProvider"/> 
<div class="top-col home-top">
	<div class="row">
		<div class="col-sm-17 relative">
			<div class="hidden-xs hidden-sm col-md-12 home-headline">
			
				<h1 class="pelmel-slogan pelmel-text"><s:text name="pelmel.slogan"/></h1>
			</div>
			<div class="hidden-xs hidden-sm col-md-10">
				<form class="input-group full-width">
					<input class="form-control pelmel-search" type="text" placeholder="<s:text name="navigation.search.geo"/>" value="<s:property value="searchTerm"/>">
				</form>
			</div>
			<div class="hidden-xs hidden-sm col-md-2"><img class="search-icon" src="/images/V3/search-big.png" alt="<s:text name="navigation.search.alt"/>"/></div>
			<div class="col-sm-24 home-map" id="map">
			</div>
		</div>
		<div class="col-sm-7">
			<s:set value="activitySupport" var="activitySupport"/>
			<div class="main-info-container homepage-activity">
				<h3 class="section-title main-info-title" id="search-title"><s:property value="#activitySupport.title"/><span class="arrow"></span></h3>
				<div class="main-info-container-content homepage-content" id="<s:property value="#activitySupport.activityHtmlContentId"/>">
					<s:set var="activitySpan" value="''"/>
					<tiles:insertTemplate template="/jsp/ajax/ajax-activities-contents.jsp"/>
					<div id="activity-highlighter" class="activity-highlight"><span class="highlight-caret"></span></div>
				</div>
			</div>
		</div>
	</div>
</div>

