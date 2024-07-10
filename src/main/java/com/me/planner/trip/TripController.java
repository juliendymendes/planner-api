package com.me.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.planner.activity.ActivityData;
import com.me.planner.activity.ActivityRequestPayload;
import com.me.planner.activity.ActivityResponse;
import com.me.planner.activity.ActivityService;
import com.me.planner.link.LinkData;
import com.me.planner.link.LinkRequestPayload;
import com.me.planner.link.LinkResponse;
import com.me.planner.link.LinkService;
import com.me.planner.participant.ParticipantCreateResponse;
import com.me.planner.participant.ParticipantData;
import com.me.planner.participant.ParticipantRequestPayload;
import com.me.planner.participant.ParticipantService;

@RestController
@RequestMapping("/trips")
public class TripController {
	@Autowired
	private ParticipantService participantService;

	@Autowired
	private TripRepository tripRepository;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private LinkService linkService;

	@PostMapping
	public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){
		Trip newTrip = new Trip(payload);

		this.tripRepository.save(newTrip);

		this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);
		return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
		Optional<Trip> trip = this.tripRepository.findById(id);
		return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
		Optional<Trip> trip = this.tripRepository.findById(id);
		if(trip.isPresent()){
			Trip rawTrip = trip.get();
			rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
			rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
			rawTrip.setDestination(payload.destination());
			this.tripRepository.save(rawTrip);
			return ResponseEntity.ok(rawTrip);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/confirm")
	public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
		Optional<Trip> trip = this.tripRepository.findById(id);
		if(trip.isPresent()){
			Trip rawTrip = trip.get();
			rawTrip.setIsConfirmed(true);
			this.tripRepository.save(rawTrip);
			this.participantService.triggerConfirmationEmailToParticipants(id);
			return ResponseEntity.ok(rawTrip);
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/{id}/activities")
	public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload){
		Optional<Trip> trip = this.tripRepository.findById(id);
		if(trip.isPresent()){
			Trip rawTrip = trip.get();

			ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

			return ResponseEntity.ok(activityResponse);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/activities")
	public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
		List<ActivityData> activitiesList = this.activityService.getAllActivitiesFromId(id);

		return ResponseEntity.ok(activitiesList);
	}

	@PostMapping("/{id}/invite")
	public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id,@RequestBody ParticipantRequestPayload payload){
		Optional<Trip> trip = this.tripRepository.findById(id);
		if(trip.isPresent()){
			Trip rawTrip = trip.get();

			ParticipantCreateResponse response = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);

			if(rawTrip.getIsConfirmed()){
				this.participantService.triggerConfirmationEmailToParticipant(payload.email());
			}
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/participants")
	public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
		List<ParticipantData> participantsList = this.participantService.getAllParticipantsFromEvent(id);

		return ResponseEntity.ok(participantsList);
	}

	@PostMapping("/{id}/links")
	public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
		Optional<Trip> trip = this.tripRepository.findById(id);
		if(trip.isPresent()){
			Trip rawTrip = trip.get();

			LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

			return ResponseEntity.ok(linkResponse);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/links")
	public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
		List<LinkData> linksList = this.linkService.getAllLinksFromTrip(id);

		return ResponseEntity.ok(linksList);
	}

}
