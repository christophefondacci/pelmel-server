<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="currentUserSupport.isFavorite(overviewSupport.overviewObject) ? 'like-selected' : ''" var="cssclass"/>
<div class="add-container-bg like-count <s:property value="#headerSupport.getPageStyle()"/>-text">
	<div id="like-count" class="add-container"><s:property value="overviewSupport.getLikesCount()"/></div>
</div>
<div class="like-border">
	<span id="like" class="tool like <s:property value="#cssclass"/>"><!-- --></span>
</div>
<div class="like-border">
	<span id="dislike" class="tool dislike"><!-- --></span>
</div>
<div class="add-container-bg like-count dislikes <s:property value="#headerSupport.getPageStyle()"/>-text">
	<div id="dislike-count" class="add-container"><s:property value="overviewSupport.getDislikesCount()"/></div>
</div>
<%-- <span id="fav-button-wrapper"> --%>
<%-- <s:if test="currentUserSupport.isFavorite(overviewSupport.overviewObject)"> --%>
<%-- 	<a id="fav-button" class="button-pushed tool" href="<s:property value="overviewSupport.getToolbarActionUrl('like','fav-button-wrapper')"/>"><img id="likeaction" class="icon tiny" src="/images/fav2.png"/><s:property value="overviewSupport.getToolbarLabel('like')"/></a> --%>
<%-- </s:if><s:elseif test="logged"> --%>
<%-- 	<a class="button tool" href="<s:property value="overviewSupport.getToolbarActionUrl('like','fav-button-wrapper')"/>"><img id="likeaction" class="icon tiny" src="/images/fav2.png"/><s:property value="overviewSupport.getToolbarLabel('like')"/></a> --%>
<%-- </s:elseif><s:else> --%>
<%-- 	<span class="dis-button tool"><img id="likeaction" class="icon tiny" src="/images/fav2.png"/><s:property value="overviewSupport.getToolbarLabel('like')"/></span> --%>
<%-- </s:else> --%>
<%-- </span> --%>