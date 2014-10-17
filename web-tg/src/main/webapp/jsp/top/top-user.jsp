<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="headerSupport" var="headerSupport"/>
<s:set value="mediaProvider" var="mediaProvider"/>
<s:set value="searchSupport" var="searchSupport"/>
<div class="col-sm-19 last">
	<div class="col-sm-19 overview-image <s:property value="#headerSupport.getPageStyle()"/>-filler last">
		<div id="image-container">
			<s:iterator value="#searchSupport.getSearchResults()" var="result">
				<s:set var="media" value="#mediaProvider.getMainMedia(#result)"/>
				<s:if test="#media!=null">
					<img src="<s:property value="#mediaProvider.getMediaUrl(#media)"/>" style="<s:property value="#mediaProvider.getOverviewFitStyle(#media)"/>" alt="<s:property value="#searchSupport.getResultTitle(#result)"/>">
					<a href="<s:property value="#searchSupport.getResultUrl(#result)"/>"></a>
				</s:if>
			</s:iterator>
		</div>
	</div>
</div>
