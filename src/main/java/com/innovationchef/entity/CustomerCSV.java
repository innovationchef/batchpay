package com.innovationchef.entity;


import com.innovationchef.constant.DBConstant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Immutable
@Table(name = DBConstant.CUSTOMER_LANDING_TBL)
public class CustomerCSV extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1234567890L;

    @Id
    @Setter(AccessLevel.NONE)
    @Column(name = DBConstant.DB_KEY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int tid;

    @Column(name = "ACCOUNT_NO")
    private String accountNo;

    @Column(name = "NAME")
    private String name;

    @Column(name = "INTEREST_RATE")
    private String interestRate;

    @Column(name = "ACCOUNT_STATUS")
    private String accountStatus;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "ACTION")
    private String action;
}
