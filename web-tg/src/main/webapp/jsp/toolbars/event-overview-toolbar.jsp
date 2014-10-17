<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertTemplate template="/jsp/toolbars/common-overview-toolbar.jsp"/>
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
			$("input#place").autocomplete({source : "/ajaxSuggestCity.action?type=PLAC", minLength:2, select: function(event, ui) {
				document.getElementById("place").value=ui.item.userLabel;
				document.getElementById("placeId").value=ui.item.value;
				return false;
			}, focus: function(event, ui) { return false; },html:true});
			$("input#startDate").datepicker({dateFormat: 'yy/mm/dd'});
			$("input#endDate").datepicker({dateFormat: 'yy/mm/dd'});
		}, onClose: function() {
			document.getElementById("contentWrap").innerHTML="";
		}});
});
</script>