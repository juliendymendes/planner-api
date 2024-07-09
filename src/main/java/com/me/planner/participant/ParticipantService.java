package com.me.planner.participant;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.me.planner.trip.Trip;

@Service
public class ParticipantService {

	@Autowired
	private ParticipantRepository participantRepository;

	public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip){
		List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();
		
		this.participantRepository.saveAll(participants);
		System.out.println(participants.get(0).getId());
	}

	public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip){
		Participant newParticipant = new Participant(email, trip);
		this.participantRepository.save(newParticipant);
		return new ParticipantCreateResponse(newParticipant.getId());
	}

	public void triggerConfirmationEmailToParticipants(UUID tripId){}

	public void triggerConfirmationEmailToParticipant(String email) {}

	public List<ParticipantData> getAllParticipantsFromEvent(UUID id) {
	return this.participantRepository.findByTripId(id).stream().map(participant-> new ParticipantData(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed())).toList();
	}

}
