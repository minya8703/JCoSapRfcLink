package jco.jcosaprfclink.repository;

import jco.jcosaprfclink.domain.StateTaxinvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxinvoiceStateRepository extends JpaRepository<StateTaxinvoice, Long> {
    Optional<StateTaxinvoice> findByInvoiceId(String invoiceId);
}
