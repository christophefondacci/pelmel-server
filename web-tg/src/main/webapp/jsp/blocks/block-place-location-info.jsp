<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<div class="search-result-location"><span style="display:block;"><a href="<s:property value="searchSupport.getResultLocalizationUrl(#result)"/>"><s:property value="searchSupport.getResultLocalizationName(#result)"/></a></span>
<s:if test="nearbySearchSupport.isDistanceAvailable(#result)"><span><s:property value="nearbySearchSupport.getDistanceLabel(#result)"/></span></s:if>
</div>