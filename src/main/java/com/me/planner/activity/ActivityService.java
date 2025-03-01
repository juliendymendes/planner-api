package com.me.planner.activity;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.me.planner.trip.Trip;

@Service
public class ActivityService {

	@Autowired
	private ActivityRepository activityRepository;

	public ActivityResponse registerActivity(ActivityRequestPayload payload, Trip trip){
		Activity newActivity = new Activity(payload.title(), payload.occurs_at(), trip);
		this.activityRepository.save(newActivity);
		return new ActivityResponse(newActivity.getId());
	}

	public List<ActivityData> getAllActivitiesFromId(UUID tripId){
		return this.activityRepository.findByTripId(tripId).stream().map(activity -> new ActivityData(activity.getId(), activity.getTitle(), activity.getOccoursAt())).toList();
	}
}
