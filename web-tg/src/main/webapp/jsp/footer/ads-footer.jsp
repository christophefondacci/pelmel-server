<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%--
<s:if test="headerSupport.getRobotsTags().startsWith('INDEX') && isGoogleEnabled()">
<div class="stickybar">
	<script type="text/javascript"><!--
		google_ad_client = "ca-pub-9894675391208591";
		/* LeaderBoard */
		google_ad_slot = "0754076047";
		google_ad_width = 728;
		google_ad_height = 90;
		//-->
	</script>
	<script type="text/javascript" src="https://pagead2.googlesyndication.com/pagead/show_ads.js">
	</script>
	<span class='stickybar-close' ><!--  --></span>
	<script type="text/javascript">
		$(document).ready(function() {
			$(".stickybar").animate({"height":"90px"},500);
			$(".stickybar-close").click(function() {
				$(".stickybar").toggle();
			});
		});
	</script>
</div>
</s:if>
 --%>