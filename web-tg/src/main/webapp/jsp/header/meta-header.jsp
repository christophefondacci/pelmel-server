<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="headerSupport" var="hs"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="robots" content="<s:property value="#hs.robotsTags"/>"/>
<meta name="description" content="<s:property value="#hs.description"/>"/>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=0" />
<title><s:property value="#hs.title"/></title>
<s:if test="#hs.canonical!=null">
<link rel="canonical" href="<s:property value="#hs.canonical"/>"/>
</s:if>
<s:iterator value="#hs.availableLanguages" var="lang">
<s:if test="#lang!=#hs.language">
<link rel="alternate" hreflang="<s:property value="#lang"/>" href="<s:property value="#hs.getAlternate(#lang)"/>"/>
</s:if>
</s:iterator>
<tiles:insertTemplate template="/jsp/header/meta-fb-header.jsp"/>
<tiles:insertTemplate template="/jsp/header/analytics-header.jsp"/>