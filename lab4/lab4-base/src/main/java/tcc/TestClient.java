package tcc;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import tcc.flight.FlightReservationDoc;
import tcc.hotel.HotelReservationDoc;

/**
 * Simple non-transactional client. Can be used to populate the booking services
 * with some requests.
 */
public class TestClient {
	public static void main(String[] args) {
		//BaseTest();
		TransactionTest();
	}

	private static void TransactionTest() {
		for (int i = 0; i < 15; i++){
			try {
				Client client = ClientBuilder.newClient();
				WebTarget target = client.target(TestServer.BASE_URI);

				GregorianCalendar tomorrow = new GregorianCalendar();
				tomorrow.setTime(new Date());
				tomorrow.add(GregorianCalendar.DAY_OF_YEAR, 1);

				// book flight

				WebTarget webTargetFlight = target.path("flight");

				FlightReservationDoc docFlight = new FlightReservationDoc();
				docFlight.setName("Christian");
				docFlight.setFrom("Karlsruhe");
				docFlight.setTo("Berlin");
				docFlight.setAirline("airberlin");
				docFlight.setDate(tomorrow.getTimeInMillis());

				Response responseFlight = webTargetFlight.request().accept(MediaType.APPLICATION_XML).post(Entity.xml(docFlight));

				boolean flightSuccess = true;
				if (responseFlight.getStatus() != 200) {
					flightSuccess = false;
					System.out.println("Failed : HTTP error code : " + responseFlight.getStatus());
				}

				FlightReservationDoc outputFlight = responseFlight.readEntity(FlightReservationDoc.class);
				System.out.println("Output from Server: " + outputFlight);

				// book hotel

				WebTarget webTargetHotel = target.path("hotel");

				HotelReservationDoc docHotel = new HotelReservationDoc();
				docHotel.setName("Christian");
				docHotel.setHotel("Interconti");
				docHotel.setDate(tomorrow.getTimeInMillis());

				Response responseHotel = webTargetHotel.request().accept(MediaType.APPLICATION_XML)
						.post(Entity.xml(docHotel));

				boolean hotelSuccess = true;
				if (responseHotel.getStatus() != 200) {
					hotelSuccess = false;
					System.out.println("Failed : HTTP error code : " + responseHotel.getStatus());
				}

				HotelReservationDoc outputHotel = responseHotel.readEntity(HotelReservationDoc.class);
				System.out.println("Output from Server: " + outputHotel);


				//TODO Aufgabe 4

				// Check if hotel booking is still valid
				if(hotelSuccess){
					WebTarget hotelCheckTarget = client.target(outputHotel.getUrl());
					Response hotelCheckResponse = hotelCheckTarget.request().accept(MediaType.APPLICATION_XML).get();
					if (hotelCheckResponse.getStatus() != 200) {
						hotelSuccess = false;
					}
				}

				// Check if flight booking is still valid
				if(flightSuccess){
					WebTarget flightCheckTarget = client.target(outputFlight.getUrl());
					Response flightCheckResponse = flightCheckTarget.request().accept(MediaType.APPLICATION_XML).get();
					if (flightCheckResponse.getStatus() != 200) {
						flightSuccess = false;
					}
				}

				//TODO If One or both unsucessful => cancel both
				if(!flightSuccess && hotelSuccess){
					//cancel hotel
					WebTarget hotelCancelTarget = client.target(outputHotel.getUrl());
					hotelCancelTarget.request().accept(MediaType.APPLICATION_XML).delete();
					System.out.println("Hotel booking was canceled since hotel flight failed");
					continue;
				}

				if(!hotelSuccess && flightSuccess){
					//cancel flight
					WebTarget flightCancelTarget = client.target(outputFlight.getUrl());
					flightCancelTarget.request().accept(MediaType.APPLICATION_XML).delete();
					System.out.println("Flight booking was canceled since hotel booking failed");
					continue;
				}

				// Confirm hotel
				WebTarget hotelConfirmTarget = client.target(outputHotel.getUrl());
				Response hotelConfirmResponse = hotelConfirmTarget.request().put(Entity.text(""));
				System.out.println("Hotel Confirmed Status = " + hotelConfirmResponse.getStatus());

				// Confirm Flight
				WebTarget flightConfirmTarget = client.target(outputFlight.getUrl());
				Response flightConfirmResponse = flightConfirmTarget.request().put(Entity.text(""));
				System.out.println("Flight Confirmed Status = " + flightConfirmResponse.getStatus());

				System.out.println("Transaction successful");

				// Confirm hotel
				WebTarget hotelConfirmCheckTarget = client.target(outputHotel.getUrl());
				Response hotelConfirmCheckResponse = hotelConfirmCheckTarget.request().accept(MediaType.APPLICATION_XML).get();
				HotelReservationDoc finalHotel = hotelConfirmCheckResponse.readEntity(HotelReservationDoc.class);
				System.out.println("Hotel Confirmed = " + finalHotel.getConfirmed());

				// Confirm Flight
				WebTarget flightConfirmCheckTarget = client.target(outputFlight.getUrl());
				Response flightConfirmCheckResponse = flightConfirmCheckTarget.request().accept(MediaType.APPLICATION_XML).get();
				FlightReservationDoc finalFlight = flightConfirmCheckResponse.readEntity(FlightReservationDoc.class);
				System.out.println("Flight Confirmed = " + finalFlight.getConfirmed());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void BaseTest(){
		for (int i = 0; i < 10; i++){
			try {
				System.out.println("Start Iteration: " + i);
				Client client = ClientBuilder.newClient();
				WebTarget target = client.target(TestServer.BASE_URI);

				GregorianCalendar tomorrow = new GregorianCalendar();
				tomorrow.setTime(new Date());
				tomorrow.add(GregorianCalendar.DAY_OF_YEAR, 1);

				// book flight

				WebTarget webTargetFlight = target.path("flight");

				FlightReservationDoc docFlight = new FlightReservationDoc();
				docFlight.setName("Christian");
				docFlight.setFrom("Karlsruhe");
				docFlight.setTo("Berlin");
				docFlight.setAirline("airberlin");
				docFlight.setDate(tomorrow.getTimeInMillis());

				Response responseFlight = webTargetFlight.request().accept(MediaType.APPLICATION_XML).post(Entity.xml(docFlight));

				if (responseFlight.getStatus() != 200) {
					System.out.println("Failed : HTTP error code : " + responseFlight.getStatus());
				}

				FlightReservationDoc outputFlight = responseFlight.readEntity(FlightReservationDoc.class);
				System.out.println("Output from Server: " + outputFlight);

				// book hotel

				WebTarget webTargetHotel = target.path("hotel");

				HotelReservationDoc docHotel = new HotelReservationDoc();
				docHotel.setName("Christian");
				docHotel.setHotel("Interconti");
				docHotel.setDate(tomorrow.getTimeInMillis());

				Response responseHotel = webTargetHotel.request().accept(MediaType.APPLICATION_XML)
						.post(Entity.xml(docHotel));

				if (responseHotel.getStatus() != 200) {
					System.out.println("Failed : HTTP error code : " + responseHotel.getStatus());
				}

				HotelReservationDoc outputHotel = responseHotel.readEntity(HotelReservationDoc.class);
				System.out.println("Output from Server: " + outputHotel);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
