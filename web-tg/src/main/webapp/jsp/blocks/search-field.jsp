<%@taglib prefix="s" uri="/struts-tags" %>
<form action="<s:property value="#homeUrl"/>" class="navbar-form navbar-left full-width" role="search">
	<div class="input-group full-width">
		<input type="text" class="form-control <s:property value="isHomePage() ? 'pelmel-search':''"/>" name="searchTerm" placeholder="<s:text name="navigation.search.geo"/>" value="<s:property value="searchTerm"/>">
		<div class="input-group-btn">
		<button type="submit" class="btn btn-default"><span class="glyphicon glyphicon-search"></span></button>
		</div>
	</div>
</form>

