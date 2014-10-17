<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<form id="sendMsg" method="post" onsubmit="javascript:document.getElementById('msgText').value=document.getElementById('msgTextArea').value">
	<input type="hidden" name="from" value="<s:property value="currentUserSupport.currentUser.key"/>">
	<input type="hidden" name="to" value="<s:property value="currentObjectKey"/>">
	<div id="popup-title" class="boxTitle"><s:text name="message.dialog.title"/></div>
	<div id="instantMsg">
		<div class="msgBubbleExt"><div class="msgBubbleIn">
		<textarea id="msgTextArea" class="msgArea bigMsg" onfocus="setfocus(this,'<s:text name="message.area.defaultText"/>'); this.style.color='black';" onblur="lostfocus(this,'<s:text name="message.area.defaultText"/>'); this.style.color='#BDC7D8';"><s:text name="message.area.defaultText"/></textarea>
		<input type="hidden" name="msgText" id="msgText" value="">
		<input class="button" type="submit">
		</div></div>
	</div>
</form>