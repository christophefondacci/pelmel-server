<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="#searchSupport.getFacetRange('age')" var="ageRange"/>
<s:set value="#searchSupport.getCurrentFacetRange('age')" var="ageCurrentRange"/>
<s:set value="#searchSupport.getFacetRange('weight_kg')" var="weightRange"/>
<s:set value="#searchSupport.getCurrentFacetRange('weight_kg')" var="weightCurrentRange"/>
<s:set value="#searchSupport.getFacetRange('height_cm')" var="heightRange"/>
<s:set value="#searchSupport.getCurrentFacetRange('height_cm')" var="heightCurrentRange"/>
<s:set value="#searchSupport.getFacetRange('start_date')" var="dateRange"/>
<s:set value="#searchSupport.getCurrentFacetRange('start_date')" var="dateCurrentRange"/>
<s:if test="#dateRange!=null">
	<div class="options-container">
		<label class="col-xs-12"><s:text name="facets.section.age"/></label>
		<span class="col-xs-12 align-right"><span id="date-min-label"><s:property value="#dateRange.lowerBound"/></span> to <span id="date-max-label"><s:property value="#dateRange.higherBound"/></span></span>
		<input type="hidden" id="dateMin" value="<s:property value="#dateRange.lowerBound"/>">
		<input type="hidden" id="dateMax" value="<s:property value="#dateRange.higherBound"/>">
		<input type="hidden" id="dateCurrentMin" value="<s:property value="#dateCurrentRange.lowerBound"/>">
		<input type="hidden" id="dateCurrentMax" value="<s:property value="#dateCurrentRange.higherBound"/>">
		<input type="hidden" id="dateUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("date","_min_","_max_")'/>">
		<div id="date-slider"></div>
	</div>
</s:if>
<s:if test="#ageRange!=null && #weightRange!=null && #heightRange!=null">
	<div class="options-container">
		<label class="col-xs-12"><s:text name="facets.section.age"/></label>
		<span class="col-xs-12 align-right"><span id="age-min-label"><s:property value="#ageRange.lowerBound"/></span> to <span id="age-max-label"><s:property value="#ageRange.higherBound"/></span></span>
		<input type="hidden" id="ageMin" value="<s:property value="#ageRange.lowerBound"/>">
		<input type="hidden" id="ageMax" value="<s:property value="#ageRange.higherBound"/>">
		<input type="hidden" id="ageCurrentMin" value="<s:property value="#ageCurrentRange.lowerBound"/>">
		<input type="hidden" id="ageCurrentMax" value="<s:property value="#ageCurrentRange.higherBound"/>">
		<input type="hidden" id="ageUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("age","_min_","_max_")'/>">
		<div id="age-slider"></div>
		
		<label class="col-xs-12"><s:text name="facets.section.weight"/></label>
		<span class="col-xs-12 align-right"><span id="weight-min-label"><s:property value="#weightRange.lowerBound"/></span> to <span id="weight-max-label"><s:property value="#weightRange.higherBound"/></span>kg</span>
		<input type="hidden" id="weightMin" value="<s:property value="#weightRange.lowerBound"/>">
		<input type="hidden" id="weightMax" value="<s:property value="#weightRange.higherBound"/>">
		<input type="hidden" id="weightCurrentMin" value="<s:property value="#weightCurrentRange.lowerBound"/>">
		<input type="hidden" id="weightCurrentMax" value="<s:property value="#weightCurrentRange.higherBound"/>">
		<input type="hidden" id="weightUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("weight_kg","_min_","_max_")'/>">
		<div id="weight-slider"></div>
		
		<label class="col-xs-12"><s:text name="facets.section.size"/></label>
		<span class="col-xs-12 last align-right"><span id="height-min-label"><s:property value="#heightRange.lowerBound"/></span> to <span id="height-max-label"><s:property value="#heightRange.higherBound"/></span>cm</span>
		<input type="hidden" id="heightMin" value="<s:property value="#heightRange.lowerBound"/>">
		<input type="hidden" id="heightMax" value="<s:property value="#heightRange.higherBound"/>">
		<input type="hidden" id="heightCurrentMin" value="<s:property value="#heightCurrentRange.lowerBound"/>">
		<input type="hidden" id="heightCurrentMax" value="<s:property value="#heightCurrentRange.higherBound"/>">
		<input type="hidden" id="heightUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("height_cm","_min_","_max_")'/>">
		<div id="height-slider"></div>
	</div>
</s:if>



