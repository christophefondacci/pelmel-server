<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<tiles:insertTemplate template="/jsp/footer/default-footer.jsp"/>
<script type="text/javascript">
function PaymillResponseHandler(error, result) {
  if (error) {
    // Shows the error above the form
    $(".payment-errors").text(paymentErrors[error.apierror]);
    $('.submit-button').text('<s:text name="promote.pay.button"/>');
    $(".submit-button").removeAttr("disabled");
  } else {
    var form = $("#payment-form");
    // Output token
    var token = result.token;
    // Insert token into form in order to submit to server
    form.append("<input type='hidden' name='paymentToken' value='" + token + "'/>");
    form.get(0).submit();
    return false;
  }
}

$(document).ready(function() {
	  $("#payment-form").submit(function(event) {
	    // Deactivate submit button to avoid further clicks
	    $(".payment-errors").text('');
	    $('.submit-button').text('<s:text name="promote.pay.button.wait"/>');
	    $('.submit-button').attr("disabled", "disabled");
	    
	    if (false == paymill.validateCardNumber($('.card-number').val())) {
            $(".payment-errors").text('<s:text name="promote.pay.error.field_invalid_card_number"/>');
            $('.submit-button').text('<s:text name="promote.pay.button"/>');
            $(".submit-button").removeAttr("disabled");
            return false;
        }

        if (false == paymill.validateExpiry($('.card-expiry-month').val(), $('.card-expiry-year').val())) {
            $(".payment-errors").text('<s:text name="promote.pay.error.field_invalid_card_exp"/>');
            $('.submit-button').text('<s:text name="promote.pay.button"/>');
            $(".submit-button").removeAttr("disabled");
            return false;
        }

	    paymill.createToken({
	      number: $('.card-number').val(),   // required, without spaces and hyphens
	      exp_month: $('.card-expiry-month').val(),   // required
	      exp_year: $('.card-expiry-year').val(),     // required, four digits e.g. "2016"
	      cvc: $('.card-cvc').val(),                  // required
	      amount_int: $('.card-amount-int').val(),     // required, integer e.g. "15" for 0.15 EUR
	      currency: $('.card-currency').val(), // required, ISO 4217 e.g. "EUR" or "GBP"
	      cardholder: $('.card-holdername').val()     // optional
	    }, PaymillResponseHandler);                   // this function is described below

	    return false;
	  });
	});
</script>