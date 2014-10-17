<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="mosaicSupport" var="mosaicSupport"/>
<s:set value="searchSupport" var="searchSupport"/>
<s:set var="popularSupport" value="latestChangesPopularSupport"/>
<div class="mosaic-table">
	<s:iterator begin="1" end="7" step="1" var="row">
		<s:iterator begin="1" end="7" step="1" var="col">
			<s:set value="#mosaicSupport.getElementAt(#row,#col)" var="mosaicObj"/>
			<s:set value="#popularSupport.getObjectFromActivity(#mosaicObj)" var="result"/>
<%-- 			<tiles:insertTemplate template="/jsp/pages/search-content-block.jsp"/> --%>
			<div class="mosaic-tile" id="<s:property value="#row + '-' + #col"/>">
				<s:if test="#mosaicObj!=null">
					<s:set value="#mosaicSupport.getImageUrl(#row,#col)" var="mosaicImg"/>
					<s:if test="#mosaicImg != null">
						<a href="<s:property value="mosaicSupport.getLinkUrlAt(#row,#col)"/>"><img class="mosaic-img" src="<s:property value="#mosaicImg"/>"/></a>
					</s:if><s:else>
						<div class="mosaic-img mosaic-filler <s:property value="headerSupport.getPageStyle()"/>"></div>
					</s:else>
				</s:if><s:else>
					<div class="mosaic-img mosaic-filler"></div>
				</s:else>
				<s:if test="#mosaicObj!=null">
					<s:set value="#mosaicSupport.getTooltipText(#row,#col)" var="tooltip"/>
					<s:if test="#tooltip!=null">
						<div id="info-<s:property value="#row + '-' + #col"/>" class="tile-info">
							<img id="img-<s:property value="#row + '-' + #col"/>" class="tile-info-arrow" src="/images/V2/pointe_boule.gif">
							<div id="content-<s:property value="#row + '-' + #col"/>" class="tile-info-content"><s:property value="#tooltip" escapeHtml="false"/></div>
						</div>
					</s:if>	
				</s:if>
			</div>
		</s:iterator>
	</s:iterator>
</div>
<s:set value="#mosaicSupport.getMosaicTitle()" var="title"/>
<s:if test="#title!=null">
	<div class="mosaic-sizer main-info-title section-title"><s:property value="#title"/></div>
</s:if>