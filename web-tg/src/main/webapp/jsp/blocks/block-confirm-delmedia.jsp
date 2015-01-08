<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
<form method="post" action="/deleteMedia.action">

<input type="hidden" name="confirmed" value="true">
<input type="hidden" name="id" value="<s:property value="id"/>">
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="media.deletion.title"/></h1>
<div id="dialog" class="row">
	<div class="prepend-top col-xs-offset-1 col-xs-22">
	<div class="row">
<s:if test="errorMessage!=null">
	<div class="col-xs-6">
		<img src="/images/dialog-warning.png"/>
	</div>
	<div class="col-xs-18"><s:property value="message"/></div>
</s:if><s:else>
	<div class="col-xs-6">
		<img src="<s:property value="getMediaUrl(media.thumbUrl)"/>"/>
	</div>
	<div class="col-xs-18">
		<s:text name="media.deletion.text"/>
	</div>
	<div class="clearfix col-xs-offset-10 col-xs-14 append-bottom">
		<input type="submit" class="button align-right <s:property value="headerSupport.getPageStyle()"/>" value="<s:text name="media.deletion.button"/>"/>
	</div>
</s:else>
</div>
	</div>
</div>
</form>