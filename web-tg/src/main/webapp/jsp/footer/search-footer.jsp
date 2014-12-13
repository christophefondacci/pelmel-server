<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertTemplate template="/jsp/footer/default-footer.jsp"/>
<tiles:insertTemplate template="/jsp/footer/map-footer.jsp"/>
<script>
$(document).ready(function() {
	Pelmel.initTooltips();
});
</script>