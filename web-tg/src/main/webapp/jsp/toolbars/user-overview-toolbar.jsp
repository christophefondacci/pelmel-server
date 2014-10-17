<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="overviewToolbox" class="overviewToolbox">
	<tiles:insertTemplate template="/jsp/blocks/block-ilike-button.jsp"/>
	<a class="button tool" rel="#overlay" href="<s:property value="overviewSupport.getToolbarActionUrl('msg',null)"/>"><img class="icon" src="/images/chat.png" title="<s:text name="message.button.title"/>"/><s:text name="toolbar.message"/></a>
	<a class="button tool"><img class="icon" src="/images/block.png"/><s:text name="toolbar.block"/></a>
</div>
<div id="status"></div>
<div id="overlay" class="overlay">
	<div class="contentWrap"><tiles:insertTemplate template="/jsp/blocks/block-write-message.jsp"/></div>
</div>
<script type="text/javascript">
$(function() {
	$("a[rel]").overlay({ mask: {
			color: '#ebecff',
			loadSpeed: 200,
			opacity: 0.9
		}, closeOnClick: true});
});
</script>