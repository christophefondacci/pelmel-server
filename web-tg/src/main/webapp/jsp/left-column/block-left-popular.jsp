<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:if test="#popularSupport!=null">
	<s:set value="#popularSupport.getPopularElements()" var="popularElts"/>
	<s:if test="#popularElts != null && #popularElts.size() > 1">
		<span class="hidden-xs side-subtext"><s:property value="#popularSupport.getTitle()"/></span>
		<div class="row hidden-xs">
			<s:iterator value="#popularElts" var="popularElt">
				<s:if test="!#popularElt.getKey().toString().equals(geoKey)">
						<span class="col-xs-offset-1 col-xs-2 col-sm-offset-2 col-sm-4 side-sublink side-count"><s:property value="#popularSupport.getCount(#popularElt)"/></span>
						<span class="col-xs-5 col-sm-18  side-sublink last">
							<a class="<s:property value="getHeaderSupport().getPageStyle()"/>-text" href="<s:property value="#popularSupport.getUrl(#popularElt)"/>"><s:property value="#popularSupport.getName(#popularElt)"/></a>
						</span>
				</s:if>
			</s:iterator>
		</div>
	</s:if>
</s:if>
