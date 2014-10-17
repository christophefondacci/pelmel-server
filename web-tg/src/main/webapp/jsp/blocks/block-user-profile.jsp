<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="currentUserSupport.currentUser" var="currentUser"/>
<input type="hidden" name="userKey" value="<s:property value="#currentUser.key"/>"/>
<input type="hidden" id="heightMin" value="120"/><input type="hidden" id="heightMax" value="220"/><s:if test="#currentUser==null"><input type="hidden" id="heightCurrentValue" value="172"></s:if><s:else><input type="hidden" id="heightCurrentValue" value="<s:property value="#currentUser.heightInCm"/>"></s:else>
<input type="hidden" id="weightMin" value="40"/><input type="hidden" id="weightMax" value="200"/><s:if test="#currentUser==null"><input type="hidden" id="weightCurrentValue" value="70"></s:if><s:else><input type="hidden" id="weightCurrentValue" value="<s:property value="#currentUser.weightInKg"/>"></s:else>
<s:if test="#currentUser == null">
	<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.login.email"/></label></span><input class="col-sm-10 last" type="text" name="email" value="<s:property value="#currentUser.email"/>" placeholder="<s:text name="index.register.emailRevealed"/>">
</s:if><s:else>
	<input type="hidden" name="email" value="<s:property value="#currentUser.email"/>">
</s:else>
<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.username"/></label></span><input class="col-sm-6 append-4 last" type="text" name="name" value="<s:property value="#currentUser.pseudo"/>">
<s:if test="#currentUser == null">
	<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.login.password"/></label></span><input class="col-sm-6 append-4 last" type="password" name="password">
	<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.passwordConfirm"/></label></span><input class="col-sm-6 append-4 last" type="password" name="passwordConfirm">
	<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.profilePhoto"/></label></span><input class="col-sm-10 last append-bottom" type="file" name="media">
</s:if>
<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.city"/></label></span><input class="col-sm-10 last" id="city" name="cityLabel" value="<s:property value="cityValue"/>"/><input type="hidden" name="cityKey" id="cityValue" value="<s:property value="currentUserSupport.currentUserCity.key"/>"/></td></tr>
<span id="cityDefinition" class="col-sm-offset-4 col-sm-10 append-bottom"><s:property value="cityDefinition"/></span>
<span class="col-sm-4"><label class="fieldLabel"><s:text name="place.form.description"/></label></span>
<input type="text" name="intro" class="col-sm-10 last" value="<s:property value="#currentUser.statusMessage"/>" placeholder="<s:text name="index.register.profileIntroLabel"/>">
<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.birthDate"/></label></span>
<div class="mutli-field">
	<select class="col-sm-3" name="birthDD">
		<option value="-1"><s:text name="index.register.birthDD"/></option>
		<s:iterator begin="1" end="31" step="1" var="day">
			<option <s:property value='getBirthDD(#currentUser)==#day ? "selected" : ""'/>><s:property value="#day"/>
		</s:iterator>
	</select>
	<select class="col-sm-4" name="birthMM">
		<option value="-1"><s:text name="index.register.birthMM"/></option>
		<s:iterator begin="1" end="12" step="1" var="month">
			<s:set var="key" value='"index.register.month."+#month'/>
			<option value="<s:property value="#month"/>" <s:property value='getBirthMM(#currentUser)==#month ? "selected" : ""'/>><s:property value="getText(#key)"/></option>
		</s:iterator>
	</select>
	<select name="birthYYYY" class="col-sm-3 last">
		<option value="-1"><s:text name="index.register.birthYY"/></option>
		<s:iterator begin="1910" end="2012" step="1" var="year">
			<option <s:property value='getBirthYYYY(#currentUser)==#year ? "selected" : ""'/>><s:property value="#year"/></option>
		</s:iterator>
	</select>
</div>
<!--	<tr><td class="right"><label class="fieldLabel"><s:text name="index.register.unitSystem"/></label></td><td><select name="intro" class="profile-intro"><s:iterator value="listUnitSystems()" var="unitSystem"><option value="#unitSystem.code"><s:property value="#unitSystem.name"/></option></s:iterator></select></td></tr>-->
<input id="inputWeight" type="hidden" name="weight">
<input id="inputHeight" type="hidden" name="height">
<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.height"/></label></span><div class="col-sm-6"><div id="height-slider"></div></div>
	<span id="heightValue" class="col-sm-4 last"><s:property value="#currentUser.heightInCm"/></span>
<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.weight"/></label></span><div class="col-sm-6"><div id="weight-slider"></div></div>
	<span id="weightValue" class="col-sm-4 last"><s:property value="#currentUser.weightInKg"/></span>
<span class="col-sm-4"><label class="fieldLabel"><s:text name="index.register.tags"/></label></span>
<s:set value="0" var="tagCount"/>
<s:iterator value="tagSupport.availableTags" id="tag">
	<s:if test="isChecked(#tag)">
		<input type="checkbox" class="hidden" id="<s:property value="#tag.key"/>" name="tags" value="<s:property value="#tag.key"/>" checked>
	</s:if><s:else><input type="checkbox" class="hidden" id="<s:property value="#tag.key"/>" name="tags" value="<s:property value="#tag.key"/>">
	</s:else>
	<div class="col-sm-3"><div class="space-right space-top"><a id="a-<s:property value="#tag.key"/>" class="<s:property value="getCSSClassSelected(#tag)"/>tag tagwidth" href="javascript:check('<s:property value="#tag.key"/>')"><img class="icon" src="<s:property value="tagSupport.getTagIconUrl(#tag)"/>"><s:property value="tagSupport.getTagTranslation(#tag)"/></a></div></div>
	<s:set var="tagCount" value="#tagCount+1"/>
	<s:if test="(#tagCount % 3) == 0">
		<span class="col-sm-1 last"></span>
		<span class="col-sm-4">&nbsp;</span>
	</s:if>
</s:iterator>
<span class="col-sm-<s:property value="(#tagCount % 3) > 0 ? (3*(3-(#tagCount % 3))+1) : 1"/> last append-bottom"/>&nbsp;</span>

	