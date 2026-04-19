package uk.gov.dwp.uc.pairtest.calculators;

import uk.gov.dwp.uc.pairtest.configs.ConfigurationProvider;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.Arrays;

public final class TicketPriceCalculator {
    public static final TicketPriceCalculator TICKET_PRICE_CALCULATOR_INSTANCE = new TicketPriceCalculator();

    private TicketPriceCalculator() {}

    public int calculateTotalAmount(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .mapToInt(this::priceFor)
                .sum();
    }

    private int priceFor(TicketTypeRequest ticketTypeRequest) {
        return switch (ticketTypeRequest.getTicketType()) {
            case ADULT  -> ticketTypeRequest.getNoOfTickets() * ConfigurationProvider.ADULT_PRICE;
            case CHILD  -> ticketTypeRequest.getNoOfTickets() * ConfigurationProvider.CHILD_PRICE;
            case INFANT -> ConfigurationProvider.INFANT_PRICE;
        };
    }
}