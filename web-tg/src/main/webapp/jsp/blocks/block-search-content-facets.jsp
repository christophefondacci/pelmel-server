<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"  trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<s:set value="searchSupport" var="searchSupport"/>
<div class="col-sm-12 col-md-7 col-lg-7 last">
	<h3 class="section-title facet-title <s:property value="headerSupport.getPageStyle()"/>"><s:property value="#searchSupport.getFacetCategoryTitle(null)"/></h3>
	<tiles:insertTemplate template="/jsp/blocks/block-facet-range.jsp"/>
	<s:set value="false" var="hasFacets"/>
	<s:iterator value="#searchSupport.facetCategories" var="categ">
		<s:set value="#searchSupport.getFacets(#categ)" var="facets"/>
		<s:if test="'tags'.equals(#categ.getCategoryCode())">
			<s:if test="!#facets.isEmpty()">
				<div class="facet-container">
					<s:iterator value="#facets" id="facet">
						<s:set value="true" var="hasFacets"/>
						<s:set value="#searchSupport.getFacetTranslation(#facet.facet.facetCode)" var="facetLabel"/>
						<div class="search-facet <s:property value="#searchSupport.isSelected(#facet.facet) ? 'search-facet-selected':''"/>">
							<s:if test="#searchSupport.isSelected(#facet.facet)">
								<a href="<s:property value="#searchSupport.getFacetRemovedUrl(#facet.facet)"/>">
									<img class="search-facet-image" alt="<s:property value="#facetLabel"/>" src="<s:property value="#searchSupport.getFacetIconUrl(#facet.facet.facetCode)"/>">
								</a>
							</s:if><s:else>
								<a href="<s:property value="#searchSupport.getFacetAddedUrl(#facet.facet)"/>">
									<img class="search-facet-image" alt="<s:property value="#facetLabel"/>" src="<s:property value="#searchSupport.getFacetIconUrl(#facet.facet.facetCode)"/>">
								</a>
							</s:else>
							<div class="search-facet-text"><s:property value="#searchSupport.getFacetTranslation(#facet.facet.facetCode)"/></div>
						</div>
					</s:iterator>
				</div>
			</s:if>
		</s:if>
		<s:if test="'amenities'.equals(#categ.getCategoryCode())">
		<s:if test="!#facets.isEmpty()">
			<div class="options-container">
				<s:iterator value="#facets" id="facet">
					<s:set value="true" var="hasFacets"/>
					<div class="search-option <s:property value="#searchSupport.isSelected(#facet.facet) ? 'search-option-selected':''"/>">
					<s:if test="#searchSupport.isSelected(#facet.facet)">
						<a href="<s:property value="#searchSupport.getFacetRemovedUrl(#facet.facet)"/>">
							<span class="option option-checked"><!-- --></span>
						</a>
						<a href="<s:property value="#searchSupport.getFacetRemovedUrl(#facet.facet)"/>">
							<s:property value="#searchSupport.getFacetTranslation(#facet.facet.facetCode)"/>
						</a>
					</s:if><s:else>
						<a href="<s:property value="#searchSupport.getFacetAddedUrl(#facet.facet)"/>">
							<span class="option option-unchecked"><!-- --></span>
						</a>
						<a href="<s:property value="#searchSupport.getFacetAddedUrl(#facet.facet)"/>">
							<s:property value="#searchSupport.getFacetTranslation(#facet.facet.facetCode)"/>
						</a>
					</s:else>
					</div>
				</s:iterator>
			</div>
		</s:if>
		</s:if>
	</s:iterator>
	<s:if test="!#hasFacets">
	<div class="options-container"><s:text name="search.facet.empty"/></div>
	</s:if>
</div>
<div class="col-sm-12 col-md-7 col-lg-7 last">
	<%-- Sponsored elements support --%>
	<s:set value="sponsoredPopularSupport" var="sponsoredSupport"/>
	<s:if test="#sponsoredSupport!= null && !#sponsoredSupport.getPopularElements().isEmpty()">
		<h3 id="fav-section" class="section-title facet-title <s:property value="headerSupport.getPageStyle()"/>"><s:text name="sponsored.search.title"/></h3>
		<div class="sponsor-container">
			<div class="row">
				<s:iterator value="#sponsoredSupport.getPopularElements()" var="item">
					<s:set value="#sponsoredSupport.getIconUrl(#item)" var="imageUrl"/>
					<s:set value="#sponsoredSupport.getUrl(#item)" var="url"/>
					<s:set value="#sponsoredSupport.getName(#item)" var="name"/>
					<div class="col-xs-8 search-result-container">
						<div class="search-result">
							<a href="<s:property value="#url"/>"><img alt="<s:property value="#name"/>" class="img-responsive search-thumb" src="<s:property value="#imageUrl"/>"/></a>
							<div class="search-thumb-text"><a href="<s:property value="#url"/>"><s:property value="#name"/></a></div>
						</div>
					</div>			
				</s:iterator>
			</div>
		</div>
	</s:if>
</div>
