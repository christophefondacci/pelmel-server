<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="commentSupport" var="commentSupport"/>
<s:set value="headerSupport" var="headerSupport"/>
<s:if test="#commentSupport.isEnabled()">
	<form id="comment-post-form" action="/postComment.action" method="post">
		<div id="comment-post-container" class="media">
			<input type="hidden" name="commentItemKey" value="<s:property value="#commentSupport.commentedItemKey"/>"/>
			<img class="comment-thumb pull-left" src="<s:property value="#commentSupport.currentUserThumbUrl"/>" alt="<s:property value="#commentSupport.getAuthor(#comment)"/> comment">
			<div class="media-body">
				<textarea id="comment-text" class="form-control col-xs-24 <s:property value="#headerSupport.getPageStyle()"/>-container" name="comment" placeholder="<s:text name="comment.default.text"/>" onfocus="Pelmel.focusComment(this);" onblur="Pelmel.blurComment(this);"></textarea>
				<div id="comment-post-spinner" class="invisible">&nbsp;</div>
				<input id="comment-submit" type="submit" class="button <s:property value="#headerSupport.getPageStyle()"/> right" value="<s:text name="comment.submit.label"/>">
			</div>	
		</div>
	</form>
</s:if>
<s:if test="!#commentSupport.comments.isEmpty()">
<s:iterator value="#commentSupport.comments" var="comment">
	<div class="comment-container media">
		<a class="pull-left" href="<s:property value="#commentSupport.getAuthorLinkUrl(#comment)"/>"><img class="comment-thumb list-item-thumb" src="<s:property value="#commentSupport.getCommentIconUrl(#comment)"/>" alt="<s:property value="#commentSupport.getAuthor(#comment)"/> comment"></a>
		<div class="media-body">
			<div class="comment-body">
				<a class="activity-user" href="<s:property value="#commentSupport.getAuthorLinkUrl(#comment)"/>"><s:property value="#commentSupport.getAuthor(#comment)"/></a>
				<s:property value="#commentSupport.getMessage(#comment)"/>
			</div>
			<span class="timestamp"><s:property value="#commentSupport.getDate(#comment)"/></span>
		</div>
	</div>
</s:iterator>
<s:set var="paginationSupport" value="#commentSupport"/> 
<s:set var="theme" value="'green'"/>
<div class="comment-pages prepend-top"><tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"/></div>
</s:if>
