package io.everyonecodes.cryptolog.controller;

import io.everyonecodes.cryptolog.service.ConfirmationTokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfirmationControllerTest {

    @Autowired
    ConfirmationController confirmationController;

    @MockBean
    ConfirmationTokenService service;

    @Test
    void confirm() {
        confirmationController.confirm(Mockito.any());
        Mockito.verify(service).confirmToken(Mockito.any());
    }
}