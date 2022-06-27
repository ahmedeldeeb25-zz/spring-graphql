package com.example.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@SpringBootApplication
public class GraphQlApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQlApplication.class, args);
    }

}

@Controller
class CustomerGraphQLController {

    CustomerRepository repository;

    CustomerGraphQLController(CustomerRepository repository) {
        this.repository = repository;
    }


    @SchemaMapping(typeName = "Customer")
    Flux<Order> orders(Customer customer){
        var orders = new ArrayList<Order>();
        for(var orderId=1; orderId <= (Math.random() * 100); orderId++){
            orders.add(new Order(orderId,customer.id()));
        }

        return Flux.fromIterable(orders);
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.repository.findAll();
    }

    @QueryMapping
    Flux<Customer> customerByName(@Argument String name){
        return this.repository.findByName(name);
    }

    @MutationMapping
    Mono<Customer> addCustomer(@Argument String name){
        return this.repository.save(new Customer(null,name));
    }
}


interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
    Flux<Customer> findByName(String name);
}

record Order(@Id Integer id, Integer customerId) {
}

record Customer(@JsonProperty("id") @Id Integer id, String name) {
}

