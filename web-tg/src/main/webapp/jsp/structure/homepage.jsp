<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" lang="<s:property value="headerSupport.language"/>">
<head>
	<tiles:insertAttribute name="meta-header"/>
	<meta name="apple-itunes-app" content="app-id=603515989, app-argument=<s:property value="headerSupport.canonical"/>">
	<link href='http://fonts.googleapis.com/css?family=Open+Sans:300,600,700' rel='stylesheet' type='text/css'>
	<!-- build:css(../web-static) /styles/pelmel.min.css -->	
    <link rel="stylesheet" href="/styles/bootstrap-3.3.1/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/styles/typeahead.min.css" type="text/css">
    <link href="/styles/jquery.Jcrop.min.css" rel="stylesheet" type="text/css" media="all"/>
    <link href="/styles/datepicker3.min.css" rel="stylesheet" type="text/css" media="all"/>
    <link href="/styles/slider.min.css" rel="stylesheet" type="text/css" media="all"/>
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
	<!-- Begin Cookie Consent plugin by Silktide - http://silktide.com/cookieconsent -->
	<script type="text/javascript">
	    window.cookieconsent_options = {"message":"<s:text name='cookie.bannerMessage'/>","dismiss":"<s:text name='cookie.bannerOk'/>","learnMore":"More info","link":null,"theme":"light-bottom"};
	</script>
	
	<script type="text/javascript" src="//s3.amazonaws.com/cc.silktide.com/cookieconsent.latest.min.js"></script>
	<!-- End Cookie Consent plugin -->
		
</head>
<body id="page-home" class="<tiles:insertAttribute name="background"/>">
<s:set value="headerSupport" var="headerSupport"/>
<tiles:insertAttribute name="header-bars"/>
<s:set value="headerSupport" var="headerSupport"/>
<s:set value="homepageUrl" var="homeUrl"/>
<div id="wrap-hp">
	<nav class="navbar navbar-default navbar-fixed-top <s:property value="headerSupport.getPageStyle()"/>" role="navigation">
	  <div class="container">
	    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
	      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#pelmel-collapse">
	        <span class="sr-only">Toggle navigation</span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	        <span class="icon-bar"></span>
	      </button>
	      <a class="navbar-brand hidden-md hidden-lg" href="<s:property value="#homeUrl"/>"><img class="logo-icon-xs" alt="PELMEL Guide" src="/images/V3/logoMob.png"/></a>
	      <div class="hidden-sm hidden-md hidden-lg col-xs-16 mini-hp-title">
	      	<a class="pelmel-slogan" href="#">PELMEL Guide</a>
	      	
	      	
	      </div>
	    </div>
	    
	    <!-- Collect the nav links, forms, and other content for toggling -->
	    <div class="collapse navbar-collapse" id="pelmel-collapse">

			<ul class="col-xs-24 col-sm-5 nav navbar-nav">
				<s:set value="'language.'+#headerSupport.getLanguage()" var="currentLang" />
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown"><span class="icon-globe"></span><span class="lang"><s:property value="getText(#currentLang)"/></span><b class="caret"></b></a>
					<ul class="dropdown-menu">
						<s:iterator value="#headerSupport.availableLanguages" var="lang">
							<s:set value="'language.'+#lang" var="msg" />
							<s:set value="#headerSupport.getAlternate(#lang)" var="alternateUrl"/>
							<li><a class="lang-link" href="<s:property value="#alternateUrl"/>" onclick="_gaq.push(['_link','<s:property value="#alternateUrl"/>']); return false;"><s:property value="getText(#msg)"/></a></li>
						</s:iterator>
					</ul>
				</li>
			</ul>
			<div class="hidden-xs hidden-sm col-md-13 nav-title nav navbar-nav">
				<a class="pelmel-slogan" href="#"><s:text name="homepage.sentence.4"/></a>
			</div>


			
			
			<s:if test="logged">
				<ul class="nav navbar-nav navbar-right">
					<s:set value="currentUserSupport.currentUser" var="currentUser"/>
					<li class="nav-login">
						<a href="/myProfile" rel="nofollow"><img class="thumb-logged-user" src="<s:property value="getMediaUrl(currentUserSupport.currentUserMedia.miniThumbUrl)"/>" alt="<s:property value="#currentUser.pseudo"/>"><span class="logged hidden-sm hidden-md"><s:property value="#currentUser.pseudo.length() < 20 ? #currentUser.pseudo : #currentUser.pseudo.substring(0,17)+'...'"/></span></a>
					</li>
					<li class="nav-login">
						<a class="profile-section-link" href="/myMessages" rel="nofollow"><img class="nav-profile-icon" src="/images/V3/navMessg.png" alt="<s:text name="nav.menu.myMessages"/>"><span class="hidden-sm hidden-md hidden-lg"><s:text name="nav.menu.myMessages"/></span></a>
					</li>
					<li class="nav-login">
						<a class="profile-section-link" href="/disconnect" rel="nofollow"><img class="nav-profile-icon" src="/images/V3/navLogout.png" alt="<s:text name="user.logout"/>"><span class="hidden-sm hidden-md hidden-lg"><s:text name="user.logout"/></span></a>
					</li>
				</ul>
					
					
			</s:if><s:else>
				<ul class="col-xs-24 col-sm-6 right nav navbar-nav navbar-right">
			        <li><a href="/ajaxGetRegisterForm" data-toggle="modal" data-target="#myModal" rel="nofollow">Sign up</a></li>
			        <li><img class="login-separator" src="/images/V3/separator.png" alt="separator"></li>
			        <li><a href="/ajaxGetLoginForm<s:property value="headerSupport.getElement() ? '?key=' + headerSupport.getElement().getKey() : ''"/>"  rel="nofollow" data-toggle="modal" data-target="#overlayLogin">Login</a></li>
		      	</ul>
	        </s:else>
	    </div><!-- /.navbar-collapse -->
	  </div><!-- /.container-fluid -->
	</nav>
<div class="container white">
	<div class="row">
		<div class="col-xs-24 col-md-offset-4 col-md-8 col-sm-12 screenshot-container">
			<a href="<s:property value="#homeUrl"/>"><img class="homepage-screenshot" alt="PELMEL Guide" src="/images/V3/iphone-webshot.png"/></a>
		</div>
		<div class="col-xs-24 col-sm-12 col-md-8">
			<div class="hp logo">
				<a href="<s:property value="#homeUrl"/>"><img class="logo-icon" alt="PELMEL Guide" src="/images/V3/web-logo-pelmel-4.png"/></a>
			</div>
			<div class="row hp-social-bar">
				<div class="col-xs-8 social-link text-right">
					<a href="https://www.facebook.com/PelmelGuide"><span class="facebook-r"></span></a>
				</div>
				<div class="col-xs-8 social-link center">
					<a href="https://twitter.com/PelMelGuide"><span class="twitter-r"></span></a>
				</div>
				<div class="col-xs-8 social-link">
					<a href="https://www.google.com/+Pelmelguide1"><span class="gplus-r"></span></a>
				</div>
			</div>
			<s:set id="obj" value="overviewSupport.overviewObject"/>
			<div class="margin-span5-first login hp-apps">
				<span class="col-xs-24 pelmel-hp-text headline-small center"><s:text name="pelmel.headline"/></span>
				<div class="col-xs-12">
					<a class="clearfix" href="https://itunes.apple.com/us/app/pelmel-guide/id603515989"><img class="apple-badge" src="/images/V3/appstore-<s:property value="language"/>.svg" alt='Apple AppStore'></a>
				</div>
				<div class="col-xs-12">
					<a class="clearfix" href="https://play.google.com/store/apps/details?id=com.nextep.pelmel">
					  <img class="android-badge" alt="Android app on Google Play" src="/images/V3/playstore-en.svg" />
					</a> 
				</div>
			</div>
		</div>
	</div>
	<div class="col-xs-12 center"><a class="pelmel-hp-text" href="/privacy">Privacy policy</a></div>
	<div class="col-xs-12 center"><a class="pelmel-hp-text" href="/about">Terms of use</a></div>
</div>
</div>
<div class="bottom-footer">
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-hidden="true">
	  <div class="modal-dialog">  
	      <div class="modal-content"></div>  
	  </div>  
	</div><!-- /.modal -->
	<div class="modal fade" id="overlayLogin" tabindex="-1" data-role="dialog" aria-hidden="true">
	  <div class="modal-dialog modal-sm">  
	      <div class="modal-content">
	      </div>  
	  </div>  
	</div>
	<div class="footer">
		<div class="container">
			<div class="row">
				<div class="col-md-5"><!--  --></div>
				<div class="col-md-19">
					<div class="row">
						<div class="col-md-17">
							<div class="row">
								<div class="col-sm-12 footer-right-sep">
									<span class="footer-title col-xs-24"><s:text name="block.popular.cities.title"/></span>
									<s:set value="popularSupport" var="popularSupport"/>
									<s:iterator value="#popularSupport.getPopularElements()" var="elt">
										<a class="popular-footer footer-text" href="<s:property value="#popularSupport.getUrl(#elt)"/>"><s:property value="#popularSupport.getName(#elt)"/></a>
									</s:iterator>
								</div>
								<div class="col-sm-offset-1 col-sm-11">
									<s:set value="secondaryPopularSupport" var="popularSupport"/>
									<s:if test="#popularSupport != null">
										<span class="footer-title col-xs-24"><s:property value="#popularSupport.getTitle()"/></span>
										<s:iterator value="#popularSupport.getPopularElements()" var="elt">
											<a class="popular-footer footer-text" href="<s:property value="#popularSupport.getUrl(#elt)"/>"><s:property value="#popularSupport.getName(#elt)"/></a>
										</s:iterator>
									</s:if>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<!-- build:js(../web-static) /js/pelmel-hp.min.js -->
<script src="/js/jquery-migrate-1.2.1.min.js"></script>
<script type='text/javascript' src="/js/jquery.form.min.js"></script>
<script type='text/javascript' src="/js/jquery.hoverIntent.min.js"></script>
<script src="/styles/bootstrap-3.3.1/js/bootstrap.min.js"></script>
<script src="/js/typeahead.min.js"></script>
<script src="/js/hogan-2.0.0.min.js"></script>
<script src="/js/togayther.js"></script>
<script src="/js/togayther-media.js"></script>
<script src="/js/togayther-search.js"></script>
<!-- endbuild -->
<tiles:insertAttribute name="footer"/>
</body>
</html>
