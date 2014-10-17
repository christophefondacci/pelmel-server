<%@ taglib prefix="s" uri="/struts-tags" %><%-- 
 --%><%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %><%-- 
 --%><?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"><%-- 
 --%><s:set value="sitemapEntries" var="sitemapEntries" /><%-- 
 --%><s:iterator value="#sitemapEntries" var="sitemapEntry"><%-- 
 --%><sitemap><loc><s:property value="#sitemapEntry.url" escapeHtml="false" escapeXml="true"/></loc><%--
 --%><lastmod><s:property value="#sitemapEntry.lastModification"/></lastmod></sitemap><%-- 
 --%></s:iterator><%-- 
 --%></sitemapindex>
