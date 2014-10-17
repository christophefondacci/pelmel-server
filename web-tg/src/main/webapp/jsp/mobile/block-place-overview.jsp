<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="overviewSupport" var="overviewSupport"/>
<s:if test="#overviewSupport!=null">
<div>
<s:set value="#overviewSupport.overviewObject" var="obj"/>
<s:if test="mediaProvider.currentMedia!=null"><div class="overview-thumb"><img src="<s:property value="mediaProvider.getMediaThumbUrl(mediaProvider.currentMedia)"/>"/></div></s:if>
<div class="place-details"><h5><img class="title-icon" src="<s:property value="#overviewSupport.getTitleIconUrl(#obj)"/>"/><s:property value="#overviewSupport.getTitle(#obj)"/></h5>
<tiles:insertTemplate template="/jsp/blocks/block-tags.jsp"/>
<p class="address"><s:property value="#overviewSupport.getAddress(#obj)"/></p>
</div></div>
<h3 class="description">Descriptions</h3>
<ul id="desc-list" data-role="listview">
<s:set value="descriptionSupport" var="descriptionSupport"/> 
<s:iterator value="#descriptionSupport.items" var="desc"/>
	<li>
		<p><s:property value="#descriptionSupport.getItemDescription(#desc)"/></p>
		<p class="ui-li-aside"><img src="<s:property value="#descriptionSupport.getItemIconUrl(#desc)"/>"/></p>
	</li>
</ul>

</s:if>