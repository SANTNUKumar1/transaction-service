package com.paypal.transaction_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.transaction_service.entity.Transaction;
import com.paypal.transaction_service.kafka.KafkaEventProducer;
import com.paypal.transaction_service.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;

    private final ObjectMapper objectMapper;

    private final KafkaEventProducer kafkaEventProducer;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public TransactionServiceImpl(TransactionRepository transactionRepository, ObjectMapper objectMapper, KafkaEventProducer kafkaEventProducer) {
        this.transactionRepository = transactionRepository;
        this.objectMapper = objectMapper;
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public Transaction createTransaction(Transaction request) {
        
        Long senderId = request.getSenderId();
        Long receiverId = request.getReceiverId();
        Double amount = request.getAmount();

        Transaction transaction = new Transaction();
        transaction.setSenderId(senderId);
        transaction.setReceiverId(receiverId);
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus("Success");

        Transaction savedTransaction = transactionRepository.save(transaction);

        try {
            System.out.println("[TRACE] Transaction saved, preparing Kafka event");
            String eventPayload = objectMapper.writeValueAsString(savedTransaction);
            String key = String.valueOf(savedTransaction.getId());
            System.out.println("[TRACE] Sending Kafka event for transaction id=" + key);
            kafkaEventProducer.sendTransactionEvent(key, savedTransaction);
            System.out.println("[TRACE] Returned from Kafka send call");
        } catch (Exception e) {
            System.err.println("Failed to send transaction event to Kafka: " + e.getMessage());
            e.printStackTrace();
        }
        return savedTransaction;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        // Implement the logic to retrieve all transactions
        return transactionRepository.findAll();
    }

}
