package uk.gov.dwp.uc.pairtest.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketPurchaseValidatorTest {

    private TicketPurchaseValidator validator;

    @BeforeEach
    void setup() {
        validator = new TicketPurchaseValidator();
    }


    @Test
    void shouldRejectNullAccountId() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(null, adult));
    }

    @Test
    void shouldRejectZeroAccountId() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(0L, adult));
    }

    @Test
    void shouldRejectNegativeAccountId() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(-5L, adult));
    }


    @Test
    void shouldRejectNullRequestArray() {
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, (TicketTypeRequest[]) null));
    }

    @Test
    void shouldRejectEmptyRequestArray() {
        TicketTypeRequest[] TicketTypeRequest = {};
        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L,TicketTypeRequest));
    }


    @Test
    void shouldRejectNullEntryInsideRequests() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, adult, null));
    }


    @Test
    void shouldRejectZeroTicketCount() {
        TicketTypeRequest invalid = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, invalid));
    }

    @Test
    void shouldRejectNegativeTicketCount() {
        TicketTypeRequest invalid = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -3);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, invalid));
    }


    @Test
    void shouldRejectMoreThanMaxTickets() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 30);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, adult));
    }

    @Test
    void shouldAllowExactlyMaxTickets() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25);

        assertDoesNotThrow(() -> validator.validate(1L, adult));
    }


    @Test
    void shouldRejectChildWithoutAdult() {
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, child));
    }

    @Test
    void shouldRejectInfantWithoutAdult() {
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, infant));
    }

    @Test
    void shouldRejectChildAndInfantWithoutAdult() {
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, child, infant));
    }

    @Test
    void shouldAllowAdultWithChild() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);

        assertDoesNotThrow(() -> validator.validate(1L, adult, child));
    }

    @Test
    void shouldAllowAdultWithInfant() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertDoesNotThrow(() -> validator.validate(1L, adult, infant));
    }

    @Test
    void shouldAllowAdultWithChildAndInfant() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        assertDoesNotThrow(() -> validator.validate(1L, adult, child, infant));
    }


    @Test
    void shouldAllowSingleAdult() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        assertDoesNotThrow(() -> validator.validate(1L, adult));
    }

    @Test
    void shouldAllowMultipleAdults() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);

        assertDoesNotThrow(() -> validator.validate(1L, adult));
    }


    @Test
    void shouldRejectInfantMoreThanAdults() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 6);

        assertThrows(InvalidPurchaseException.class,
                () -> validator.validate(1L, adult, infant));
    }
}
