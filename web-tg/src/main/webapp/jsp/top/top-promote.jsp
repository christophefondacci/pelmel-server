<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<div class="prepend-top append-bottom col-sm-24">
	<h1 class="profile section-title section-login"><s:text name="promote.title"/></h1>
</div>
<div class="col-sm-offset-1 col-sm-23">
	<h2 class="section-title main-info-title"><s:text name="promote.why.title"/></h2>
	<span class="prepend-top"><s:text name="promote.why.desc"/></span>
		<div class="prepend-top">
			<h2 class="section-title main-info-title"><s:text name="promote.what.title"/></h2>
			<div class="prepend-top">
				<s:text name="promote.what.intro"/>
			</div>
			<div class="row prepend-top">
				<div class="col-xs-10 col-md-offset-3 col-md-5 prepend-top center"><img alt="pelmel-app-screenshot" src="/images/V2/screenshots/nearby-boost-small.png"></div>
				<div class="col-xs-14 prepend-top">
					<span><s:property value="getText('promote.what.desc1')" escapeHtml="false"/></span>
					<div class="center prepend-top"><tiles:insertTemplate template="/jsp/blocks/block-apps.jsp"/></div>
				</div>
			</div>
			<div class="row prepend-top">
				<div class="col-xs-10 col-md-offset-3 col-md-5 prepend-top center"><img alt="pelmel-website-screenshot" class="img-responsive" src="/images/V2/screenshots/search-boost-small.png"></div>
				<span class="col-xs-14 prepend-top last"><s:text name="promote.what.desc2"/></span>
			</div>
			<div class="row prepend-top">
				<div class="col-xs-10 col-md-offset-3 col-md-5 prepend-top center"><img alt="pelmel-website-screenshot-2" class="img-responsive" src="/images/V2/screenshots/lock-content.png"></div>
				<span class="col-xs-14 prepend-top last"><s:text name="promote.what.desc3"/></span>
			</div>
			<div class="row prepend-top">
				<div class="col-xs-10 col-md-offset-3 col-md-5 prepend-top center"><img alt="pelmel-website-banners" class="img-responsive" src="/images/V2/screenshots/ad-banner.png"></div>
				<span class="col-xs-14 prepend-top last"><s:text name="promote.what.desc4"/></span>
			</div>
		</div>		
		
		<div class="prepend-top">
			<h2 class="section-title main-info-title"><s:text name="promote.how.title"/></h2>
			<span class="prepend-top last"><s:text name="promote.how.desc"/></span>
			<s:if test="!isLogged()">
				<form action="/userLogin.action" method="post">
					<input type="hidden" name="url" value="/promote">
					<input type="hidden" name="email" placeholder="Email">
					<input type="hidden" name="password" placeholder="Mot de passe">
					<div class="center prepend-top">
						<input type="submit" class="button profile" value="<s:text name="promote.login.required"/>">
					</div>
				</form>
			</s:if><s:else>
				<s:set value="paymentSupport" var="paymentSupport"/>
				<span class="col-xs-24 prepend-top center user-credits">
					<s:property value="#paymentSupport.getCreditsLabel()"/>
				</span>
				<div class="prepend-top">
					<form id="payment-form" action="<s:property value="#paymentSupport.getPaymentUrl()"/>" method="POST">
<!-- 						<div class="col-sm-18 last"> -->
							<span class="col-sm-8 credits-text"><s:text name="promote.getCredits"/></span>
							<select name="amount" class="col-sm-3 card-amount-int">
								<s:iterator value="#paymentSupport.getAvailableAmounts()" var="amount">
									<option value="<s:property value="#paymentSupport.getPriceOptionValue(#amount)"/>"><s:property value="#paymentSupport.getPriceLabel(#amount)"/></option>
								</s:iterator>
							</select>
<!-- 						</div> -->
						<div class="col-xs-24 prepend-top last"><s:property value="getText('promote.pay.secured')" escapeHtml="false"/></div>
						<input name="currency" class="card-currency" type="hidden" value="EUR" />
						<div class="col-xs-24 prepend-top last">
							<label class="col-sm-8 text-right"><s:text name="promote.pay.creditcard"/></label>
							<input class="col-sm-5 card-number" type="text" size="20" />
						</div>
						<div class="col-xs-24 last">
							<label class="col-sm-8 text-right"><s:text name="promote.pay.cvc"/></label>
							<input class="card-cvc col-sm-2" type="text" maxlength="4" />
							<span class="col-sm-8 last cvc-desc"><s:text name="promote.pay.cvc.desc"/></span>
						</div>
						<div class="col-xs-24 last">
							<label class="col-sm-8 text-right"><s:text name="promote.pay.name"/></label>
							<input class="card-holdername col-sm-8" type="text" />
						</div>
						<div class="col-xs-24 last">
							<label class="col-sm-8 text-right"><s:text name="promote.pay.expires"/></label>
							<input class="card-expiry-month col-sm-1" type="text" maxlength="2" size="2" />
							
							
							
							<div class="col-sm-4">&nbsp;/&nbsp;
								<input class="card-expiry-year" type="text" maxlength="4" size="4" />
							</div>
						</div>

						<div class="col-xs-24 prepend-top center last">
							<input class="submit-button button profile" type="submit" value="<s:text name="promote.pay.button"/>"/>
<%-- 							<span class="big-font"><s:text name="promote.pay.disabled"></s:text></span>  --%>
						</div>
						<div class="col-xs-24 payment-errors center"></div>
						<div class="col-xs-24 prepend-top"><s:text name="promote.pay.conditions"/></div>
					</form>
				</div>
			</s:else>			
		</div>
</div>








