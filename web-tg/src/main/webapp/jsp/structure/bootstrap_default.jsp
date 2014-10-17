<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link href="/css/bootstrap.css" rel="stylesheet" type="text/css"/>
	<link href="/styles/main.css" rel="stylesheet" type="text/css" media="all"/>
	<style type="text/css">
      body {
        padding-top: 0px;
        padding-bottom: 0px;
      }
    </style>
	<link href="/css/docs.css" rel="stylesheet" type="text/css"/>
	<link href="/css/bootstrap-responsive.css" rel="stylesheet" type="text/css"/>
	
    <link href="/styles/jquery-ui.css" rel="stylesheet" type="text/css" media="all"/>
    <script language="JavaScript" type="text/javascript" src="/js/jquery-1.7.1.min.js"></script>
    <script type='text/javascript' src="/js/togayther.js"></script>
    <script language="JavaScript" type='text/javascript' src="/js/jquery.form.js"></script>
    <tiles:insertAttribute name="header"/>
    <script language="JavaScript" type="text/javascript" src="http://twitter.github.com/bootstrap/assets/js/bootstrap-scrollspy.js"></script>
    
</head>
<body data-spy="scroll" data-target=".subnav" data-offset="50">
	<div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">Togayther</a>
          <div class="nav-collapse">
            <ul class="nav">
              <li class="active"><a href="#">Home</a></li>
              <li><a href="#messages">Messages</a></li>
              <li><a href="#contact">Contact</a></li>
            </ul>
          </div><!--/.nav-collapse -->
          <form class="navbar-search pull-left">
				<input type="text" class="search-query" name="search-geo" id="search-geo" placeholder="<s:text name="navigation.search.geo"/>"> 
				<input type="text" class="search-query" name="search-user" placeholder="<s:text name="navigation.search.user"/>" >
          </form>
        </div>
      </div>
    </div>
    
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
				<tiles:insertTemplate template="/jsp/blocks/block-nav-bootstrap.jsp"/>
			</div>
			<div id="mainContent" class="span10">
				<tiles:insertAttribute name="content"/>
			</div>
		</div>
	</div>
    <footer><tiles:insertTemplate template="/jsp/structure/footer.jsp"/></footer>
    <script language="JavaScript" type="text/javascript" src="/js/bootstrap.js"></script>
    <script language="JavaScript" type='text/javascript' src="/js/jquery-ui.min.js"></script>
    <script type='text/javascript' src="/jwplayer/jwplayer.js"></script>
    
</body>
</html>
