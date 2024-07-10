package com.me.planner.link;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.me.planner.trip.Trip;

@Service
public class LinkService {
	@Autowired
	private LinkRepository linkRepository;

	public LinkResponse registerLink(LinkRequestPayload payload, Trip trip){
		Link newLink = new Link(payload.title(), payload.url(), trip);
		this.linkRepository.save(newLink);
		return new LinkResponse(newLink.getId());
	}

	public List<LinkData> getAllLinksFromTrip(UUID tripId){
		return this.linkRepository.findByTripId(tripId).stream().map(link -> new LinkData(tripId, link.getTitle(), link.getUrl())).toList();
	}
}
