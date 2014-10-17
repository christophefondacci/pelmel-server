<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="eventsListSupport" var="eventsListSupport"/>
<s:if test="#eventsListSupport.getItems() != null && #eventsListSupport.getItems().size()>0">
	<h2 id="events-section" class="section-title main-info-title"><s:property value="#eventsListSupport.getBoxTitle()"/></h2>
	<s:set value="0" var="i"/>
	<s:iterator value="#eventsListSupport.getItems()" var="event">
		<div class="last event-box event-box-<s:property value="#i % 7"/>">
			<a class="event-title" href="<s:property value="#eventsListSupport.getItemUrl(#event)"/>"><s:property value="#eventsListSupport.getItemTitle(#event)"/></a>
			<span class="event-subtitle last"><s:property value="#eventsListSupport.getItemTitlePrefix(#event)"/></span>
			<span class="event-subtitle last"><s:property value="#eventsListSupport.getItemDescription(#event)"/></span>
		</div>
		<s:set value="#i+1" var="i"/>
	</s:iterator>
</s:if>