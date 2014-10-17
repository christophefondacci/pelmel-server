<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set value="headerSupport" var="hs"/>
<meta http-equiv="Content-Language" content="<s:property value="#hs.language"/>"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=0" />
<meta name="robots" content="NOINDEX,NOFOLLOW"/>
<title><s:property value="#hs.title"/></title>
<link rel="shortcut icon" type="image/x-icon" href="/images/favicon.ico">
<tiles:insertTemplate template="/jsp/header/meta-fb-header.jsp"/>