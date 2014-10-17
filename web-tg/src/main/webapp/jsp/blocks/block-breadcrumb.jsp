<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<div class="breadcrumb-elt">
	<a class="geo-link" href="<s:property value="#localization.getAjaxSearchUrl(#localization.continent)"/>"><s:property value="#localization.getName(#localization.continent)"/></a>
</div>
<s:set value="#localization.getContinent().getKey()" var="parentId"/>
<s:if test="#localization.country != null">
	<span class="breadcrumb-sep">/</span>
	<div class="breadcrumb-elt">
		<a class="geo-link" href="<s:property value="#localization.getAjaxSearchUrl(#localization.country)"/>">
			<s:property value="#localization.getName(#localization.country)"/>
		</a>
		<s:if test="#country != null">
			<s:set value="#country.getKey()" var="parentId"/>
		</s:if>
	</div>
</s:if>
<s:set var="adm1" value="#localization.adm1"/>
<s:if test="#adm1 != null">
	<span class="breadcrumb-sep">/</span>
	<div class="breadcrumb-elt">
		<a class="geo-link" href="<s:property value="#localization.getAjaxSearchUrl(#adm1)"/>">
			<s:property value="#localization.getName(#adm1)"/>
		</a>
		<s:if test="#adm1 != null">
			<s:set value="#adm1.getKey()" var="parentId"/>
		</s:if>
	</div>
</s:if>
<s:if test="#localization.city != null">
	<span class="breadcrumb-sep">/</span>
	<div class="breadcrumb-elt">
		<a class="geo-link" href="<s:property value="#localization.getAjaxSearchUrl(#localization.city)"/>">
			<s:property value="#localization.getName(#localization.city)"/>
		</a>
		<s:if test="#city != null">
			<s:set value="#city.getKey()" var="parentId"/>
		</s:if>
	</div>
</s:if>
<s:if test="#localization.currentItem != null && (#localization.searchType==null || 'EVENTS'.equals(#localization.searchType.name()) )  && !'CITY'.equals(#localization.currentItem.key.type)">
	<span class="breadcrumb-sep">/</span>
	<a class="geo-link" href="<s:property value="#localization.getAjaxSearchUrl(#localization.currentItem)"/>">
		<s:property value="#localization.getName(#localization.currentItem)"/>
	</a>
</s:if>
