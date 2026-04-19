package uk.gov.dwp.uc.pairtest.calculators;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Arrays;

public final class SeatReservationCalculator {

    public static final SeatReservationCalculator SEAT_RESERVATION_CALCULATOR_INSTANCE = new SeatReservationCalculator();

    private SeatReservationCalculator() {}

    public int getTotalNumberOfSeatsWithoutInfant(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() != TicketTypeRequest.Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }
}
