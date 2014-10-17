<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<s:set value="currentUserSupport.getCurrentUser()" var="user"/>

<s:if test="overviewSupport.isCurrentOwner(#user)">
	<div class="main-info-container last append-bottom">
		<div class="mosaic-text <s:property value="getHeaderSupport().getPageStyle()"/>-text">
			<s:text name="owner.place.mine"/>
		</div>
		<div class="container-light-padding">
			<s:property value="overviewSupport.getOwnershipInfoLabel(#user)"/>
		</div>
		<s:set value="overviewSupport.getOverviewObject()" var="obj"/>
		<span class="center prepend-top append-bottom"><a rel="#overlay" class="button <s:property value="getHeaderSupport().getPageStyle()"/>" href="<s:property value="urlService.getSponsorshipUrl(#obj)"/>"><s:text name="owner.place.mine.more"/></a></span>
	</div>		
</s:if><s:else>
	<s:if test="overviewSupport.isUpdatable()">
		<div class="main-info-container last append-bottom">
			<div class="mosaic-text <s:property value="getHeaderSupport().getPageStyle()"/>-text">
				<s:text name="owner.place.question">
					<s:param value="headerSupport.getPageTypeLabel()"/>
				</s:text>
			</div>
			<s:if test="#user==null || #user.getCredits()<=0"> 
				<div class="container-light-padding"><s:text name="owner.place.promote"><s:param value="overviewSupport.getTitle(overviewSupport.getOverviewObject())"/></s:text></div>
				<div class="center"><a class="container-light-padding sponsor-link <s:property value="getHeaderSupport().getPageStyle()"/>-text" href="/promote"><s:text name="owner.place.link"/></a></div>
			</s:if><s:else>
				<div class="container-light-padding"><s:text name="owner.place.promote.ok"><s:param value="overviewSupport.getTitle(overviewSupport.getOverviewObject())"/></s:text></div>
				<s:set value="overviewSupport.getOverviewObject()" var="obj"/>
				<div class="center prepend-top append-bottom"><a rel="#overlay" class="button <s:property value="getHeaderSupport().getPageStyle()"/>" href="<s:property value="urlService.getSponsorshipUrl(#obj)"/>"><s:text name="owner.place.link.promote"/></a></div>
			</s:else>
		</div>
	</s:if>
</s:else>
