<%@ page language="java" contentType="text/json; charset=UTF-8"
    pageEncoding="UTF-8"%><%-- 
--%><%@taglib prefix="s" uri="/struts-tags"%><%-- 
--%><s:property value="safeJson" escapeHtml="false"/><%-- 
--%><%-- <s:iterator value="cities" id="city"> 
<li><b><s:property value="#city.name"/></b>&nbsp;(<s:property value="#city.country.name"/><s:set name="adm1" value="#city.adm1"/><s:set name="adm2" value="#city.adm2"/>
	<s:if test="#adm1 != null">&nbsp;-&nbsp;<s:property value="#adm1.name"/></s:if><s:if test="#adm2 != null">&nbsp;-&nbsp;<s:property value="#adm2.name"/></s:if>)</li> 
</s:iterator>
--%>