package uk.gov.dwp.uc.pairtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import static uk.gov.dwp.uc.pairtest.calculators.TicketPriceCalculator.TICKET_PRICE_CALCULATOR_INSTANCE;
import static uk.gov.dwp.uc.pairtest.calculators.SeatReservationCalculator.SEAT_RESERVATION_CALCULATOR_INSTANCE;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.validators.TicketPurchaseValidator;

public class TicketServiceImpl implements TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketServiceImpl.class);
    private final TicketPurchaseValidator validator;
    private final TicketPaymentService paymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService paymentService,
                             SeatReservationService seatReservationService) {
        this(new TicketPurchaseValidator(),
                paymentService,
                seatReservationService);
    }

    TicketServiceImpl(TicketPurchaseValidator validator,
                      TicketPaymentService paymentService,
                      SeatReservationService seatReservationService) {
        this.validator = validator;
        this.paymentService = paymentService;
        this.seatReservationService = seatReservationService;
    }
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
            throws InvalidPurchaseException {

        validator.validate(accountId, ticketTypeRequests);

        int totalAmount = TICKET_PRICE_CALCULATOR_INSTANCE.calculateTotalAmount(ticketTypeRequests);
        int totalSeats  = SEAT_RESERVATION_CALCULATOR_INSTANCE.getTotalNumberOfSeatsWithoutInfant(ticketTypeRequests);

        if(totalAmount>0){
            paymentService.makePayment(accountId, totalAmount);
            log.info("Payment is made for GBP:{}", totalAmount);
        } else {
            log.info("Nothing to pay for this booking.");
        }

        if(totalSeats > 0){
            seatReservationService.reserveSeat(accountId, totalSeats);
            log.info("{} seats have been booked for this purchase.", totalSeats);
        } else {
            log.info("No seat is booked for this purchase.");
        }

        log.info("Ticket purchase completed.");
    }

}