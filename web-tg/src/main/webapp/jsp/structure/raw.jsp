<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="<s:property value="headerSupport.language"/>">
<head>
	<tiles:insertAttribute name="meta-header"/>
	<meta name="apple-itunes-app" content="app-id=603515989, app-argument=<s:property value="headerSupport.canonical"/>">
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:300,600,700' rel='stylesheet' type='text/css'>
	<!-- build:css(../web-static) /styles/pelmel-raw.min.css -->	
    <link rel="stylesheet" href="/styles/bootstrap-3.3.1/css/bootstrap.min.css" type="text/css">
    <link href="/styles/tg-main.css" rel="stylesheet" type="text/css" media="all"/>
    <!-- endbuild -->
    <link rel="stylesheet" href="<s:property value="getStaticUrl('/images/favicon.ico?v=5')"/>" />
    <link rel="icon" type="image/png" href="<s:property value="getStaticUrl('/images/favicon-32x32.png')"/>" sizes="32x32">
	<link rel="icon" type="image/png" href="<s:property value="getStaticUrl('/images/android-chrome-192x192.png')"/>" sizes="192x192">
	<link rel="icon" type="image/png" href="<s:property value="getStaticUrl('/images/favicon-96x96.png')"/>" sizes="96x96">
	<link rel="icon" type="image/png" href="<s:property value="getStaticUrl('/images/favicon-16x16.png')"/>" sizes="16x16">
	<link rel="shortcut icon" href="<s:property value="getStaticUrl('/images/favicon.ico?v=5')"/>">
    <tiles:insertAttribute name="header"/>
    <!--[if lt IE 9]>
	  <script src="http://static.pelmelguide.com/js/html5shiv.js"></script>
	  <script src="http://static.pelmelguide.com/js/respond.min.js"></script>
	<![endif]-->
		
</head>
<body id="page-home" class="<tiles:insertAttribute name="background"/>">
<s:set value="headerSupport" var="headerSupport"/>
<s:set value="headerSupport" var="headerSupport"/>
<s:set value="homepageUrl" var="homeUrl"/>
<div id="wrap">

<div class="white top">
	<div class="container">
		<div class="space-top-content row append-bottom">
			<tiles:insertAttribute name="content"/>
		</div>
	</div>
</div>
</div>
</body>
</html>
