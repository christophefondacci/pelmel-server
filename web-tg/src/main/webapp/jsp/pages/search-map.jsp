<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<div id="mainCol" class="mainCol">
<tiles:insertTemplate template="/jsp/blocks/block-breadcrumb.jsp"/>
<tiles:insertTemplate template="/jsp/blocks/block-tab-menu.jsp"/>
<div class="tab-content" id="map">
</div></div>
<div id="rightCol" class="ptm">
<tiles:insertAttribute name="right-col"/>
</div>