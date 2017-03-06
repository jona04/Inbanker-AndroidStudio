package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jonatasilva on 04/03/17.
 */

public class CriarPagamentoToken {

    @JsonProperty("MerchantOrderId")
    private String MerchantOrderId;

    @JsonProperty("IdRequest")
    private String IdRequest;

    @JsonProperty("ClientAcount")
    private String ClientAcount;

    @JsonProperty("ClientKey")
    private String ClientKey;

    @JsonProperty("CustomerName")
    private String CustomerName;

    @JsonProperty("PaymentsAmount")
    private String PaymentsAmount;

    @JsonProperty("PaymentsInstallments")
    private String PaymentsInstallments;

    @JsonProperty("PaymentsSoftDescriptor")
    private String PaymentsSoftDescriptor;

    @JsonProperty("PaymentsCapture")
    private String PaymentsCapture;

    @JsonProperty("Token")
    private String Token;

    @JsonProperty("MerchantOrderId")
    public String getMerchantOrderId() {
        return MerchantOrderId;
    }

    @JsonProperty("MerchantOrderId")
    public void setMerchantOrderId(String merchantOrderId) {
        MerchantOrderId = merchantOrderId;
    }

    @JsonProperty("Token")
    public String getToken() {
        return Token;
    }

    @JsonProperty("Token")
    public void setToken(String token) {
        Token = token;
    }

    @JsonProperty("IdRequest")
    public String getIdRequest() {
        return IdRequest;
    }

    @JsonProperty("IdRequest")
    public void setIdRequest(String idRequest) {
        IdRequest = idRequest;
    }

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

    @JsonProperty("CustomerName")
    public String getCustomerName() {
        return CustomerName;
    }

    @JsonProperty("CustomerName")
    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    @JsonProperty("PaymentsAmount")
    public String getPaymentsAmount() {
        return PaymentsAmount;
    }

    @JsonProperty("PaymentsAmount")
    public void setPaymentsAmount(String paymentsAmount) {
        PaymentsAmount = paymentsAmount;
    }

    @JsonProperty("PaymentsInstallments")
    public String getPaymentsInstallments() {
        return PaymentsInstallments;
    }

    @JsonProperty("PaymentsInstallments")
    public void setPaymentsInstallments(String paymentsInstallments) {
        PaymentsInstallments = paymentsInstallments;
    }

    @JsonProperty("PaymentsSoftDescriptor")
    public String getPaymentsSoftDescriptor() {
        return PaymentsSoftDescriptor;
    }

    @JsonProperty("PaymentsSoftDescriptor")
    public void setPaymentsSoftDescriptor(String paymentsSoftDescriptor) {
        PaymentsSoftDescriptor = paymentsSoftDescriptor;
    }

    @JsonProperty("PaymentsCapture")
    public String getPaymentsCapture() {
        return PaymentsCapture;
    }

    @JsonProperty("PaymentsCapture")
    public void setPaymentsCapture(String paymentsCapture) {
        PaymentsCapture = paymentsCapture;
    }

}
