package com.innovationchef.entity;

import com.innovationchef.constant.ChargeBearer;
import com.innovationchef.constant.DBConstant;
import com.innovationchef.constant.PaymentStatus;
import com.innovationchef.support.ChargeBearerConverter;
import com.innovationchef.support.PaymentStatusConverter;
import com.innovationchef.support.UUIDConverter;
import lombok.AccessLevel;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = DBConstant.PAIN_001_LANDING_TBL)
public class Pain001CSV extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1234567890L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = DBConstant.DB_KEY, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int tid;

    @NaturalId
    @Convert(converter = UUIDConverter.class)
    @Column(name = "PAYMENT_ID", updatable = false)
    private UUID paymentId;

    @Convert(converter = ChargeBearerConverter.class)
    @Column(name = "CHARGE_BEARER", updatable = false)
    private ChargeBearer chargeBearer;

    @Column(name = "RQST_EXEC_DT", updatable = false)
    private LocalDate requestedExecutionOn;

    @Column(name = "DBTR_ACC_NO", length = 14, updatable = false)
    private String debtorAccountNo;

    @Column(name = "CDTR_ACC_NO", length = 14, updatable = false)
    private String creditorAccountNo;

    @Convert(converter = UUIDConverter.class)
    @Column(name = "END_TO_END_ID", updatable = false)
    private UUID endToEndId;

    @Column(name = "CURRENCY", length = 3, updatable = false)
    private String currency;

    @Column(name = "AMOUNT", length = 2, updatable = false)
    private BigDecimal amount;

    @Convert(converter = PaymentStatusConverter.class)
    @Column(name = "STATUS")
    private PaymentStatus status;

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID paymentId;
        private ChargeBearer chargeBearer;
        private LocalDate requestedExecutionOn;
        private String debtorAccountNo;
        private String creditorAccountNo;
        private UUID endToEndId;
        private String currency;
        private BigDecimal amount;

        public Builder forPayment(String payId, String e2eId) {
            this.paymentId = UUID.fromString(payId);
            this.endToEndId = UUID.fromString(e2eId);
            return this;
        }

        public Builder from(String dbtrAccNo) {
            this.debtorAccountNo = dbtrAccNo;
            return this;
        }

        public Builder to(String cdtrAccNo) {
            this.creditorAccountNo = cdtrAccNo;
            return this;
        }

        public Builder forTxnAmt(BigDecimal amount, String currency) {
            this.amount = amount;
            this.currency = currency;
            return this;
        }

        public Builder toBeExecOn(String date) {
            this.requestedExecutionOn = LocalDate.parse(date);
            return this;
        }

        public Builder withBearer(String bearer) {
            this.chargeBearer = ChargeBearer.getBearerType(bearer);
            return this;
        }

        public Pain001CSV build() {
            Pain001CSV pain001CSV = new Pain001CSV();
            pain001CSV.paymentId = this.paymentId;
            pain001CSV.chargeBearer = this.chargeBearer;
            pain001CSV.requestedExecutionOn = this.requestedExecutionOn;
            pain001CSV.debtorAccountNo = this.debtorAccountNo;
            pain001CSV.creditorAccountNo = this.creditorAccountNo;
            pain001CSV.endToEndId = this.endToEndId;
            pain001CSV.currency = this.currency;
            pain001CSV.amount = this.amount;
            return pain001CSV;
        }
    }
}
