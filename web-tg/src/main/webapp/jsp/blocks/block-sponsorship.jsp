<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="sponsorshipSupport" var="sponsorshipSupport"/>
<form id="sponsorForm" action="<s:property value="#sponsorshipSupport.getValidationUrl()"/>" method="post">
<input type="hidden" name="sponsoredItemKey" value="<s:property value="#sponsorshipSupport.getObject().getKey().toString()"/>">
<input type="hidden" name="transactionId" value="<s:property value="#sponsorshipSupport.getTransactionId()"/>">
<h1 id="popup-title" class="section-title category-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="sponsor.popup.title"/></h1>
<div id="dialog" class="container">
	<div class="prepend-top col-sm-offset-1 col-sm-14 append-bottom">
		<span class="col-sm-14 append-bottom"><s:property value="#sponsorshipSupport.getSponsorshipLabel()"/></span>
		<span class="col-sm-14 append-bottom"><s:property value="getText('sponsor.rules')" escapeHtml="false"/></span>
		<div class=" col-sm-14 center user-credits"><s:property value="#sponsorshipSupport.getCreditsLabel()"/></div>
		<div class="prepend-top col-sm-offset-1 col-sm-12">
			<span class="col-sm-6 credits-text"><s:text name="sponsor.duration.label"/></span>
			<select class="col-sm-4" name="duration">
				<s:iterator value="#sponsorshipSupport.getAvailableSponsorshipDuration()" var="duration">
					<option value="<s:property value="#duration"/>"><s:property value="#sponsorshipSupport.getDurationLabel(#duration)"/></option>
				</s:iterator>
			</select>
			<span class="col-sm-6 credits-text"><s:text name="sponsor.monthCredits.label"/></span>
			<select class="col-sm-4" name="monthCredits">
				<s:iterator value="#sponsorshipSupport.getAvailableMonthCredits()" var="monthCredits">
					<option value="<s:property value="#monthCredits"/>"><s:property value="#sponsorshipSupport.getMonthCreditsLabel(#monthCredits)"/></option>
				</s:iterator>
			</select>
		</div>
		<div class="prepend-top col-sm-offset-10 col-sm-4 text-right">
			<input class="col-sm-4 last button <s:property value="headerSupport.getPageStyle()"/>" type="submit" value="Proceed">
		</div>
	</div>
</div>
</form>
