<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<div class="search-result-location"><s:text name="search.result.date"/>&nbsp;<s:property value="searchSupport.getCustomText(#result,'start')"/></div>
<div class="search-result-location"><s:text name="search.result.location.at"/>&nbsp;<a href="<s:property value="searchSupport.getResultLocalizationUrl(#result)"/>"><s:property value="searchSupport.getResultLocalizationName(#result)"/></a></div>