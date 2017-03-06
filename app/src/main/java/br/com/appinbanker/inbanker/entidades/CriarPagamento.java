package br.com.appinbanker.inbanker.entidades;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by jonatasilva on 04/03/17.
 */

public class CriarPagamento{

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

    @JsonProperty("PaymentsType")
    private String PaymentsType;

    @JsonProperty("PaymentsCapture")
    private String PaymentsCapture;

    @JsonProperty("CreditCardBrand")
    private String CreditCardBrand;

    @JsonProperty("CreditCardCardNumber")
    private String CreditCardCardNumber;

    @JsonProperty("CreditCardSecurityCode")
    private String CreditCardSecurityCode;

    @JsonProperty("CreditCardExpirationDate")
    private String CreditCardExpirationDate;

    @JsonProperty("CreditCardHolder")
    private String CreditCardHolder;

    @JsonProperty("MerchantOrderId")
    public String getMerchantOrderId() {
        return MerchantOrderId;
    }

    @JsonProperty("MerchantOrderId")
    public void setMerchantOrderId(String merchantOrderId) {
        MerchantOrderId = merchantOrderId;
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

    @JsonProperty("PaymentsType")
    public String getPaymentsType() {
        return PaymentsType;
    }

    @JsonProperty("PaymentsType")
    public void setPaymentsType(String paymentsType) {
        PaymentsType = paymentsType;
    }

    @JsonProperty("PaymentsCapture")
    public String getPaymentsCapture() {
        return PaymentsCapture;
    }

    @JsonProperty("PaymentsCapture")
    public void setPaymentsCapture(String paymentsCapture) {
        PaymentsCapture = paymentsCapture;
    }

    @JsonProperty("CreditCardBrand")
    public String getCreditCardBrand() {
        return CreditCardBrand;
    }

    @JsonProperty("CreditCardBrand")
    public void setCreditCardBrand(String creditCardBrand) {
        CreditCardBrand = creditCardBrand;
    }

    @JsonProperty("CreditCardCardNumber")
    public String getCreditCardCardNumber() {
        return CreditCardCardNumber;
    }

    @JsonProperty("CreditCardCardNumber")
    public void setCreditCardCardNumber(String creditCardCardNumber) {
        CreditCardCardNumber = creditCardCardNumber;
    }

    @JsonProperty("CreditCardSecurityCode")
    public String getCreditCardSecurityCode() {
        return CreditCardSecurityCode;
    }

    @JsonProperty("CreditCardSecurityCode")
    public void setCreditCardSecurityCode(String creditCardSecurityCode) {
        CreditCardSecurityCode = creditCardSecurityCode;
    }

    @JsonProperty("CreditCardExpirationDate")
    public String getCreditCardExpirationDate() {
        return CreditCardExpirationDate;
    }

    @JsonProperty("CreditCardExpirationDate")
    public void setCreditCardExpirationDate(String creditCardExpirationDate) {
        CreditCardExpirationDate = creditCardExpirationDate;
    }

    @JsonProperty("CreditCardHolder")
    public String getCreditCardHolder() {
        return CreditCardHolder;
    }

    @JsonProperty("CreditCardHolder")
    public void setCreditCardHolder(String creditCardHolder) {
        CreditCardHolder = creditCardHolder;
    }
}
