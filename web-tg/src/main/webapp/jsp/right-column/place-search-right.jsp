<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="headerSupport.canonical" var="canonical"/>
<s:if test="#canonical!=null"><div class="fb-container"><div class="fb-like" data-href="<s:property value="#canonical"/>" data-send="false" data-layout="button_count" data-width="180" data-show-faces="false"></div></div></s:if>
<s:if test="childSupport.canAddChildFor('PLAC')">
<div class="space-top-bottom">
<a class="button tool" rel="#overlay" href="<s:property value="childSupport.getAddUrl('PLAC')"/>"><img class="icon" src="<s:property value="childSupport.getAddIconUrl('PLAC')"/>"/><s:property value="childSupport.getAddLabel('PLAC')"/></a>
</div>
<div id="overlay" class="overlay">
	<div id="contentWrap" class="contentWrap"></div>
</div>
<script type="text/javascript">
$(function() {
	$("a[rel]").overlay({ mask: {
			color: '#ebecff',
			loadSpeed: 200,
			opacity: 0.9
		}, closeOnClick: true, onBeforeLoad: function() {
			// grab wrapper element inside content
			var wrap = this.getOverlay().find("#contentWrap");
			if(wrap != null) {
				// load the page specified in the trigger
				wrap.load(this.getTrigger().attr("href"));
			}
		}, onLoad: function () {
			$("input#city").autocomplete({source : "/ajaxSuggestCity.action?type=CITY", minLength:2, select: function(event, ui) {
				document.getElementById("city").value=ui.item.userLabel;
				document.getElementById("cityId").value=ui.item.value;
				return false;
			}, focus: function(event, ui) { return false; }, html: true});
			var map = document.getElementById("place-map");
			if(map != null) {
				createGMap();
			}
		}, onClose: function() {
			document.getElementById("contentWrap").innerHTML="";
		}});
});
</script>
</s:if>
<s:set var="searchSupport" value="searchSupport"/>
<tiles:insertTemplate template="/jsp/blocks/block-searchFacets.jsp"/>
<s:set var="popularSupport" value="popularSupport"/>
<tiles:insertTemplate template="/jsp/blocks/block-popular.jsp"/>
