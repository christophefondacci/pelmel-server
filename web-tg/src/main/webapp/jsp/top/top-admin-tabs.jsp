<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!-- Nav tabs -->
<ul class="nav nav-tabs" role="tablist">
	<li role="presentation" class="<s:property value="#activeTab == 0 ? 'active' : ''"/>"><a href="/admin/places"
		aria-controls="home" role="tab" data-toggle="tab">Places</a></li>
	<li role="presentation" class="<s:property value="#activeTab == 1 ? 'active' : ''"/>"><a href="/admin/users"
		aria-controls="home" role="tab" data-toggle="tab">Users</a></li>
	<li role="presentation" class="<s:property value="#activeTab == 2 ? 'active' : ''"/>"><a href="/admin/events"
		aria-controls="home" role="tab" data-toggle="tab">Events</a></li>
	<li role="presentation" class="<s:property value="#activeTab == 3 ? 'active' : ''"/>"><a href="/admin/eventseries"
		aria-controls="home" role="tab" data-toggle="tab">Hours</a></li>
</ul>