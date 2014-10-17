<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="headerSupport" var="hs"/>
<meta property="og:title" content="<s:property value="#hs.title"/>" />
<s:if test="#hs.facebookType!=null">
<meta property="og:type" content="<s:property value="#hs.facebookType"/>" />
</s:if>
<meta property="og:url" content="<s:property value="#hs.canonical"/>" />
<meta property="og:description" content="<s:property value="#hs.description"/>" />
<meta property="og:site_name" content="PELMEL Guide" />
<meta property="fb:admins" content="1267607899" />
<meta property="fb:app_id" content="302478486488872"/>
<s:if test="#hs.thumbUrl!=null">
<meta property="og:image" content="<s:property value="#hs.thumbUrl"/>" />
</s:if>