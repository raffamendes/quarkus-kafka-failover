package com.rmendes.service;

import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.rmendes.model.Expense;

import org.apache.kafka.common.header.internals.RecordHeaders;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

@ApplicationScoped
public class ExpenseSender {

	@Inject
	@Channel("expense-out-failover")
	Emitter<Expense> failoverSender;

	@Inject
	@Channel("expense-out")
	MutinyEmitter<Expense> mutinyEmitter;

	private static final Logger LOG = Logger.getLogger(ExpenseSender.class);

	private void sendExpenseFailOver(Expense expense){
		failoverSender.send(Message.of(expense)
				.addMetadata(OutgoingKafkaRecordMetadata.<String> builder()
						.withHeaders(new RecordHeaders().add("channel", "failover".getBytes(StandardCharsets.UTF_8))).build())
				);
	}


	private void sendExpenseWithUni(Expense expense){
		mutinyEmitter.send(expense)
		.map(x -> expense)
		.subscribe().with(
				result -> LOG.info("------------------Message Sent using the main channel--------------------------"),
				failure -> {
					LOG.info("-------------------------------Sending Message using the failover channel---------------------");
					sendExpenseFailOver(expense);}
				);

	}


	public void sendMessageWithFailover(Expense expense) {
		sendExpenseWithUni(expense);

	}
}
