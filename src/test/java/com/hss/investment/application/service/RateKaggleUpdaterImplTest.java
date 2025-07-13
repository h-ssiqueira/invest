package com.hss.investment.application.service;

import com.hss.investment.application.exception.InvestmentException;
import com.hss.investment.application.persistence.ConfigurationDao;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static com.hss.investment.application.exception.ErrorMessages.INV_005;
import static java.nio.file.Files.readAllBytes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(SpringExtension.class)
class RateKaggleUpdaterImplTest {

    @Mock
    private RateServiceImpl rateService;

    @Mock
    private RestTemplate client;

    @Mock
    private ConfigurationDao configurationDao;

    @InjectMocks
    private RateKaggleUpdaterImpl updater;

    @BeforeEach
    void init() {
        setField(updater, "client", client);
        setField(updater, "username", "user");
        setField(updater, "key", "key");
    }

    @Test
    void shouldGetDatasetSuccessfully() {
        byte[] content = new byte[0];
        try {
            content = readAllBytes(Paths.get(RateKaggleUpdaterImplTest.class.getClassLoader().getResource("brazil-interest-rate-history-selic.zip").toURI()));
        } catch (Exception e){
            // empty
        }

        when(client.exchange(any(RequestEntity.class), any(Class.class))).thenReturn(ResponseEntity.ok(content));

        updater.retrieveAndUpdateRates();

        assertAll(
            () -> verify(client).exchange(any(RequestEntity.class), any(Class.class)),
            () -> verify(rateService).processIpca(any()),
            () -> verify(rateService).processSelic(any()),
            () -> verify(configurationDao).save(any())
        );
    }

    @Test
    void shouldThrowExceptionWhileUpdatingRates() {
        byte[] content = new byte[0];
        try {
            content = readAllBytes(Paths.get(RateKaggleUpdaterImplTest.class.getClassLoader().getResource("brazil-interest-rate-history-selic.zip").toURI()));
        } catch (Exception e){
            // empty
        }

        when(client.exchange(any(RequestEntity.class), any(Class.class))).thenReturn(ResponseEntity.ok(content));
        doThrow(RuntimeException.class).when(rateService).processIpca(any());

        assertThrows(InvestmentException.class, () -> updater.retrieveAndUpdateRates());

        assertAll(
            () -> verify(client).exchange(any(RequestEntity.class), any(Class.class)),
            () -> verify(rateService).processIpca(any()),
            () -> verify(rateService).processSelic(any())
        );
    }

    @Test
    void shouldReturnBadRequestWhenRetrievingDataset() {
        when(client.exchange(any(RequestEntity.class), any(Class.class))).thenReturn(ResponseEntity.badRequest().build());

        updater.retrieveAndUpdateRates();

        assertAll(
            () -> verify(client).exchange(any(RequestEntity.class), any(Class.class)),
            () -> verifyNoInteractions(rateService, configurationDao)
        );
    }

    @Test
    void shouldReturnErrorWhenIntegratingWithKaggle() {
        when(client.exchange(any(RequestEntity.class), any(Class.class))).thenThrow(RuntimeException.class);

        var ex = assertThrows(InvestmentException.class, () -> updater.retrieveAndUpdateRates());

        assertAll(
            () -> assertThat(ex.getMessage(), equalTo(INV_005.formatted(null))),
            () -> verify(client).exchange(any(RequestEntity.class), any(Class.class)),
            () -> verifyNoInteractions(rateService, configurationDao)
        );
    }
}