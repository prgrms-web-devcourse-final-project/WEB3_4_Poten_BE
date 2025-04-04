package com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.res;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRes {
    @JsonProperty("mId")
    private String mid;

    private String version;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private String currency;
    private String method;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private Boolean useEscrow;
    private Boolean cultureExpense;
    private String type;
    private String country;
    private String lastTransactionKey;
    private Boolean isPartialCancelable;

    @JsonProperty("totalAmount")
    private Long totalAmount;

    @JsonProperty("balanceAmount")
    private Long balanceAmount;

    @JsonProperty("suppliedAmount")
    private Long suppliedAmount;

    @JsonProperty("vat")
    private Long vat;

    @JsonProperty("taxFreeAmount")
    private Long taxFreeAmount;

    @JsonProperty("taxExemptionAmount")
    private Long taxExemptionAmount;

    // 결제 취소 이력
    private List<CancelHistory> cancels;

    // 카드 결제 정보
    private CardPaymentInfo card;

    // 가상계좌 결제 정보
    private VirtualAccountInfo virtualAccount;

    // 휴대폰 결제 정보
    private MobilePhoneInfo mobilePhone;

    // 상품권 결제 정보
    private GiftCertificateInfo giftCertificate;

    // 계좌이체 정보
    private TransferInfo transfer;

    // 간편결제 정보
    private EasyPayInfo easyPay;

    // 현금영수증 정보
    private CashReceiptInfo cashReceipt;

    // 현금영수증 발행 및 취소 이력
    private List<CashReceiptHistory> cashReceipts;

    // 영수증 정보
    private ReceiptInfo receipt;

    // 결제창 정보
    private CheckoutInfo checkout;

    // 결제 실패 정보
    private FailureInfo failure;

    // 메타데이터
    private Map<String, String> metadata;

    // 할인 정보
    private DiscountInfo discount;

    // 비밀 값 (웹훅 검증용)
    private String secret;

    @Getter
    @Setter
    public static class CancelHistory {
        private Long cancelAmount;
        private String cancelReason;
        private Long taxFreeAmount;
        private Long taxExemptionAmount;
        private Long refundableAmount;
        private Long transferDiscountAmount;
        private Long easyPayDiscountAmount;
        private OffsetDateTime canceledAt;
        private String transactionKey;
        private String receiptKey;
        private String cancelStatus;
        private String cancelRequestId;
    }

    @Getter
    @Setter
    public static class CardPaymentInfo {
        private Long amount;
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private Boolean isInterestFree;
        private String interestPayer;
        private String receiptUrl;
    }

    @Getter
    @Setter
    public static class VirtualAccountInfo {
        private String accountType;
        private String accountNumber;
        private String bankCode;
        private String customerName;
        private OffsetDateTime dueDate;
        private String refundStatus;
        private Boolean expired;
        private String settlementStatus;
        private RefundReceiveAccountInfo refundReceiveAccount;
    }

    @Getter
    @Setter
    public static class RefundReceiveAccountInfo {
        private String bankCode;
        private String accountNumber;
        private String holderName;
    }

    @Getter
    @Setter
    public static class MobilePhoneInfo {
        private String customerMobilePhone;
        private String settlementStatus;
        private String receiptUrl;
    }

    @Getter
    @Setter
    public static class GiftCertificateInfo {
        private String approveNo;
        private String settlementStatus;
    }

    @Getter
    @Setter
    public static class TransferInfo {
        private String bankCode;
        private String settlementStatus;
    }

    @Getter
    @Setter
    public static class EasyPayInfo {
        private String provider;
        private Long amount;
        private Long discountAmount;
    }

    @Getter
    @Setter
    public static class CashReceiptInfo {
        private String type;
        private String receiptKey;
        private String issueNumber;
        private String receiptUrl;
        private Long amount;
        private Long taxFreeAmount;
    }

    @Getter
    @Setter
    public static class CashReceiptHistory {
        private String receiptKey;
        private String orderId;
        private String orderName;
        private String type;
        private String issueNumber;
        private String receiptUrl;
        private String businessNumber;
        private String transactionType;
        private Long amount;
        private Long taxFreeAmount;
        private String issueStatus;
        private FailureInfo failure;
        private String customerIdentityNumber;
        private OffsetDateTime requestedAt;
    }

    @Getter
    @Setter
    public static class ReceiptInfo {
        private String url;
    }

    @Getter
    @Setter
    public static class CheckoutInfo {
        private String url;
    }

    @Getter
    @Setter
    public static class FailureInfo {
        private String code;
        private String message;
    }

    @Getter
    @Setter
    public static class DiscountInfo {
        private Long amount;
    }
}