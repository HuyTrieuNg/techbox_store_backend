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
@Table(name = "cod_payments")
@PrimaryKeyJoinColumn(name = "payment_id")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CodPayment extends Payment {

    @Column(name = "collected_by", length = 100)
    private String collectedBy;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "confirmation_note", columnDefinition = "TEXT")
    private String confirmationNote;
}
