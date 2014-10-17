<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
<form action="/updatePlace.action" id="update-place-form" method="post">
<s:set value="placeEditionSupport" var="editionSupport"/>
<input type="hidden" name="placeId" value="<s:property value="#editionSupport.placeId"/>"/>
<input type="hidden" id='place-edit-lat' name="latitude" value="<s:property value="#editionSupport.latitude"/>"/>
<input type="hidden" id='place-edit-lng' name="longitude" value="<s:property value="#editionSupport.longitude"/>"/>
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="place.form.title"/></h1>
<div id="dialog" class="row">
	<div class="col-sm-offset-1 col-sm-10 col-xs-offset-1 col-xs-22">
		<div class="row edit-form">
			<div class="form-group">
				<label for="name"><s:text name="place.form.name"/></label>
				<input class="form-control" type="text" id="name" name="name" value="<s:property value="#editionSupport.name"/>" placeholder="<s:text name="place.form.name"/>">
			</div>
			<input type="hidden" name="cityId" id="cityValue" value="<s:property value="#editionSupport.cityId"/>"/>
			<div class="form-group">
				<label for="city"><s:text name="place.form.location"/></label>
				<input class="form-control" type="text" name="cityName" autocomplete="off" id="city" value="<s:property value="#editionSupport.cityName"/>" placeholder="<s:text name="place.form.location"/>"/>
			</div>
			<div class="form-group">
				<label for="placeType"><s:text name="place.form.placeType"/></label>
				<select id="placeType" name="placeType" class="form-control">
					<s:iterator value="#editionSupport.placeTypes" var="placeType">
						<option value="<s:property value="#placeType"/>" <s:property value="#editionSupport.getSelected(#placeType)"/>><s:property value="#editionSupport.getPlaceTypeLabel(#placeType)"/></option>
					</s:iterator>
				</select>
			</div>
			<div class="form-group">
				<label for="place-address"><s:text name="place.form.address"/></label>
				<textarea class="form-control field-address" id="place-address" name="address" placeholder="<s:text name="place.form.address"/>"><s:property value="#editionSupport.address"/></textarea>
			</div>
		</div>
		<tiles:insertTemplate template="/jsp/edition/block-edit-descriptions.jsp"/>
		<tiles:insertTemplate template="/jsp/edition/block-edit-properties.jsp"/>
	</div>
	<div class="col-sm-offset-1 col-sm-11 col-xs-offset-1 col-xs-22">
		<div class="row edit-form form-group">
			<label class="col-xs-24 last"><s:text name="place.form.amenities"/></label>
			<div id="amenities-edition" class="options-container">
				<s:iterator value="amenitiesTagSupport.getAvailableTags()" var="tag">
					<div>
						<input type="checkbox" name="tags" id="tag_<s:property value="#tag.code"/>" value="<s:property value="#tag.key"/>" <s:property value="tagSupport.isTagSelected(#tag) ? 'checked' : ''"/> >
						<label class="amenity-option" for="tag_<s:property value="#tag.code"/>"><s:property value="tagSupport.getTagTranslation(#tag)"/></label>
					</div>
				</s:iterator>
			</div>
		</div>
		<div class="form-group">
			<label><s:text name="place.form.tags"/></label>
			<tiles:insertTemplate template="/jsp/edition/block-edit-tags.jsp"/>
		</div>
	</div>
<!-- 	<div class="clearfix"></div> -->
<!-- 	<div class="prepend-top col-sm-offset-1 col-sm-10 col-xs-offset-1 col-xs-22"> -->

<!-- 	</div> -->

	<div class="prepend-top append-bottom col-sm-offset-17 col-sm-6 col-xs-offset-13 col-xs-10">
		<div class="col-sm-1" id="place-edit-spinner"></div>
		<input type="submit" class="right button <s:property value="headerSupport.getPageStyle()"/>" value="<s:text name="place.form.submit"/>"/>
	</div>
</div>
</form>
