package com.tutorneo.core.ports.out;

public interface TournamentCleanupPort {

    //Este puerto de salida solo tiene un metodo para eliminar los equipos e inscripciones de un torneo.
    //Quien lo implemente que va a ser el adaptador de salida correspondiente, debe encargarse de realizar la limpieza. 
    void removeTeamsAndRegistrations(Long tournamentId);



    
}
