<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="msg-instant-container col-sm-6 last msg-bubble-container">
			<form id="sendMsg" method="post">
				<input type="hidden" name="from" value="<s:property value="currentUserSupport.currentUser.key"/>">
				<input type="hidden" name="to" value="<s:property value="overviewSupport.getOverviewObject().getKey().toString()"/>">
				<div class="col-sm-6 last msg-instant-text-container">
					<textarea id="msgInstantTextArea" name="msgText" class="msg-instant-text" placeholder="<s:text name="message.area.defaultText"/>"></textarea>
					<div class="col-sm-6 msg-instant-buttons last">
						<a class="button msg-instant-cancel col-sm-3 <s:property value="messagingSupport.getMessagePageStyle()"/>" href="#"><s:text name="message.cancel"/></a> 
						<input type="submit" class="col-sm-3 button last <s:property value="messagingSupport.getMessagePageStyle()"/>" value="<s:text name="message.submit.shortLabel"/>">
					</div>
				</div>
				<div class="col-sm-5 msg-instant-arrow last">
					<div class="msg-arrow msg-bottom-arrow"><!--  --></div>
				</div>
			</form>
		</div>
<div class="action-bar <s:property value="#headerSupport.getPageStyle()"/>-alpha">
	<div id="user-toolbar" class="toolbar">
		<tiles:insertTemplate template="/jsp/toolbars/toolbar-picture-map-actions.jsp"/>
	</div>
	<div class="action-page-title hidden-xs"><s:property value="#overviewSupport.getTitle(#obj)" escapeHtml="false"/></div>
</div>