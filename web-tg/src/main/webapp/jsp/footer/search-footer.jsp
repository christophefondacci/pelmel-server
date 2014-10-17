<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertTemplate template="/jsp/footer/default-footer.jsp"/>
<script type="text/javascript" src="/js/togayther-search.js?v=3"></script>
<tiles:insertTemplate template="/jsp/footer/map-footer.jsp"/>
<script>
$(document).ready(function() {
	Pelmel.initTooltips();
});
</script>