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
		<span class="col-xs-12 align-right"><span id="age-min-label"><s:property value="#ageCurrentRange.lowerBound"/></span> to <span id="age-max-label"><s:property value="#ageCurrentRange.higherBound"/></span></span>
		<input type="hidden" id="ageUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("age","_min_","_max_")'/>">
		<div id="age-slider" data-slider-min="<s:property value="#ageRange.lowerBound"/>" data-slider-max="<s:property value="#ageRange.higherBound"/>" data-slider-value="[<s:property value="#ageCurrentRange.lowerBound"/>,<s:property value="#ageCurrentRange.higherBound"/>]" data-slider-tooltip="hide"></div>
		
		<label class="col-xs-12"><s:text name="facets.section.weight"/></label>
		<span class="col-xs-12 align-right"><span id="weight-min-label"><s:property value="#weightCurrentRange.lowerBound"/></span> to <span id="weight-max-label"><s:property value="#weightCurrentRange.higherBound"/></span>kg</span>
		<input type="hidden" id="weightUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("weight_kg","_min_","_max_")'/>">
		<div id="weight-slider" data-slider-min="<s:property value="#weightRange.lowerBound"/>" data-slider-max="<s:property value="#weightRange.higherBound"/>" data-slider-value="[<s:property value="#weightCurrentRange.lowerBound"/>,<s:property value="#weightCurrentRange.higherBound"/>]" data-slider-tooltip="hide"></div>
		
		<label class="col-xs-12"><s:text name="facets.section.size"/></label>
		<span class="col-xs-12 last align-right"><span id="height-min-label"><s:property value="#heightCurrentRange.lowerBound"/></span> to <span id="height-max-label"><s:property value="#heightCurrentRange.higherBound"/></span>cm</span>
		<input type="hidden" id="heightUrl" value="<s:property value='#searchSupport.getFacetRangeUrl("height_cm","_min_","_max_")'/>">
		<div id="height-slider" data-slider-min="<s:property value="#heightRange.lowerBound"/>" data-slider-max="<s:property value="#heightRange.higherBound"/>" data-slider-value="[<s:property value="#heightCurrentRange.lowerBound"/>,<s:property value="#heightCurrentRange.higherBound"/>]" data-slider-tooltip="hide"></div>
	</div>
</s:if>



