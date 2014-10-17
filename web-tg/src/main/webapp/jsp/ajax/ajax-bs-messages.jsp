<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div class="row-fluid">
<!--<div class="<s:property value="#cssClass"/>">-->
<div class="span8"><table class="table"><tbody>
<s:iterator value="#myMessagingSupport.messages" var="msg">
<s:set value='#msg.isUnread() ? "msg-selected" : ""' var="cssClass"/> 
	<tr class="<s:property value="#cssClass"/>"><td>
	<a href="<s:property value="#myMessagingSupport.getFromUrl(#msg)"/>"><img class="list-item-thumb" src="<s:property value="#myMessagingSupport.getFromIconUrl(#msg)"/>"></a>
	</td><td>
	<div id="msg" class="minithumb-text"><div class="message-body"><a class="message-from" href="<s:property value="#myMessagingSupport.getMessageReplyPageUrl(#msg)"/>"><s:property value="#myMessagingSupport.getFromText(#msg)"/></a>
		<s:iterator value="#myMessagingSupport.getMessageText(#msg)" var="msgLine">
			<s:property value="#msgLine"/><br>
		</s:iterator>
	</div>
	<span id ="msg" class="timestamp"><s:property value="#myMessagingSupport.getDateText(#msg)"/></span>
	</div></td><td>
		<i class="icon-share"></i>
		<i class="icon-minus-sign"></i>
		<i class="icon-trash"></i>
<!--	<s:iterator value="myMessagingSupport.tools" var="tool">-->
<!--	<s:if test="#myMessagingSupport.showTool(#msg,#tool)">-->
<!--		<div class="msg-tool"><a href="<s:property value="#myMessagingSupport.getToolUrl(#msg,#tool)"/>" title="<s:property value="#myMessagingSupport.getToolLabel(#tool)"/>"><img src="<s:property value="#myMessagingSupport.getToolIconUrl(#tool)"/>"/></a></div>-->
<!--	</s:if>-->
<!--	</s:iterator>-->
	</td>
	</tr>
</s:iterator></tbody></table>
	</div>
<div class="span4"></div>
</div>
<s:set var="paginationSupport" value="#myMessagingSupport"/> 
<div class="comment-pages"><tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"/></div>