package com.rmendes;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rmendes.model.Expense;
import com.rmendes.service.ExpenseSender;

@Path("/expense")
public class ExpenseResource {
	
	@Inject
	ExpenseSender expenseSender;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from RESTEasy Reactive";
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addExpense(Expense expense) {
    		expenseSender.sendMessageWithFailover(expense);
    		return Response.accepted(expense).build();
    }
}