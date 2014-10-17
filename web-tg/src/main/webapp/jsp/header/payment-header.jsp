<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<tiles:insertTemplate template="/jsp/header/default-header.jsp"/>
<script type="text/javascript">
  var PAYMILL_PUBLIC_KEY = '<s:property value="paymentSupport.getBusinessId()"/>';
  var paymentErrors = {};
  paymentErrors['3ds_cancelled'] = "<s:text name="promote.pay.error.3ds_cancelled"/>";
  paymentErrors['field_invalid_card_number'] = "<s:text name="promote.pay.error.field_invalid_card_number"/>";
  paymentErrors['field_invalid_card_exp_year'] = "<s:text name="promote.pay.error.field_invalid_card_exp_year"/>";
  paymentErrors['field_invalid_card_exp_month'] = "<s:text name="promote.pay.error.field_invalid_card_exp_month"/>";
  paymentErrors['field_invalid_card_exp'] = "<s:text name="promote.pay.error.field_invalid_card_exp"/>";
  paymentErrors['field_invalid_card_cvc'] = "<s:text name="promote.pay.error.field_invalid_card_cvc"/>";
  paymentErrors['field_invalid_card_holder'] = "<s:text name="promote.pay.error.field_invalid_card_holder"/>";
  paymentErrors['field_invalid_amount_int'] = "<s:text name="promote.pay.error.field_invalid_amount_int"/>";
  paymentErrors['field_invalid_amount'] = "<s:text name="promote.pay.error.field_invalid_amount"/>";
  paymentErrors['field_invalid_currency'] = "<s:text name="promote.pay.error.field_invalid_currency"/>";
</script>
<script type="text/javascript" src="https://bridge.paymill.com/"></script>