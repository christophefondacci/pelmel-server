<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set var="city" value="city"/>
<i><s:property value="#city.country.name"/><s:set var="adm1" value="#city.adm1"/><s:set var="adm2" value="#city.adm2"/>
<s:if test="#adm1 != null">&nbsp;>&nbsp;<s:property value="#adm1.name"/></s:if>
<s:if test="#adm2 != null">&nbsp;>&nbsp;<s:property value="#adm2.name"/></s:if>
&nbsp;>&nbsp;<b><s:property value="#city.name"/></b></i> 