package uk.gov.dwp.uc.pairtest.calculators;

import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.configs.ConfigurationProvider;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketPriceCalculatorTest {

    private final TicketPriceCalculator calculator =
            TicketPriceCalculator.TICKET_PRICE_CALCULATOR_INSTANCE;


    @Test
    void shouldCalculatePriceForAdultsOnly() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3);

        int total = calculator.calculateTotalAmount(adult);

        assertEquals(3 * ConfigurationProvider.ADULT_PRICE, total);
    }

    @Test
    void shouldCalculatePriceForChildrenOnly() {
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 4);

        int total = calculator.calculateTotalAmount(child);

        assertEquals(4 * ConfigurationProvider.CHILD_PRICE, total);
    }

    @Test
    void shouldCalculatePriceForInfantsOnly() {
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 5);

        int total = calculator.calculateTotalAmount(infant);

        assertEquals(5 * ConfigurationProvider.INFANT_PRICE, total);
    }


    @Test
    void shouldCalculatePriceForMixedTickets() {
        TicketTypeRequest adult = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);
        TicketTypeRequest child = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3);
        TicketTypeRequest infant = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1);

        int expected =
                (2 * ConfigurationProvider.ADULT_PRICE) +
                        (3 * ConfigurationProvider.CHILD_PRICE) +
                        (1 * ConfigurationProvider.INFANT_PRICE);

        int total = calculator.calculateTotalAmount(adult, child, infant);

        assertEquals(expected, total);
    }


    @Test
    void shouldReturnZeroWhenNoRequestsProvided() {
        int total = calculator.calculateTotalAmount();

        assertEquals(0, total);
    }

    @Test
    void shouldHandleMultipleEntriesOfSameType() {
        TicketTypeRequest adult1 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);
        TicketTypeRequest adult2 = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        int expected = (1 + 2) * ConfigurationProvider.ADULT_PRICE;

        int total = calculator.calculateTotalAmount(adult1, adult2);

        assertEquals(expected, total);
    }


    @Test
    void shouldAlwaysReturnSameSingletonInstance() {
        TicketPriceCalculator instance1 = TicketPriceCalculator.TICKET_PRICE_CALCULATOR_INSTANCE;
        TicketPriceCalculator instance2 = TicketPriceCalculator.TICKET_PRICE_CALCULATOR_INSTANCE;

        assertSame(instance1, instance2);
    }
}
