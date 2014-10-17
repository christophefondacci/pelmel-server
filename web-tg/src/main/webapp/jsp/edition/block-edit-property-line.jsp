<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<div class="row form-group">
	<div class="col-xs-24">
		<select name="propertyCode" class="col-sm-12">
			<option value="0">[Property]</option>
			<s:iterator value="#propertiesEditionSupport.availablePropertyCodes" var="propertyCode">
				<option value="<s:property value="#propertyCode"/>" <s:property value='#prop.code.equals(#propertyCode) ? "selected" : ""'/>><s:property value="#propertiesEditionSupport.getPropertyLabel(#propertyCode)"/></option>
			</s:iterator>
		</select>
	</div>
	<div class="col-xs-24">
		<input type="hidden" name="propertyKey" value="<s:property value="#prop == null ? 0 : #prop.key"/>"/>
		<input class="form-control" type="text" name="propertyValue" value="<s:property value="#prop.value"/>"/>
	</div>
</div>