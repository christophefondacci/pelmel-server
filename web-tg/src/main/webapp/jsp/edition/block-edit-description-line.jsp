<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="row form-group">
	<div class="col-xs-24">
		<select name="descriptionLanguageCode" class="col-sm-12 select-lang">
			<option value="0"></option>
			<s:set value="#descriptionsEditionSupport.getDescriptionLanguage(#desc)" var="descLangCode"/>
			<s:iterator value="#descriptionsEditionSupport.availableLanguages" var="languageCode">
				<option class="flag-option" value="<s:property value="#languageCode"/>" <s:property value='#descriptionsEditionSupport.getSelectedAttribute(#languageCode,#descLangCode)' escapeHtml="false"/>><s:property value="#descriptionsEditionSupport.getLanguageName(#languageCode)"/></option>
			</s:iterator>
		</select>
	</div>
	<div class="col-xs-24">
		<input type="hidden" name="descriptionKey" value="<s:property value="#desc == null ? 0 : #desc.key"/>"/>
		<input type="hidden" name="descriptionSourceId" value="<s:property value="#desc == null ? 1000 : #desc.sourceId"/>"/>
		<textarea class="form-control" id="description" name="description"><s:property value="#descriptionsEditionSupport.getDescription(#desc)"/></textarea>
	</div>
</div>