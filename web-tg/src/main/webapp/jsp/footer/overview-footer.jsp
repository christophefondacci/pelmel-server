<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<tiles:insertTemplate template="/jsp/footer/map-footer.jsp"/>
<tiles:insertTemplate template="/jsp/footer/common-footer.jsp"/>
<div class="fb-like" id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/<s:property value="locale"/>/all.js#xfbml=1&appId=302478486488872";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<script type="text/javascript">
  (function() {
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
    po.src = 'https://apis.google.com/js/platform.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
  })();
</script>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="https://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
<script type="text/javascript">
	$(function() {
   		Pelmel.bindForms();
   		var containerName = "<s:property value="overviewSupport.getToolbarActionUrl('like','like-container')"/>";
   		Pelmel.bindLike(containerName);
   	    $(window).resize(function() {
   	        Pelmel.centerImageVertically();
   	    });
   	});
Pelmel.imageOverlay=true;
$(document).ready(function() {
	Pelmel.initOverview();
	Pelmel.centerImageVertically();
// 	Pelmel.bindModals();
});
</script>