<!DOCTYPE html PUBLIC 
	"-//W3C//DTD XHTML 1.1 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="s" uri="/struts-tags" %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<title><decorator:title default="Struts Starter"/></title>
    <link href="/styles/main.css" rel="stylesheet" type="text/css" media="all"/>
    <link href="/styles/jquery-ui.css" rel="stylesheet" type="text/css" media="all"/>
<!--    <link href="/struts/niftycorners/niftyCorners.css" rel="stylesheet" type="text/css"/>-->
<!--    <link href="/struts/niftycorners/niftyPrint.css" rel="stylesheet" type="text/css" media="print"/>-->
<!--    <script language="JavaScript" type="text/javascript" src="/struts/niftycorners/nifty.js"></script>-->
    <script language="JavaScript" type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
    <script language="JavaScript" type='text/javascript' src="/js/jquery.form.js"></script>
    <script language="JavaScript" type='text/javascript' src="/js/jquery-ui.min.js"></script>
    <script type='text/javascript' src="/jwplayer/jwplayer.js"></script>
    <script type="text/javascript">

    	function call( callUrl, elementId ) {
			jQuery.get(callUrl,null,function(data) { document.getElementById(elementId).innerHTML = data; bindForms(); bindSliders(); } );
		}

   	</script>
    <decorator:head/>
</head>
<body id="page-home">
	<div id="blueBar" class="oph">
        	<div id="header" class="page ptm white clearfix">
			<div id="headerLeftCol" class="leftCol">
        			<div id="logo" class="logo">Togayther</div>
	        		<div id="quickLinks">&nbsp;beta</div>
			</div>
        		<div id="navigationHead">
        			<div id="search">Search</div>
        			<div id="actions">Actions</div>
        		</div>
	        </div>
      	</div>
	<div id="pageContent" class="page">
		<div id="dropdown"></div>
		<page:applyDecorator name="empty" page="/jsp/blocks/block-navMenu.jsp"></page:applyDecorator>
		<div id="mainContent"><decorator:body/></div>		
	</div>
        

</body>
</html>
