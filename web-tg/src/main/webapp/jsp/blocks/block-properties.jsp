<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:set var="propertySupport" value="propertiesSupport"/>
<s:if test="#propertySupport!=null && !#propertySupport.properties.empty">
	<table id="prop-table" class="no-zebra">
		<s:iterator value="#propertySupport.properties" var="prop">
			<tr><td class="prop-tab-label font-bold <s:property value="headerSupport.getPageStyle()"/>-text"><s:property value="#propertySupport.getPropertyLabel(#prop)"/></td>
			<td class="prop-tab-value  <s:property value="headerSupport.getPageStyle()"/>-text">
				<s:set value="#propertySupport.getPropertyValue(#prop)" var="val"/>
				<s:if test="#val!=null && #val.startsWith('http://www.booking.com')">
					<a href="<s:property value="#val"/>"><s:text name="property.label.booking"></s:text></a>
				</s:if><s:elseif test="#val!=null && #val.startsWith('http://')">
					<a href="<s:property value="#val"/>" rel="nofollow"><s:property value="#val"/></a>
				</s:elseif><s:else>
					<s:property value="#propertySupport.getPropertyValue(#prop)"/>
				</s:else>
			</td></tr>
		</s:iterator>
	</table>
</s:if>