package uk.gov.dwp.uc.pairtest.calculators;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SeatReservationCalculatorTest {

    private final SeatReservationCalculator calculator =
            SeatReservationCalculator.SEAT_RESERVATION_CALCULATOR_INSTANCE;


    @Test
    void shouldCountSeatsForAdultsOnly() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);

        int seats = calculator.getTotalNumberOfSeatsWithoutInfant(adult);

        assertEquals(3, seats);
    }

    @Test
    void shouldCountSeatsForAdultsAndChildren() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);

        int seats = calculator.getTotalNumberOfSeatsWithoutInfant(adult, child);

        assertEquals(6, seats);
    }


    @Test
    void shouldNotCountInfantsInSeatTotal() {
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        int seats = calculator.getTotalNumberOfSeatsWithoutInfant(infant);

        assertEquals(0, seats);
    }

    @Test
    void shouldIgnoreInfantsWhenMixedWithAdultsAndChildren() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);

        int seats = calculator.getTotalNumberOfSeatsWithoutInfant(adult, child, infant);

        assertEquals(3, seats); // 1 adult + 2 child
    }


    @Test
    void shouldReturnZeroWhenNoRequestsProvided() {
        int seats = calculator.getTotalNumberOfSeatsWithoutInfant();

        assertEquals(0, seats);
    }

    @Test
    void shouldHandleMultipleEntriesOfSameType() {
        TicketTypeRequest adult1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest adult2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        int seats = calculator.getTotalNumberOfSeatsWithoutInfant(adult1, adult2);

        assertEquals(3, seats);
    }

    @Test
    void shouldAlwaysReturnSameSingletonInstance() {
        SeatReservationCalculator instance1 = SeatReservationCalculator.SEAT_RESERVATION_CALCULATOR_INSTANCE;
        SeatReservationCalculator instance2 = SeatReservationCalculator.SEAT_RESERVATION_CALCULATOR_INSTANCE;

        assertSame(instance1, instance2);
    }
}
