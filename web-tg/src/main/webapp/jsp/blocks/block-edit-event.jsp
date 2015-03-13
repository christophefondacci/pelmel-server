<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
<form action="/updateEvent.action" method="post">
	<s:set value="eventEditionSupport" var="editionSupport"/>
	<input type="hidden" name="eventId" value="<s:property value="#editionSupport.eventId"/>"/>
	<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:property value="#editionSupport.getTitle()"/></h1>
	<div id="dialog" class="row">
		<s:set value="#editionSupport.isNamed()" var="showDescriptions"/>
		<s:set value="#editionSupport.isRecurrencyForced()" var="recurrencyForced"/>
		<div class="col-xs-offset-1 <s:property value="#showDescriptions ? 'col-xs-10' : 'col-xs-22'"/>">
			<div class="row">
				<s:if test="#editionSupport.isNamed()">
					<div class="form-group">
						<label for="eventName"><s:text name="event.form.name"/></label>
						<input class="form-control" type="text" id="eventName" name="name" value="<s:property value="#editionSupport.name"/>"/>
					</div>
				</s:if>
				<div class="form-group">
					<input type="hidden" name="calendarType" value="<s:property value="#editionSupport.getCalendarType()"/>">
					<label class="col-xs-24"><s:if test="#showDescriptions"><s:text name="event.form.startDate"/></s:if><s:else><s:text name="event.form.startDate.opening"/></s:else></label>
					<s:if test="!#recurrencyForced">
						<div class="col-xs-12">
							<input class="form-control" type="text" name="startDate" id="startDate" value="<s:property value="#editionSupport.startDate"/>"/>
						</div>
<%-- 						<label class="col-xs-4 field-align">&nbsp;<s:text name="event.form.startTime"/></label> --%>
					</s:if>
					<div class="col-xs-6">
						<select class="form-control" name="startHour">
							<option value="-1"><s:text name="event.form.hour"/></option>
							<s:iterator begin="0" end="23" step="1" var="hour">
								<option <s:property value='#editionSupport.startHour==#hour ? "selected" : ""'/> value="<s:property value="#hour"/>">
									<s:if test="#hour<10">
										<s:property value='"0" + #hour'/>
									</s:if><s:else>
										<s:property value="#hour"/>
									</s:else>
								</option>
							</s:iterator>
						</select>
					</div>
					<div class="col-xs-6">
						<select class="form-control" name="startMinute">
							<option value="-1"><s:text name="event.form.minute"/></option>
							<s:iterator begin="0" end="45" step="15" var="min">
								<option <s:property value='#editionSupport.startMinute==#min ? "selected" : ""'/> value="<s:property value="#min"/>">
									<s:if test="#min<10">
										<s:property value='"0" + #min'/>
									</s:if><s:else>
										<s:property value="#min"/>
									</s:else>
								</option>
							</s:iterator>
						</select>
					</div>
					<div class="clearfix"></div>
				</div>
				<div class="form-group">
					<label class="col-xs-24"><s:if test="#showDescriptions"><s:text name="event.form.endDate"/></s:if><s:else><s:text name="event.form.endDate.opening"/></s:else></label>
					<s:if test="!#recurrencyForced">
						<div class="col-xs-12">
							<input class="form-control" type="text" name="endDate" id="endDate" value="<s:property value="#editionSupport.endDate"/>"/>
						</div>
					</s:if>
					<div class="col-xs-6">
						<select class="form-control" name="endHour">
							<option value="-1"><s:text name="event.form.hour"/></option>
							<s:iterator begin="0" end="23" step="1" var="hour">
								<option <s:property value='#editionSupport.endHour==#hour ? "selected" : ""'/> value="<s:property value="#hour"/>">
									<s:if test="#hour<10">
										<s:property value='"0" + #hour'/>
									</s:if><s:else>
										<s:property value="#hour"/>
									</s:else>
								</option>
							</s:iterator>
						</select>
					</div>
					<div class="col-xs-6">
						<select class="form-control" name="endMinute">
							<option value="-1"><s:text name="event.form.minute"/></option>
							<s:iterator begin="0" end="45" step="15" var="min">
								<option <s:property value='#editionSupport.endMinute==#min ? "selected" : ""'/> value="<s:property value="#min"/>">
									<s:if test="#min<10">
										<s:property value='"0" + #min'/>
									</s:if><s:else>
										<s:property value="#min"/>
									</s:else>
								</option>
							</s:iterator>
						</select>
					</div>
					<div class="clearfix"></div>
				</div>
				<input type="hidden" name="placeId" id="placeId" value="<s:property value="#editionSupport.placeId"/>"/>
				<s:if test="#editionSupport.isRelocalizable()">
					<div class="form-group">
						<label for="place"><s:text name="event.form.location"/></label>
							<input class="form-control" type="text" name="placeName" id="place" autocomplete="off" value="<s:property value="#editionSupport.placeName"/>">
					</div>
				</s:if>
				<s:if test="#editionSupport.isSeriesEnabled()">
					<div class="form-group">
						<s:if test="#recurrencyForced">
							<input type="hidden" name="monthRecurrency" value="0">
						</s:if><s:else>
							<label class="col-xs-24"><s:text name="event.form.monthrecurrency"/></label>
							<select class="form-control" name="monthRecurrency" class="col-xs-24">
								<option value="-999" <s:property value="#editionSupport.isRecurringFor(null) ? 'SELECTED' : ''"/>><s:text name="event.form.monthnone"/></option>
								<option value="0" <s:property value="#editionSupport.isRecurringFor(0) ? 'SELECTED' : ''"/>><s:text name="event.form.monthevery"/></option>
								<option value="1" <s:property value="#editionSupport.isRecurringFor(1) ? 'SELECTED' : ''"/>><s:text name="event.form.monthfirst"/></option>
								<option value="2" <s:property value="#editionSupport.isRecurringFor(2) ? 'SELECTED' : ''"/>><s:text name="event.form.monthsecond"/></option>
								<option value="3" <s:property value="#editionSupport.isRecurringFor(3) ? 'SELECTED' : ''"/>><s:text name="event.form.monththird"/></option>
								<option value="4" <s:property value="#editionSupport.isRecurringFor(4) ? 'SELECTED' : ''"/>><s:text name="event.form.monthfourth"/></option>
								<option value="-1" <s:property value="#editionSupport.isRecurringFor(-1) ? 'SELECTED' : ''"/>><s:text name="event.form.monthlast"/></option>
							</select>
						</s:else>
						<label class="col-xs-24"><s:text name="event.form.frequency"/></label>
						<div>
							<div class="col-xs-8"><input id="monday" type="checkbox" name="monday" value="true" <s:property value="#editionSupport.isMonday() ? 'CHECKED' : ''"/>/><label class="event-day" for="monday"><s:text name="event.form.monday"/></label></div>
							<div class="col-xs-8"><input id="tuesday" type="checkbox" name="tuesday" value="true" <s:property value="#editionSupport.isTuesday() ? 'CHECKED' : ''"/>/><label class="event-day" for="tuesday"><s:text name="event.form.tuesday"/></label></div>
							<div class="col-xs-8"><input id="wednesday" type="checkbox" name="wednesday" value="true" <s:property value="#editionSupport.isWednesday() ? 'CHECKED' : ''"/>/><label class="event-day" for="wednesday"><s:text name="event.form.wednesday"/></label></div>
							<div class="col-xs-8"><input id="thursday" type="checkbox" name="thursday" value="true" <s:property value="#editionSupport.isThursday() ? 'CHECKED' : ''"/>/><label class="event-day" for="thursday"><s:text name="event.form.thursday"/></label></div>
							<div class="col-xs-8"><input id="friday" type="checkbox" name="friday" value="true" <s:property value="#editionSupport.isFriday() ? 'CHECKED' : ''"/>/><label class="event-day" for="friday"><s:text name="event.form.friday"/></label></div>
							<div class="col-xs-8"><input id="saturday" type="checkbox" name="saturday" value="true" <s:property value="#editionSupport.isSaturday() ? 'CHECKED' : ''"/>/><label class="event-day" for="saturday"><s:text name="event.form.saturday"/></label></div>
							<div class="col-xs-24"><input id="sunday" type="checkbox" name="sunday" value="true" <s:property value="#editionSupport.isSunday()  ? 'CHECKED' : ''"/>/><label class="event-day" for="sunday"><s:text name="event.form.sunday"/></label></div>
						</div>
					</div>
				</s:if>
			</div>
		</div>
		<s:if test="#showDescriptions">
			<div class="col-xs-offset-1 col-xs-10">
				<s:set value="'col-sm-6'" var="spanclass"/>
				<tiles:insertTemplate template="/jsp/edition/block-edit-descriptions.jsp"/>
			</div>
		</s:if>
		<div class="col-xs-offset-15 prepend-top append-bottom col-xs-8">
			<input type="submit" class="button button-fullsize full-width <s:property value="headerSupport.getPageStyle()"/>" value="<s:text name="button.save"/>" />
		</div>
	</div>
</form>



