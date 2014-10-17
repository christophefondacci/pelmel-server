<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set var="calendarSupport" value="calendarSupport"/>
<s:if test="#calendarSupport!=null && !#calendarSupport.types.empty">
	<div class="row">
		<s:iterator value="#calendarSupport.types" var="calType">
			<div class="clearfix append-bottom"></div>
			<s:set value="#calendarSupport.getNextTimeLabel(#calType)" var="nextTimeLabel"/>
			<div class="col-xs-8">
				<s:if test="#nextTimeLabel!=null">
					<div class="event-hours <s:property value="#calendarSupport.getNextTimeCSSClass(#calType)"/>">
						<span class="hours-subtitle"><s:property value="#calendarSupport.getNextTimeSubtitle(#calType)"/></span>
						<span class="hours-title"><s:property value="#nextTimeLabel"/></span>
					</div>
				</s:if>
			</div>
			<div class="col-xs-16">
				<div class="row">
					<div class="col-xs-24 <s:property value="headerSupport.getPageStyle()"/>-text align-right prop-title">
						<a class="control-add <s:property value="headerSupport.getPageStyle()"/>-add" data-toggle="modal" data-target="#myModal" href="<s:property value="#calendarSupport.getAddCalendarUrl(#calType)"/>"></a>
						<span><s:property value="#calendarSupport.getCalendarTypeLabel(#calType)"/></span>
					</div>
					<div class="col-xs-24 align-right">
						<s:iterator value="#calendarSupport.getCalendarsFor(#calType)" var="series">
							<div class="full-width">
								<a data-toggle="modal" data-target="#myModal" rel="nofollow" href="<s:property value="#calendarSupport.getEditUrl(#series)"/>"><s:property value="#calendarSupport.getLabel(#series)"/></a>
							</div>
						</s:iterator>
					</div>
				</div>
			</div>
		</s:iterator>
	</div>
</s:if>