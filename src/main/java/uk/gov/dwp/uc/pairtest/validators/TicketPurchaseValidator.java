package uk.gov.dwp.uc.pairtest.validators;

import uk.gov.dwp.uc.pairtest.configs.ConfigurationProvider;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

/**
 * Encapsulates all business rule validation for ticket purchases.
 * Single Responsibility: validation only.
 */
public class TicketPurchaseValidator {

    private static final int MAX_TICKETS = ConfigurationProvider.MAX_TICKETS;

    public void validate(Long accountId, TicketTypeRequest... ticketTypeRequests) {
        validateAccountId(accountId);
        validateRequestNotEmpty(ticketTypeRequests);
        validateNoNullRequests(ticketTypeRequests);
        validateNoNegativeTicketCounts(ticketTypeRequests);
        validateTotalTicketCount(ticketTypeRequests);
        validateAdultPresence(ticketTypeRequests);
        validateAdultsNotLessThanInfant(ticketTypeRequests);
    }

    private void validateAccountId(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be greater than zero.");
        }
    }

    private void validateRequestNotEmpty(TicketTypeRequest... requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException("At least one ticket type request must be provided.");
        }
    }

    private void validateNoNullRequests(TicketTypeRequest... requests) {
        if (Arrays.stream(requests).anyMatch(r -> r == null)) {
            throw new InvalidPurchaseException("Ticket type requests must not contain null entries.");
        }
    }

    private void validateNoNegativeTicketCounts(TicketTypeRequest... requests) {
        if (Arrays.stream(requests).anyMatch(r -> r.getNoOfTickets() <= 0)) {
            throw new InvalidPurchaseException("Each individual ticket type request must specify at least one ticket.");
        }
    }

    private void validateTotalTicketCount(TicketTypeRequest... requests) {
        int totalNoOfTickets = Arrays.stream(requests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
        if (totalNoOfTickets > MAX_TICKETS) {
            throw new InvalidPurchaseException(
                    "Cannot purchase more than " + MAX_TICKETS + " tickets at a time. Requested: " + totalNoOfTickets);
        }
    }

    private void validateAdultPresence(TicketTypeRequest... requests) {
        long adultCount = Arrays.stream(requests)
                .filter(r -> r.getTicketType() == TicketTypeRequest.Type.ADULT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();

        if (adultCount == 0) {
            throw new InvalidPurchaseException("Child and Infant tickets cannot be purchased without an Adult ticket.");
        }
    }
    private void validateAdultsNotLessThanInfant(TicketTypeRequest... requests) {
        long adultCount = Arrays.stream(requests)
                .filter(r -> r.getTicketType() == TicketTypeRequest.Type.ADULT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();

        long infantCount = Arrays.stream(requests)
                .filter(r -> r.getTicketType() == TicketTypeRequest.Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();

        if (adultCount < infantCount) {
            throw new InvalidPurchaseException("Number of adults must not be less than number of infants. Tickets cannot be purchased without an Adult ticket.");
        }
    }
}