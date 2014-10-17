<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<ul id="menu-category" class="dropdown">
	<s:set value="geoPlaceTypesSupport" var="placeTypeSupport"/>
	<s:if test="#placeTypeSupport==null || #placeTypeSupport.availablePlaceTypes == null || #placeTypeSupport.availablePlaceTypes.isEmpty()">
		<li class="category-option bar"><a href="<s:property value="#localization.getSearchUrlForType('BARS')"/>"><s:text name="facet.label.bar"/></a></li>
		<li class="category-option sexshop"><a href="<s:property value="#localization.getSearchUrlForType('SHOPS')"/>"><s:text name="facet.label.sexshop"/></a></li>
		<li class="category-option asso"><a href="<s:property value="#localization.getSearchUrlForType('ASSOCIATIONS')"/>"><s:text name="facet.label.asso"/></a></li>
		<li class="category-option restaurant"><a href="<s:property value="#localization.getSearchUrlForType('RESTAURANTS')"/>"><s:text name="facet.label.restaurant"/></a></li>
		<li class="category-option sauna"><a href="<s:property value="#localization.getSearchUrlForType('SAUNAS')"/>"><s:text name="facet.label.sauna"/></a></li>
		<li class="category-option club"><a href="<s:property value="#localization.getSearchUrlForType('CLUBS')"/>"><s:text name="facet.label.club"/></a></li>
		<li class="category-option sexclub"><a href="<s:property value="#localization.getSearchUrlForType('SEXCLUBS')"/>"><s:text name="facet.label.sexclub"/></a></li>
	</s:if><s:else>
		<s:iterator value="#placeTypeSupport.availablePlaceTypes" var="placeType">
			<li class="category-option <s:property value="#placeType"/>">
				<a href="<s:property value="#placeTypeSupport.getPlaceTypeUrl(#placeType)"/>">
					<s:property value="#placeTypeSupport.getPlaceTypeLabel(#placeType)"/>
					<span class="li-count"><s:property value="#placeTypeSupport.getPlaceTypeCount(#placeType)"/></span>
				</a>
			</li>
		</s:iterator>
	</s:else>
</ul>
