<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="append-bottom col-xs-offset-1 col-xs-23">
	<div role="tabpanel">

		<!-- Nav tabs -->
		<s:set value="0" var="activeTab"/>
		<tiles:insertTemplate template="/jsp/top/top-admin-tabs.jsp"></tiles:insertTemplate>
		<div class="tab-content">
			<form action="<s:property value="url"/>" method="get" class="form-inline append-bottom prepend-top">
			
			
				<div class="form-group">
					<label for="filterOnline">Status</label>
					<select class="form-control" id="filterOnline" name="filterOnline">
						<option value="true" <s:property value="'true'.equals(filterOnline) ? 'selected' : ''"/>>Online</option>
						<option value="false" <s:property value="'false'.equals(filterOnline) ? 'selected' : ''"/>>Deleted</option>
						<option value="" <s:property value="filterOnline==null || filterOnline.isEmpty() ? 'selected' : ''"/>>All</option>
					</select>
				</div>
				<div class="form-group">
					<label for="filterIndexed">Visibility</label>
					<select class="form-control" id="filterIndexed" name="filterIndexed">
						<option value="true" <s:property value="'true'.equals(filterIndexed) ? 'selected' : ''"/>>SEO visible only</option>
						<option value="false" <s:property value="'false'.equals(filterIndexed) ? 'selected' : ''"/>>Non SEO only </option>
						<option value="" <s:property value="filterIndexed==null || filterIndexed.isEmpty() ? 'selected' : ''"/>>All</option>
					</select>
				</div>
				<div class="form-group">
					<label for="sortField">Sort by</label>
					<select class="form-control" id="sortField" name="sortField">
						<option value="UDATE" <s:property value="'UDATE'.equals(sortField) ? 'selected' : ''"/>>Update Date</option>
						<option value="CLOSED_REPORT_COUNT" <s:property value="'CLOSED_REPORT_COUNT'.equals(sortField) ? 'selected' : ''"/>>Abuse Reports</option>
					</select>
					<select class="form-control" id="sortDirection" name="sortDirection">
						<option value="ASC" <s:property value="'ASC'.equals(sortDirection) ? 'selected' : ''"/>>ASC</option>
						<option value="DESC" <s:property value="'DESC'.equals(sortDirection) ? 'selected' : ''"/>>DESC</option>
					</select>
				</div>
				<button class="btn btn-primary">Refresh</button>
			</form>
			<s:set value="searchSupport" var="search"/>
			<s:set value="urlService" var="urlService"/>
			<s:set value="paginationSupport" var="paginationSupport"/>
			<tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"></tiles:insertTemplate>
			<table class="table table-bordered table-hover">
				<thead>
					<tr><th>Thumb</th><th>Name</th><th class="center">Type</th><th class="center">Online</th><th class="center">SEO</th><th>Location</th>
					<th>Reports <s:if test="'CLOSED_REPORT_COUNT'.equals(sortField)"><i class="fa fa-fw <s:property value="'ASC'.equals(sortDirection) ? 'fa-sort-asc' : 'fa-sort-desc'"/>"></i></s:if></th>
					<th>Last Updated <s:if test="'UDATE'.equals(sortField)"><i class="fa fa-fw <s:property value="'ASC'.equals(sortDirection) ? 'fa-sort-asc' : 'fa-sort-desc'"/>"></i></s:if></th>
					<th class="center">Edit</th><th class="center">Del</th></tr>
				</thead>
				<tbody>
					<s:iterator value="places" var="place">
						<tr><td>
							<img src="<s:property value="#search.getResultMiniThumbUrl(#place)"/>">
						</td>
						<td><a href="<s:property value="#urlService.getOverviewUrl('mainContent',#place)"/>"><s:property value="#place.name"/></a></td>
						<td class="center"><s:property value="#place.getPlaceType()"/></td>
						<td class="center"><s:if test="#place.online"><img class="admin-icon" src="/images/V3/check.png"></s:if><s:else><img class="admin-icon" src="/images/V3/delete.png"></s:else></td>
						<td class="center"><s:if test="#place.indexed"><img class="admin-icon" src="/images/V3/check.png"></s:if><s:else><img class="admin-icon" src="/images/V3/delete.png"></s:else></td>
						<td><a href="<s:property value="getSearchUrl(#place)"/>"><s:property value="#search.getResultLocalizationName(#place)"/></a></td>
						<td><s:property value="#place.getClosedCount()"/></td>
						<td><s:property value="#place.lastUpdateTime"/></td>
						<td class="bar">
							<a data-toggle="modal" data-target="#myModal" href="<s:property value="urlService.getPlaceEditionFormUrl('mainContent',
							#place.getKey(), #place.getPlaceType())"/>" rel="nofollow">
								<span id="edit" class="tool tool-edit" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.edit"><s:param value="#place.getPlaceType()"/></s:text>"><!--  --></span>
							</a>
						</td>
							<td class="bar">
								<a data-toggle="modal" data-target="#myModal" href="/deleteItem?key=<s:property value="#place.getKey()"/>" rel="nofollow"><span id="addevent" class="tool tool-delete" data-toggle="tooltip" data-placement="right" title="<s:text name="tooltip.delete"/>"><!--  --></span></a>
							</td>
						</tr>
						
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
	
	
	
</div>
