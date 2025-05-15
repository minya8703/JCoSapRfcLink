package jco.jcosaprfclink.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "s_state_taxinvoice")
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class StateTaxinvoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INVOICE_ID", nullable = false)
    private String invoiceId;

    @Column(name = "CHANNEL")
    private String channel;

    @Column(name = "CORP_BIZ_NO")
    private String corpBizNo;

    @Column(name = "MGRDOC_NO")
    private String mgrdocNo;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "RESULT")
    private String result;

    @Column(name = "ERR_CODE")
    private String errCode;

    @Column(name = "ERR_MSG")
    private String errMsg;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "UPDATE_DATE")
    private LocalDateTime updateDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

}
