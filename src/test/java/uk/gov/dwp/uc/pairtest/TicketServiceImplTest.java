package uk.gov.dwp.uc.pairtest;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.configs.ConfigurationProvider;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validators.TicketPurchaseValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TicketServiceImplTest {

    private TicketPaymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketPurchaseValidator validator;
    private TicketServiceImpl service;

    @BeforeEach
    void setup() {
        paymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        validator = mock(TicketPurchaseValidator.class);

        service = new TicketServiceImpl(validator, paymentService, seatReservationService);
    }

    @Test
    void successfulPurchaseShouldInvokePaymentAndSeatReservation() {
        Long accountId = 10L;

        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        doNothing().when(validator).validate(accountId, adult, child);

        Logger logger = (Logger) LoggerFactory.getLogger(TicketServiceImpl.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        service.purchaseTickets(accountId, adult, child);

        int expectedAmount =
                (2 * ConfigurationProvider.ADULT_PRICE)
                        + (1 * ConfigurationProvider.CHILD_PRICE);
        int expectedSeats = 2 + 1;

        verify(paymentService, times(1)).makePayment(accountId, expectedAmount);
        verify(seatReservationService, times(1)).reserveSeat(accountId, expectedSeats);

        List<String> messages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertTrue(messages.contains("Payment is made for GBP:" + expectedAmount));
        assertTrue(messages.contains(expectedSeats + " seats have been booked for this purchase."));
        assertTrue(messages.contains("Ticket purchase completed."));
    }

    @Test
    void successfulPurchaseShouldBeWithTicketTypeRequestArray() {
        Long accountId = 10L;

        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        TicketTypeRequest[] ticketTypeRequests = { adult, child };

        doNothing().when(validator).validate(accountId, ticketTypeRequests);

        service.purchaseTickets(accountId, ticketTypeRequests);

        int expectedAmount =
                (adult.getNoOfTickets() * ConfigurationProvider.ADULT_PRICE)
                        + (child.getNoOfTickets() * ConfigurationProvider.CHILD_PRICE);
        int expectedSeats = adult.getNoOfTickets() + child.getNoOfTickets();

        verify(paymentService, times(1)).makePayment(accountId, expectedAmount);
        verify(seatReservationService, times(1)).reserveSeat(accountId, expectedSeats);
    }

    @Test
    void invalidPurchaseShouldNotCallExternalServices() {
        Long accountId = 1L;
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);

        doThrow(new InvalidPurchaseException("No adult")).when(validator)
                .validate(accountId, child);

        assertThrows(InvalidPurchaseException.class,
                () -> service.purchaseTickets(accountId, child));

        verifyNoInteractions(paymentService);
        verifyNoInteractions(seatReservationService);
    }

    @Test
    void zeroSeatsShouldNotCallSeatReservation() {
        Long accountId = 1L;

        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        doNothing().when(validator).validate(accountId, infant, adult);

        Logger logger = (Logger) LoggerFactory.getLogger(TicketServiceImpl.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        service.purchaseTickets(accountId, infant, adult);

        verify(paymentService, never()).makePayment(anyLong(), anyInt());
        verify(seatReservationService, never()).reserveSeat(anyLong(), anyInt());

        List<String> messages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertTrue(messages.contains("Nothing to pay for this booking."));
        assertTrue(messages.contains("No seat is booked for this purchase."));
        assertTrue(messages.contains("Ticket purchase completed."));
    }

    @Test
    void exactly25TicketsShouldWork() {
        Long accountId = 1L;

        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25);

        doNothing().when(validator).validate(accountId, adult);

        service.purchaseTickets(accountId, adult);

        int expectedAmount = 25 * ConfigurationProvider.ADULT_PRICE;
        int expectedSeats = 25;

        verify(paymentService).makePayment(accountId, expectedAmount);
        verify(seatReservationService).reserveSeat(accountId, expectedSeats);
    }

    @Test
    void validatorShouldBeCalledWithExactArguments() {
        Long accountId = 99L;

        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        doNothing().when(validator).validate(accountId, adult, child);

        service.purchaseTickets(accountId, adult, child);

        verify(validator, times(1)).validate(accountId, adult, child);
    }
}
