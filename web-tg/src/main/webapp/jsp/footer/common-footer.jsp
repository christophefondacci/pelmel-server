<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<script type="text/javascript">
var center;
$(function () {
	var type = '<s:property value="localizationSupport.getSearchCalType()"/>';
	var subtype = '<s:property value="localizationSupport.getSearchType().getSubtype()"/>';
	Pelmel.bindDropdowns(type,subtype);
});
</script>