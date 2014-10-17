<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:if test="#listSupport!=null">
<div id="items-title" class="boxTitle">
<s:if test="#listSupport.getActionUrl()!=null">
<a id="title-action" class="button tool" rel="#overlay" href="<s:property value="#listSupport.getActionUrl()"/>"><img class="icon" src="<s:property value="#listSupport.getActionIconUrl()"/>"/><s:property value="#listSupport.getActionText()"/></a>
</s:if>
<s:property value="#listSupport.boxTitle"/></div>
<div id="items-list" class="box">
<s:if test="!#listSupport.items.isEmpty()">
<s:iterator value="#listSupport.items" var="item">
	<s:set value="#listSupport.getLangAttribute(#item)" var="langAttr"/>
	<s:set value="#listSupport.getItemUrl(#item)" var="itemUrl"/>
	<div class="list-item-wrapper"><table><tr><td class="thumb-col"><s:if test="#listSupport.getItemIconUrl(#item)!=null"><s:if test="#itemUrl!=null"><a href="<s:property value="#itemUrl"/>"></s:if><img class="list-item-thumb" src="<s:property value="#listSupport.getItemIconUrl(#item)"/>"><s:if test="#itemUrl!=null"></a></s:if></s:if></td>
		<td <s:property value="#langAttr" escapeHtml="false"/> class="list-item"><s:if test="#listSupport.getItemTitle(#item)!=null"><p class="list-item-text"><b><s:property value="#listSupport.getItemTitlePrefix(#item)"/></b><s:if test="#itemUrl!=null"><a class="list-item-title" href="<s:property value="#itemUrl"/>"></s:if><s:property value="#listSupport.getItemTitle(#item)"/><s:if test="#itemUrl!=null"></a></s:if></p></s:if>
		<p class="list-item-text"><s:property value="#listSupport.getItemDescription(#item)"/></p></td></tr></table>
	</div>
</s:iterator>
</s:if><s:else><div class="padded"><s:text name="block.noinfo"/></div></s:else>
</div>
</s:if>