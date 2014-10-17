<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insertTemplate template="/jsp/footer/ajax-light-footer.jsp"/>
<tiles:insertTemplate template="/jsp/footer/common-footer.jsp"/>
<script type="text/javascript">
   	jQuery(document).ready(function() {
   		Pelmel.bindForms();
   	});
</script>