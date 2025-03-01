package com.me.planner.participant;

import java.util.UUID;

import com.me.planner.trip.Trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

	public Participant(String email, Trip trip){
		this.email = email;
		this.trip = trip;
		this.isConfirmed = false;
		this.name = "";
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "is_confirmed", nullable = false)
	private Boolean isConfirmed;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@ManyToOne
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;
}
