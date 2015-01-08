<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

  	<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
<form id="addMediaForm" action="/addMedia.action" method="post" enctype="multipart/form-data">
<input type="hidden" id="redirectUrl" name="redirectUrl" value="<s:property value="redirectUrl"/>">
<input type="hidden" name="parentKey" value="<s:property value="currentObjectKey"/>">
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="media.panel.addMediaTitleBox"/></h1>
<div id="dialog" class="row">
	<div class="prepend-top col-sm-offset-1 col-sm-10 col-xs-offset-1 col-xs-22">
			<h2 class="section-title"><s:text name="media.panel.pickFileLabel"/></h2>
			<div class="prepend-top"><input type="file" name="media"></div>
		<div class="prepend-top">
			<div id="progress-container" class="progress active">
			  <div id="progress" class="progress-bar progress-bar-striped progress-bar-success" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" style="width: 0%">
			    0%
			  </div>
			</div>
		</div>
	</div>
	<div class="prepend-top col-sm-offset-1 col-sm-11 col-xs-offset-1 col-xs-22">
			<h2 class="section-title"><s:text name="media.panel.preview"/></h2>
		<div class="prepend-top">
			<div class="image-preview"></div>
		</div>
			<input type="checkbox" checked="checked" id="constrainRatio" name="constrainRatio"><label for="constrainRatio">Constrain image ratio</label>
	</div>
</div>
</form>
<div class="row">
	<form action="/cropMedia.action" method="post">
		<input type="hidden" id="redirectUrl" name="redirectUrl" value="<s:property value="redirectUrl"/>">
		<input type="hidden" id="formMediaKey" name="mediaKey" value="">
		<input type="hidden" id="previewCropX" name="cropx" value=""/>
		<input type="hidden" id="previewCropY" name="cropy" value=""/>
		<input type="hidden" id="previewCropW" name="cropw" value=""/>
		<input type="hidden" id="previewCropH" name="croph" value=""/>
		<div class="col-xs-offset-14 col-xs-9 last append-bottom">
			<input type="submit" id="addMedia" class="button <s:property value="headerSupport.getPageStyle()"/>" value="<s:text name="profile.upload"/>">
		</div>
	</form>
</div>
