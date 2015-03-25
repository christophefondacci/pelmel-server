<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="append-bottom col-xs-offset-1 col-xs-23">

	<div role="tabpanel">

		<!-- Nav tabs -->
		<s:set value="2" var="activeTab"/>
		<tiles:insertTemplate template="/jsp/top/top-admin-tabs.jsp"></tiles:insertTemplate>
		<div class="tab-content">
			<s:set value="searchSupport" var="search" />
			<s:set value="urlService" var="urlService" />
			<s:set value="paginationSupport" var="paginationSupport" />
			<tiles:insertTemplate template="/jsp/blocks/block-menu-pages.jsp"></tiles:insertTemplate>
			<table class="table table-bordered table-hover">
				<thead>
					<tr>
						<th>Thumb</th>
						<th>Name</th>
						<th class="center">Start</th>
						<th>Place</th>
						<th>Online</th>
						<th class="center">Updated<i class="fa fa-fw fa-sort-desc"></i></th>
						<th class="center">Author</th>
						<th class="center">Edit</th><th class="center">Del</th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="events" var="event">
						<tr>
							<td><img src="<s:property value="#search.getResultMiniThumbUrl(#event)"/>"></td>
							<td><a href="<s:property value="#urlService.getOverviewUrl('mainContent',#event)"/>"><s:property value="#event.name" /></a></td>
							<td class="center"><s:property value="getLocalizedDate(#event,#event.startDate)" /><br><s:property value="getLocalizedDate(#event,#event.endDate)" /></td>
							<td><a href="<s:property value="#search.getResultLocalizationUrl(#event)"/>"><s:property value="#search.getResultLocalizationName(#event)" /></a></td>
							<td class="center"><s:if test="#event.online"><img class="admin-icon" src="/images/V3/check.png"></s:if><s:else><img class="admin-icon" src="/images/V3/delete.png"></s:else></td>
							<td><s:property value="#event.lastUpdateTime" /></td>
							<td><a href="<s:property value="getAuthorUrl(#event)"/>"><s:property value="getAuthor(#event)" /></a></td>
							<td class="bar"><a data-toggle="modal"
								data-target="#myModal"
								href="<s:property value="urlService.getEventEditionFormUrl('mainContent', #event.getKey())"/>"
								rel="nofollow"> <span id="edit" class="tool tool-edit"
									data-toggle="tooltip" data-placement="right"
									title="<s:text name="tooltip.edit"><s:param value="#event.name"/></s:text>">
										<!--  -->
								</span>
							</a></td>
							<td class="bar"><a data-toggle="modal"
								data-target="#myModal"
								href="/deleteItem?key=<s:property value="#event.getKey()"/>"
								rel="nofollow"><span id="addevent" class="tool tool-delete"
									data-toggle="tooltip" data-placement="right"
									title="<s:text name="tooltip.delete"/>">
										<!--  -->
								</span></a></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
</div>
