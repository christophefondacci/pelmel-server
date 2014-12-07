<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<s:set value="headerSupport" var="headerSupport"/>
<s:set value="currentUserSupport.currentUser" var="currentUser"/>
<div class="col-xs-24">
<form action="/userRegister.action"  enctype="multipart/form-data" method="post">
<input type="hidden" name="userKey" value="<s:property value="#currentUser.key"/>"/>
<div class="prepend-top append-bottom col-xs-24 last">
<h1 class="<s:property value="headerSupport.getPageStyle()"/> section-title"><s:text name="user.profile.h1"/></h1>
</div>
<div class="row">
<div class="col-xs-24 prepend-top">
	<div class="row">
	<div class="col-sm-offset-1 col-sm-8">
		<h2 class="section-title "><s:text name="user.profile.me"/></h2>
		<div class="row edit-form">
			<div class="form-group">
				<label class="prepend-top col-xs-24"><s:text name="index.register.username"/></label>
				<input class="form-control" type="text" name="name" value="<s:property value="#currentUser.pseudo"/>">
			</div>
			<div class="form-group">
				<label class="col-xs-24"><s:text name="index.register.city"/></label>
				<input class="form-control" type="text" id="city" name="cityLabel" value="<s:property value="cityValue"/>">
			</div>
			<input type="hidden" name="cityKey" id="cityValue" value="<s:property value="currentUserSupport.currentUserCity.key"/>"/>
			<div class="form-group">
				<label class="col-xs-24"><s:text name="index.register.birthDate"/></label>
				<div class="row">
					<div class="col-xs-24">
						<select class="col-xs-8" name="birthDD">
							<option value="-1"><s:text name="index.register.birthDD"/></option>
							<s:iterator begin="1" end="31" step="1" var="day">
								<option <s:property value='getBirthDD(#currentUser)==#day ? "selected" : ""'/>><s:property value="#day"/>
							</s:iterator>
						</select>
						<select class="col-xs-8" name="birthMM">
							<option value="-1"><s:text name="index.register.birthMM"/></option>
							<s:iterator begin="1" end="12" step="1" var="month">
								<s:set var="key" value='"index.register.month."+#month'/>
								<option value="<s:property value="#month"/>" <s:property value='getBirthMM(#currentUser)==#month ? "selected" : ""'/>><s:property value="getText(#key)"/></option>
							</s:iterator>
						</select>
						<select name="birthYYYY" class="col-xs-8">
							<option value="-1"><s:text name="index.register.birthYY"/></option>
							<s:iterator begin="1910" end="2012" step="1" var="year">
								<option <s:property value='getBirthYYYY(#currentUser)==#year ? "selected" : ""'/>><s:property value="#year"/></option>
							</s:iterator>
						</select>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="col-sm-6">
		<h2 class="section-title "><s:text name="user.profile.info"/></h2>
		<div class="prepend-top gray-container">
			<div class="container-padding">
				<div class="row">
					<label class="prepend-top col-xs-24"><s:text name="index.register.weight"/></label>
					<input type="hidden" id="weightMin" value="40"/><input type="hidden" id="weightMax" value="200"/><s:if test="#currentUser==null"><input type="hidden" id="weightCurrentValue" value="70"></s:if><s:else><input type="hidden" id="weightCurrentValue" value="<s:property value="#currentUser.weightInKg"/>"></s:else>
					<input id="inputWeight" type="hidden" name="weight">
					<div id="weight-slider"></div>
					<span id="weightValue" class="col-xs-24 align-right"><s:property value="#currentUser.weightInKg"/></span>
					
					<label class="col-xs-24"><s:text name="index.register.height"/></label>
					<input type="hidden" id="heightMin" value="120"/><input type="hidden" id="heightMax" value="220"/><s:if test="#currentUser==null"><input type="hidden" id="heightCurrentValue" value="172"></s:if><s:else><input type="hidden" id="heightCurrentValue" value="<s:property value="#currentUser.heightInCm"/>"></s:else>
					<input id="inputHeight" type="hidden" name="height">
					<div id="height-slider"></div>
					<span id="heightValue" class="col-xs-24 align-right"><s:property value="#currentUser.heightInCm"/></span>
				</div>
			</div>
		</div>
	</div>
	<div class="col-sm-9">
		<h2 class="section-title "><s:text name="user.profile.photo"/></h2>
		<div class="prepend-top">
			<div class="profile-media-container">
				<div class="row">
					<s:iterator value="mediaProvider.media" var="media">
						<div class="user-media">
							<div class="media-actions"><div class="over-toolbox"><a class="but-right img-button" href="<s:property value="mediaProvider.getMoveUrl(#media,1)"/>"><img src="/images/right.png"/></a><a class="but-middle img-button"   data-toggle="modal" data-target="#myModal" href="<s:property value="mediaProvider.getDeletionUrl(#media)"/>"><img src="/images/delete.png"/></a><a class="but-left img-button" href="<s:property value="mediaProvider.getMoveUrl(#media,-1)"/>"><img src="/images/left.png"/></a></div></div>
							<img class="thumb-inner box-shadow" src="<s:property value="getMediaUrl(#media.miniThumbUrl)"/>" />
						</div>
					</s:iterator>
				</div>
			</div>
		</div>
		<div >
			<a class="button <s:property value="headerSupport.getPageStyle()"/> button-fullsize"  data-toggle="modal" data-target="#myModal" href="<s:property value="mediaProvider.getAddMediaDialogUrl()"/>"><s:text name="action.add"/></a>
		</div>
	</div>
	</div>
</div>


	<div class="row col-xs-24 prepend-top">
		<div class="col-sm-offset-1 col-sm-11">
			<div class="row">
				<h2 class="section-title "><s:text name="user.profile.desc"/></h2>
				<div class="prepend-top col-sm-11">
					<s:set value="'col-sm-9'" var="spanclass"/> 
					<tiles:insertTemplate template="/jsp/edition/block-edit-descriptions.jsp"/>
				</div>
				<div class="prepend-top col-sm-offset-1 col-sm-11">
					<s:set value="'col-sm-8'" var="spanclass"/>
					<tiles:insertTemplate template="/jsp/edition/block-edit-properties.jsp"/>
				</div>
				<div class="col-sm-24 clearfix prepend-top">
					<input id="submit-profile" class="button <s:property value="headerSupport.getPageStyle()"/>" type="submit" value="<s:text name="profile.submit.button"/>">
				</div>
			</div>
		</div>
		<div class="col-sm-12">
			<h2 class="section-title "><s:text name="user.profile.style"/></h2>
			<tiles:insertTemplate template="/jsp/edition/block-edit-tags.jsp"/>
		</div>
	</div>
	<div class="col-sm-offset-1 col-sm-23">
		<h2 class="section-title"><s:text name="user.profile.sponsor"/></h2>
		<div class="prepend-top"><s:property value="getText('user.profile.getmore')" escapeHtml="false"/></div> 
		<s:set value="sponsorshipSupport" var="sponsorshipSupport"/>
		<div class="center user-credits"><s:property value="#sponsorshipSupport.getCreditsLabel()"/></div>
		<s:set value="#sponsorshipSupport.getSponsoredElements()" var="sponsoredElts"/>
		<s:if test="#sponsoredElts.isEmpty()">
			<span class="prepend-top"><s:text name="user.profile.nosponsor"/></span>
		</s:if><s:else>
			<div class="prepend-top"><s:text name="user.profile.sponsored"/></div>
			<div class="row">
				<s:iterator value="#sponsoredElts" var="sponsoredElt">
					<s:set value="#sponsorshipSupport.getSponsoredElementIcon(#sponsoredElt)" var="imageUrl"/>
					<s:set value="#sponsorshipSupport.getSponsoredElementUrl(#sponsoredElt)" var="url"/>
					<s:set value="#sponsorshipSupport.getSponsoredElementName(#sponsoredElt)" var="name"/>
					<div class="col-sm-3">
						<div class="col-sm-3 search-result">
							<a href="<s:property value="#url"/>"><img class="col-sm-3 search-thumb" src="<s:property value="#imageUrl"/>"/></a>
							<div class="col-sm-3 search-thumb-text"><a href="<s:property value="#url"/>"><s:property value="#name"/></a></div>
						</div>
					</div>
				</s:iterator>
			</div>
		</s:else>
</div>
</div>
</form>
</div>