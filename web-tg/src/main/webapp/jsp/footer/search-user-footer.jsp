<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertTemplate template="/jsp/footer/search-footer.jsp"/>
<script type="text/javascript" src="/js/jqFancyTransitions.1.8.min.js"></script>
<script>
$(document).ready(function() {
	bindSliders();
	$("#image-container").jqFancyTransitions({position:'top',direction:'left', width:750, height:346, navigation: true, links: true});
});

</script>