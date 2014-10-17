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
    <link href="/styles/jquery-ui-1.8.16.css" rel="stylesheet" type="text/css" media="all"/>
<!--    <link href="/struts/niftycorners/niftyCorners.css" rel="stylesheet" type="text/css"/>-->
<!--    <link href="/struts/niftycorners/niftyPrint.css" rel="stylesheet" type="text/css" media="print"/>-->
<!--    <script language="JavaScript" type="text/javascript" src="/struts/niftycorners/nifty.js"></script>-->
    <script language="JavaScript" type="text/javascript" src="/js/jquery-1.6.2.min.js"></script>
    <script language="JavaScript" type='text/javascript' src="/js/jquery.form.js"></script>
    <script language="JavaScript" type='text/javascript' src="/js/jquery-ui-1.8.16.min.js"></script>
    <script type='text/javascript' src="/jwplayer/jwplayer.js"></script>
    <script type="text/javascript">

    	function call( callUrl, elementId ) {
			jQuery.get(callUrl,null,function(data) { document.getElementById(elementId).innerHTML = data; bindForms(); bindSliders(); } );
		}
    	function simpleCall( callUrl, elementId ) {
			jQuery.get(callUrl,null,function(data) { document.getElementById(elementId).innerHTML = data;  } );
		}
    	function displayMsg() {
    		var current = document.getElementById('instantMsg').style.display;
    		if(current == 'inline') {
    			document.getElementById('instantMsg').style.display='none';
    		} else {
    			document.getElementById('instantMsg').style.display='inline';
    		}
    	}
    	function bindForms() {
    		var options = {
    				target: '#instantMsg',
    				url: 'ajaxSendMsg.action'
    		};
    		jQuery('#sendMsg').ajaxForm(options);
			options = {
					target: '#instantUserMsg',
					url: 'ajaxSendInstantMsg.action'
			};
			jQuery('#instantMsgForm').ajaxForm(options);
    	}
    	function computeHeightString( value ) {
    		var feetVal = Math.floor(value*0.0328084);
    		var inchVal = Math.round(value*0.3937008-feetVal*12);
    		var valStr = feetVal + " ' " + inchVal + "''";
    		return valStr;
    	}
    	function computeWeightString( value ) {
    		var lbVal = floor(value*2.204623);
    		var valStr = lbVal + " lbs";
    		return valStr;
    	}
    	function convert(id, value) {
    		if("weight" == id) {
    			return computeWeightString(value);
    		} else if( "height" == id) {
    			return computeHeightString(value);
    		}
    		return "";
    	} 
    	function updateSlider( id ) {
    		var min = $('#' + id + '-slider').slider("values",0);
    		var max = $('#' + id + '-slider').slider("values",1);
    		document.getElementById(id + "-min-label").innerHTML = min;
    		document.getElementById(id + "-max-label").innerHTML = max;
    		
    	}
    	function createSlider( id ) {
    		var min = document.getElementById(id + "Min").value;
			var max = document.getElementById(id + "Max").value;
			var currMin = document.getElementById(id + "CurrentMin").value;
			var currMax = document.getElementById(id + "CurrentMax").value;
			$("#" + id + "-slider").slider({
				range: true,
				min: parseInt(min),
				max: parseInt(max),
				values: [parseInt(currMin), parseInt(currMax)],
				slide: function(event, ui) {
					updateSlider( id );
				},
				stop: function(event, ui) {
					var urlPattern = document.getElementById(id + "Url").value;
					var url = urlPattern.replace("_min_",document.getElementById(id + "-min-label").innerHTML);
					url = url.replace("_max_",document.getElementById(id + "-max-label").innerHTML);
					call(url,"mainContent");
				}
			});
    	}

    	function bindSliders() {
			createSlider("age");
			createSlider("weight");
			createSlider("height");
			updateSlider("age");
			updateSlider("weight");
			updateSlider("height");
    	}
    	jQuery(document).ready(bindForms());
    	$(function() {
    		bindSliders();
    	});
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
