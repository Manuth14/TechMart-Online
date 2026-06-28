package lk.techmart.core.service;

import jakarta.ejb.Remote;

import java.util.concurrent.Future;

@Remote
public interface InvoiceService {
    Future<Boolean> generateInvoiceAndEmail(String orderId, String customerEmail);
}
