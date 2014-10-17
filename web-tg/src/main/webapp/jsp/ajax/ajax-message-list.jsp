<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="myMessagingSupport" var="myMessagingSupport"/>
<div>
	<h2 class="section-title main-info-title"><s:property value="#myMessagingSupport.getMessageTitle()"/></h2>
	<s:set value="currentUserSupport.getCurrentUser().getKey().toString()" var="myKey"/>
	<form id="sendFullMsg" method="post">
		<input type="hidden" name="from" value="<s:property value="currentUserSupport.currentUser.key"/>">
		<input type="hidden" name="to" value="<s:property value="from"/>">
		<div class="prepend-top col-sm-offset-1 media">
			<img class="pull-left" src="<s:property value="currentUserSupport.getCurrentUserMedia().getMiniThumbUrl()"/>">
			<div class="msg-bubble-text media-body">
				<div class="msg-padding">
					<textarea id="msgInstantTextArea" name="msgText" class="form-control msg-reply-text" placeholder="<s:text name="message.area.defaultReplyText"/>"></textarea>
				</div>
				<input type="submit" class="col-xs-12 col-sm-6 button last msg-reply-button <s:property value="messagingSupport.getMessagePageStyle()"/>" value="<s:text name="message.submit.shortLabel"/>">
			</div>
			<div class="msg-small-arrow msg-arrow-left"><!--  --></div>
		</div>
	</form>
	<s:iterator value="#myMessagingSupport.messages" var="msg">
		<s:set value="#myMessagingSupport.getFromKey(#msg)" var="fromKey"/>
		<s:if test="#myKey.equals(#fromKey)">
			<div class="prepend-top col-xs-offset-6 col-xs-19 media msg-my-container">
				<div class="row">
					<div class="msg-bubble-text msg-left media-body col-xs-18">
						<div class="msg-padding">
							<span class="<s:property value="#myMessagingSupport.getMessagePageStyle()"/>-text"><s:property value="#myMessagingSupport.getDateText(#msg)"/></span>
							<p>		
								<s:iterator value="#myMessagingSupport.getMessageText(#msg)" var="msgLine">
									<s:property value="#msgLine"/><br>
								</s:iterator>
							</p>
						</div>
						<div class="msg-small-arrow msg-arrow-right"><!--  --></div>
					</div>
					<a class="pull-right col-xs-6" href="<s:property value="#myMessagingSupport.getFromUrl(#msg)"/>"><img class="comment-thumb list-item-thumb msg-left" src="<s:property value="#myMessagingSupport.getFromIconUrl(#msg)"/>"></a>
				</div>
			</div>
		</s:if><s:else>
			<div class="prepend-top col-sm-offset-1 col-xs-24 msg-other-container media">
				
				<a class="pull-left" href="<s:property value="#myMessagingSupport.getFromUrl(#msg)"/>"><img class="comment-thumb list-item-thumb" src="<s:property value="#myMessagingSupport.getFromIconUrl(#msg)"/>"></a>
				<div class="msg-bubble-text msg-right media-body">
					<div class="msg-padding">
						<span class="<s:property value="#myMessagingSupport.getMessagePageStyle()"/>-text"><s:property value="#myMessagingSupport.getDateText(#msg)"/></span>
						<p>		
							<s:iterator value="#myMessagingSupport.getMessageText(#msg)" var="msgLine">
								<s:property value="#msgLine"/><br>
							</s:iterator>
						</p>
					</div>
				</div>
				<div class="msg-small-arrow msg-arrow-left"><!--  --></div>
			</div>
		</s:else>
	</s:iterator>
</div>

<%-- <s:set value="myMessagingSupport" var="myMessagingSupport"/> --%>
<!-- <div id="mainCol" class="mainCol"> -->
<%-- <div class="message-title"><s:property value="#myMessagingSupport.getMessageTitle()"/></div> --%>
<!-- <div id="message-post-container" class="message-wrapper"><form id="message-post-form" method="post"> -->
<%-- <input type="hidden" name="to" value="<s:property value="from"/>"/> --%>
<%-- <img class="list-item-thumb" src="<s:property value="currentUserSupport.currentUserMedia.miniThumbUrl"/>"> --%>
<%-- <div class="minithumb-text"><textarea id="comment-text" class="comment-text field" name="msgText" onfocus="javascript:focusComment(this,'<s:text name="message.area.defaultReplyText"/>');" onblur="javascript:blurComment(this,'<s:text name="message.area.defaultReplyText"/>');"><s:text name="message.area.defaultReplyText"/></textarea></div>	 --%>
<%-- <div id="message-post-spinner">&nbsp;</div><input id="msg-submit" type="submit" class="button" value="<s:text name="message.submit.label"/>"> --%>
<!-- </form></div> -->
<%-- <tiles:insertTemplate template="/jsp/ajax/ajax-messages.jsp"/> --%>
<!-- </div> -->