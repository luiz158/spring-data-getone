package com.example.springdatagetone.app.rest.controller;

import com.example.springdatagetone.app.rest.controller.dto.CustomerDto;
import com.example.springdatagetone.domain.model.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by mtumilowicz on 2018-08-08.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void findAll_status() {
//        expect
        assertThat(restTemplate
                        .getForEntity(
                                createURLWithPort("customers"),
                                null)
                        .getStatusCode(),
                is(HttpStatus.OK));
    }

    @Test
    @Sql(value = {"findAll_entities_delete.sql", "findAll_entities_insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "findAll_entities_delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findAll_entities() {
//        given
        List<Customer> expectedCustomers = Collections.singletonList(Customer.builder()
                .id(1)
                .firstName("firstName")
                .build());

//        when
        List<Customer> customers = restTemplate
                .exchange(
                        createURLWithPort("customers"),
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Customer>>() {
                        }).getBody();

//        then
        assertThat(customers,
                is(expectedCustomers));
    }

    @Test
    @Sql(value = {"findById_found_delete.sql", "findById_found_insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "findById_found_delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_found_status() {
//        expect
        assertThat(restTemplate
                        .getForEntity(
                                createURLWithPort("customers/1"),
                                null)
                        .getStatusCode(),
                is(HttpStatus.OK));
    }

    @Test
    @Sql(value = {"findById_found_delete.sql", "findById_found_insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "findById_found_delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void findById_found_entity() {
//        given
        Customer expectedCustomer = Customer.builder()
                .id(1)
                .firstName("firstName")
                .build();

//        when
        Customer customer = restTemplate
                .getForObject(createURLWithPort("customers/1"), Customer.class);

//        then
        assertThat(customer,
                is(expectedCustomer));
    }

    @Test
    @Sql(value = {"findById_found_delete.sql", "findById_found_insert.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "findById_found_delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void update_found_entity() {
//        given
        Customer expectedCustomer = Customer.builder()
                .id(1)
                .firstName("changed")
                .build();

//        and
        CustomerDto customerDto = CustomerDto.builder()
                .id(1)
                .firstName("changed")
                .build();

//        when
        restTemplate
                .put(createURLWithPort("customers"), customerDto);

//        then
        assertThat(restTemplate
                        .getForObject(createURLWithPort("customers/1"), Customer.class),
                is(expectedCustomer));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
