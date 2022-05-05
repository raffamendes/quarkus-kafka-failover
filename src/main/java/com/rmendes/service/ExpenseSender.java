package com.rmendes.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.rmendes.model.Expense;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;

@ApplicationScoped
public class ExpenseSender {


//	@Inject
//	@Channel("expense-out")
//	Emitter<Expense> sender;

	@Inject
	@Channel("expense-out-failover")
	Emitter<Expense> failoverSender;

	@Inject
	@Channel("expense-out")
	MutinyEmitter<Expense> mutinyEmitter;


	//private CompletionStage<Void> sendExpense(Expense expense) {
		//return sender.send(expense);
//	}

	private CompletionStage<Void> sendExpenseFailOver(Expense expense){
		System.out.println("-------------------send expense fail over--------------------------");
		return failoverSender.send(expense);
	}


	private void sendExpenseWithUni(Expense expense){
		mutinyEmitter.send(expense)
				.map(x -> expense)
				.subscribe().with(
						result -> System.out.println("----------------Message Sent Mutiny-------------"),
						failure -> {
							System.out.println("----------------Message NOT Sent Mutiny-------------");
							expense.desc="Mutiny Failover";
							sendExpenseFailOver(expense);}
						);

	}


	public void sendMessageWithFailover(Expense expense) {
		sendExpenseWithUni(expense);

	}
}
