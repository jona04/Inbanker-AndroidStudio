package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jonatasilva on 06/03/17.
 */

public class AlteraPagamento {


    @JsonProperty("ClientAcount")
    private String ClientAcount;

    @JsonProperty("ClientKey")
    private String ClientKey;

    @JsonProperty("PaymentId")
    private String PaymentId;

    @JsonProperty("OptionId")
    private String OptionId;

    @JsonProperty("NewValue")
    private String NewValue;

    @JsonProperty("ClientAcount")
    public String getClientAcount() {
        return ClientAcount;
    }

    @JsonProperty("ClientAcount")
    public void setClientAcount(String clientAcount) {
        ClientAcount = clientAcount;
    }

    @JsonProperty("ClientKey")
    public String getClientKey() {
        return ClientKey;
    }

    @JsonProperty("ClientKey")
    public void setClientKey(String clientKey) {
        ClientKey = clientKey;
    }

    @JsonProperty("PaymentId")
    public String getPaymentId() {
        return PaymentId;
    }

    @JsonProperty("PaymentId")
    public void setPaymentId(String paymentId) {
        PaymentId = paymentId;
    }

    @JsonProperty("OptionId")
    public String getOptionId() {
        return OptionId;
    }

    @JsonProperty("OptionId")
    public void setOptionId(String optionId) {
        OptionId = optionId;
    }

    @JsonProperty("NewValue")
    public String getNewValue() {
        return NewValue;
    }

    @JsonProperty("NewValue")
    public void setNewValue(String newValue) {
        NewValue = newValue;
    }
}
