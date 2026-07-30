package com.tutorneo.core.ports.in;

//import com.tutorneo.domain.models.Tournament;
import com.tutorneo.core.domain.models.Tournament;

public interface CreateTournamentPort {
    Tournament create(Tournament toCreate, Long organizerId);
}
