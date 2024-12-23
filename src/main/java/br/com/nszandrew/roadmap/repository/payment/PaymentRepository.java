package br.com.nszandrew.roadmap.repository.payment;

import br.com.nszandrew.roadmap.model.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
