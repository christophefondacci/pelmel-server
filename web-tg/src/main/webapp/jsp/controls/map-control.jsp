<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div id="map-controls" class="hidden-xs col-sm-offset-14 last action-map-controls <s:property value="#display ? '' : 'nodisplay'"/>">
	<span class="map-control map-minimize"><!-- --></span>
	<span class="map-control map-enlarge"><!-- --></span>
	<span class="map-control map-close"><!-- --></span>
</div>
<div class="col-xs-24 col-sm-offset-14 col-sm-5 action-map-container <s:property value="#display ? '' : 'nodisplay'"/>">
	<div class="action-map"><div class="map" id="map"></div> </div>
</div>