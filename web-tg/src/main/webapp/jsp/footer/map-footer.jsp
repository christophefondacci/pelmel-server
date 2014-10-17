<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="mapSupport != null">
<script type="text/javascript">
  function initialize() {
<s:if test="mapSupport.javascriptCentralPoint != null">
    var options = {
    	center: <s:property value="mapSupport.javascriptCentralPoint"/>,
    	zoom: <s:property value="mapSupport.zoomLevel"/>
    };
    Pelmel.initMap(options);
    <s:property value="mapSupport.javascriptMarkers" escapeHtml="false"/>
</s:if>
  }
  function loadScript() {
	  var script = document.createElement('script');
	  script.type = 'text/javascript';
	  script.src = 'https://maps.googleapis.com/maps/api/js?v=3.exp&' +
	      'callback=initialize&key=AIzaSyCYuOLtG75SxstqVQ7VamVD1_vlpETEeyY&amp;sensor=true';
	  document.body.appendChild(script);
  };
  $(document).ready(function() {
		loadScript();
	});
</script>
</s:if>