<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="append-bottom col-xs-offset-1 col-xs-23">

	<div role="tabpanel">

		<!-- Nav tabs -->
		<s:set value="1" var="activeTab"/>
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
						<th class="center">Email</th>
						<th>City</th>
						<th class="center">Last connection<i class="fa fa-fw fa-sort-desc"></i></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator value="users" var="user">
						<tr>
							<td><img src="<s:property value="#search.getResultMiniThumbUrl(#user)"/>"></td>
							<td><a href="<s:property value="#urlService.getOverviewUrl('mainContent',#user)"/>"><s:property value="#user.pseudo" /></a></td>
							<td class="center"><s:property value="#user.email" /></td>
							<td><a href="<s:property value="#search.getResultLocalizationUrl(#user)"/>"><s:property value="#search.getResultLocalizationName(#user)" /></a></td>
							<td><s:property value="#user.onlineTimeout" /></td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
		</div>
	</div>
</div>
