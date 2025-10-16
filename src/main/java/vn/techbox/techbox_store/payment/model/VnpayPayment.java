package vn.techbox.techbox_store.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "vnpay_payments")
@PrimaryKeyJoinColumn(name = "payment_id")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VnpayPayment extends Payment {

    @Column(name = "vnp_transaction_no", length = 50)
    private String vnpTransactionNo;

    @Column(name = "vnp_txn_ref", length = 50)
    private String vnpTxnRef;

    @Column(name = "vnp_response_code", length = 10)
    private String vnpResponseCode;

    @Column(name = "vnp_bank_code", length = 50)
    private String vnpBankCode;

    @Column(name = "vnp_order_info", columnDefinition = "TEXT")
    private String vnpOrderInfo;

    @Column(name = "vnp_secure_hash", length = 255)
    private String vnpSecureHash;

    @Column(name = "vnp_payment_date")
    private LocalDateTime vnpPaymentDate;
}
