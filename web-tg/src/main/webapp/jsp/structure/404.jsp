<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<s:property value="headerSupport.language"/>" lang="<s:property value="headerSupport.language"/>">
<head>
	<meta name="robots" content="NOINDEX,NOFOLLOW"/>
	<title><s:text name="nav.error.404.title"/></title>
    <link href="/styles/main.css" rel="stylesheet" type="text/css" media="all"/>
</head>
<body id="page-home">
<tiles:insertTemplate template="/jsp/blocks/block-header.jsp"/>
<div class="page-container"><div id="pageContent" class="page">
	<div id="dropdown"></div>
	<tiles:insertTemplate template="/jsp/blocks/block-loginMenu.jsp"/>
	<div id="mainContent">
		<div id="mainCol" class="mainCol"><table><tr><td><img src="<s:property value="getImage('/images/dialog_warning.png')"/>"></td><td><h2><s:text name="nav.error.404.title"/></h2><s:text name="nav.error.404.desc"/></td></tr></table></div>
	</div>		
</div></div>
<tiles:insertTemplate template="/jsp/structure/footer.jsp"/>
</body>
</html>
