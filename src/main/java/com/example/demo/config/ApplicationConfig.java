package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.adapters.out.persistence.jpa.mappers.UserMapper;
import com.example.demo.core.application.usecase.ChangePasswordUseCase;
import com.example.demo.core.application.usecase.CreateTournamentUseCase;
import com.example.demo.core.application.usecase.GetAllTournamentsUseCase;
import com.example.demo.core.application.usecase.GetTournamentByIdUseCase;
import com.example.demo.core.application.usecase.GetLatestTournamentsUseCase;
import com.example.demo.core.application.usecase.GetTournamentUseCase;
import com.example.demo.core.application.usecase.GetUserByIdAndEmailUseCase;
import com.example.demo.core.application.usecase.GetUserByIdUseCase;
import com.example.demo.core.application.usecase.ListDisciplinesUseCase;
import com.example.demo.core.application.usecase.ListFormatsByDisciplineUseCase;
import com.example.demo.core.application.usecase.ListPublicTournamentsUseCase;
import com.example.demo.core.application.usecase.ListTournamentsByStatusUseCase;
import com.example.demo.core.application.usecase.ListUsersAdminUseCase;
import com.example.demo.core.application.usecase.ListUsersUseCase;
import com.example.demo.core.application.usecase.GenerateEliminationFixtureUseCase;
import com.example.demo.core.application.usecase.GenerateLeagueFixtureUseCase;
import com.example.demo.core.application.usecase.RegisterRunnerToTournamentUseCase;
import com.example.demo.core.application.usecase.GetRaceResultsUseCase;
import com.example.demo.core.application.usecase.ReportRaceResultsUseCase;
import com.example.demo.core.application.usecase.DeactivateUserUseCase;
import com.example.demo.core.application.usecase.RestoreUserUseCase;
import com.example.demo.core.application.usecase.ToOrganizerUseCase;
import com.example.demo.core.application.usecase.RegisterTeamToTournamentUseCase;
import com.example.demo.core.application.usecase.RegisterToTournamentUseCase;
import com.example.demo.core.application.usecase.RegisterUserUseCase;
import com.example.demo.core.application.usecase.ReportLeagueMatchResultUseCase;
import com.example.demo.core.application.usecase.ReportMatchResultUseCase;
import com.example.demo.core.application.usecase.GetLeagueStandingsUseCase;
import com.example.demo.core.application.usecase.UpdateUserUseCase;
import com.example.demo.core.application.usecase.CancelTournamentUseCase;
import com.example.demo.core.application.usecase.StartTournamentUseCase;
import com.example.demo.core.application.usecase.FinalizeTournamentUseCase;
import com.example.demo.core.application.usecase.RemoveTeamFromTournamentUseCase;
import com.example.demo.core.application.usecase.GetUserNotificationsUseCase;
import com.example.demo.core.application.usecase.MarkNotificationAsReadUseCase;
import com.example.demo.core.application.usecase.CreateNotificationUseCase;
import com.example.demo.core.application.usecase.RateOrganizerUseCase;
import com.example.demo.core.application.usecase.GetOrganizerReputationUseCase;
import com.example.demo.core.application.usecase.UpdateTournamentUseCase;
import com.example.demo.core.application.usecase.CreatePostUseCase;
import com.example.demo.core.application.usecase.GetPostsUseCase;
import com.example.demo.core.application.usecase.ClosePostUseCase;
import com.example.demo.core.application.usecase.CreateComentarioUseCase;
import com.example.demo.core.application.usecase.GetComentariosUseCase;
import com.example.demo.core.application.usecase.ContactarAvisoUseCase;
import com.example.demo.core.application.usecase.GetContactosUseCase;
import com.example.demo.core.application.service.NotificationSseService;
import com.example.demo.core.ports.in.ChangePasswordPort;
import com.example.demo.core.ports.in.CreateTournamentPort;
import com.example.demo.core.ports.in.GetAllTournamentsPort;
import com.example.demo.core.ports.in.RemoveTeamFromTournamentPort;
import com.example.demo.core.ports.in.GetUserNotificationsPort;
import com.example.demo.core.ports.in.MarkNotificationAsReadPort;
import com.example.demo.core.ports.in.CreateNotificationPort;
import com.example.demo.core.ports.in.GetTournamentByIdPort;
import com.example.demo.core.ports.in.GetLatestTournamentsPort;
import com.example.demo.core.ports.in.GetTournamentPort;
import com.example.demo.core.ports.in.GetUserByIdAndEmailPort;
import com.example.demo.core.ports.in.GetUserByIdPort;
import com.example.demo.core.ports.in.ListDisciplinesPort;
import com.example.demo.core.ports.in.ListFormatsByDisciplinePort;
import com.example.demo.core.ports.in.ListUsersAdminPort;
import com.example.demo.core.ports.in.ListPublicTournamentsPort;
import com.example.demo.core.ports.in.ListTournamentsByStatusPort;
import com.example.demo.core.ports.in.ListUsersPort;
import com.example.demo.core.ports.in.DeactivateUserPort;
import com.example.demo.core.ports.in.RestoreUserPort;
import com.example.demo.core.ports.in.RegisterRunnerToTournamentPort;
import com.example.demo.core.ports.in.GenerateLeagueFixturePort;
import com.example.demo.core.ports.in.GetRaceResultsPort;
import com.example.demo.core.ports.in.RegisterTeamToTournamentPort;
import com.example.demo.core.ports.in.RegisterToTournamentPort;
import com.example.demo.core.ports.in.RegisterUserPort;
import com.example.demo.core.ports.in.ReportLeagueMatchResultPort;
import com.example.demo.core.ports.in.ReportMatchResultPort;
import com.example.demo.core.ports.in.ReportRaceResultsPort;
import com.example.demo.core.ports.in.ToOrganizerPort;
import com.example.demo.core.ports.in.GetLeagueStandingsPort;
import com.example.demo.core.ports.in.UpdateProfilePort;
import com.example.demo.core.ports.in.CancelTournamentPort;
import com.example.demo.core.ports.in.StartTournamentPort;
import com.example.demo.core.ports.in.FinalizeTournamentPort;
import com.example.demo.core.ports.in.RateOrganizerPort;
import com.example.demo.core.ports.in.GetOrganizerReputationPort;
import com.example.demo.core.ports.in.UpdateTournamentPort;
import com.example.demo.core.ports.in.CreatePostPort;
import com.example.demo.core.ports.in.GetPostsPort;
import com.example.demo.core.ports.in.ClosePostPort;
import com.example.demo.core.ports.in.CreateComentarioPort;
import com.example.demo.core.ports.in.GetComentariosPort;
import com.example.demo.core.ports.in.ContactarAvisoPort;
import com.example.demo.core.ports.in.GetContactosPort;
import com.example.demo.core.ports.out.DisciplineRepositoryPort;
import com.example.demo.core.ports.out.FindTournamentsByStatusPort;
import com.example.demo.core.ports.out.FindTournamentsPort;
import com.example.demo.core.ports.out.FormatRepositoryPort;
import com.example.demo.core.ports.out.FixturePersistencePort;
import com.example.demo.core.ports.out.TeamQueryPort;
import com.example.demo.core.ports.out.RaceResultPersistencePort;
import com.example.demo.core.ports.out.TeamRegistrationPort;
import com.example.demo.core.ports.out.TeamRemovalPort;
import com.example.demo.core.ports.out.NotificationPort;
import com.example.demo.core.ports.out.TournamentRegistrationPort;
import com.example.demo.core.ports.out.TournamentRepositoryPort;
import com.example.demo.core.ports.out.UserRepositoryPort;
import com.example.demo.core.ports.out.UserAdminReadPort;
import com.example.demo.core.ports.out.TournamentCleanupPort;
import com.example.demo.core.ports.out.ReputationRepositoryPort;
import com.example.demo.core.ports.out.PostRepositoryPort;
import com.example.demo.core.ports.out.ComentarioRepositoryPort;
import com.example.demo.core.ports.out.ContactoReveladoRepositoryPort;

@Configuration
public class ApplicationConfig {

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public ListUsersPort listUsersPort(UserRepositoryPort userRepositoryPort) {
        return new ListUsersUseCase(userRepositoryPort);
    }

    @Bean
    public ListUsersAdminPort listUsersAdminPort(UserAdminReadPort userAdminReadPort) {
        return new ListUsersAdminUseCase(userAdminReadPort);
    }

    @Bean
    public DeactivateUserPort deactivateUserPort(UserRepositoryPort userRepositoryPort) {
        return new DeactivateUserUseCase(userRepositoryPort);
    }

    @Bean
    public RestoreUserPort restoreUserPort(UserRepositoryPort userRepositoryPort) {
        return new RestoreUserUseCase(userRepositoryPort);
    }

    @Bean
    public RegisterUserPort registerUserPort(UserRepositoryPort userRepositoryPort, CreateNotificationPort createNotificationPort) {
        return new RegisterUserUseCase(userRepositoryPort, createNotificationPort);
    }

    @Bean
    public UpdateProfilePort UpdateProfilePort(UserRepositoryPort userRepositoryPort) {
        return new UpdateUserUseCase(userRepositoryPort);
    }

    @Bean
    public GetUserByIdPort GetUserPort(UserRepositoryPort userRepositoryPort) {
        return new GetUserByIdUseCase(userRepositoryPort);
    }

    @Bean
    public CreateTournamentPort CreateTournamentPort(TournamentRepositoryPort tournamentRepositoryPort) {
        return new CreateTournamentUseCase(tournamentRepositoryPort);
    }

    @Bean
    public ListPublicTournamentsPort listPublicTournamentsPort(FindTournamentsPort findTournamentsPort) {
        return new ListPublicTournamentsUseCase(findTournamentsPort);
    }

    @Bean
    public ListTournamentsByStatusPort listTournamentsByStatusPort(
            FindTournamentsByStatusPort findTournamentsByStatusPort) {
        return new ListTournamentsByStatusUseCase(findTournamentsByStatusPort);
    }

    @Bean
    public ListDisciplinesPort ListDisciplinesPort(DisciplineRepositoryPort disciplineRepositoryPort) {
        return new ListDisciplinesUseCase(disciplineRepositoryPort);
    }

    @Bean
    public ListFormatsByDisciplinePort ListFormatsByDisciplinePort(FormatRepositoryPort formatRepositoryPort) {
        return new ListFormatsByDisciplineUseCase(formatRepositoryPort);
    }

    @Bean
    public GetTournamentPort GetTournamentPort(TournamentRepositoryPort repo) {
        return new GetTournamentUseCase(repo);
    }

    @Bean
    public GetAllTournamentsPort GetAllTournamentsPort(TournamentRepositoryPort repo) {
        return new GetAllTournamentsUseCase(repo);
    }

    @Bean
    public GetTournamentByIdPort getTournamentById(TournamentRepositoryPort tournamentRepositoryPort) {
        return new GetTournamentByIdUseCase(tournamentRepositoryPort);
    }

    @Bean
    public GetLatestTournamentsPort getLatestTournamentsPort(TournamentRepositoryPort tournamentRepositoryPort) {
        return new GetLatestTournamentsUseCase(tournamentRepositoryPort);
    }

    @Bean
    public RegisterToTournamentPort RegisterToTournamentPort(TournamentRepositoryPort tournamentRepositoryPort,
            TournamentRegistrationPort tournamentRegistrationPort,
            NotificationPort notificationPort,
            CreateNotificationPort createNotificationPort) {
        return new RegisterToTournamentUseCase(tournamentRepositoryPort, tournamentRegistrationPort, notificationPort, createNotificationPort);
    }

    @Bean
    public RegisterTeamToTournamentPort RegisterTeamToTournamentPort(TournamentRepositoryPort tournamentRepositoryPort,
            TeamRegistrationPort teamRegistrationPort,
            NotificationPort notificationPort,
            CreateNotificationPort createNotificationPort) {
        return new RegisterTeamToTournamentUseCase(tournamentRepositoryPort, teamRegistrationPort, notificationPort, createNotificationPort);
    }

    @Bean
    public RegisterRunnerToTournamentPort registerRunnerToTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            TeamRegistrationPort teamRegistrationPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort,
            CreateNotificationPort createNotificationPort) {
        return new RegisterRunnerToTournamentUseCase(tournamentRepositoryPort, teamRegistrationPort,
                userRepositoryPort, notificationPort, createNotificationPort);
    }

    @Bean
    public GenerateEliminationFixtureUseCase generateEliminationFixtureUseCase(
            TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            NotificationPort notificationPort) {
        return new GenerateEliminationFixtureUseCase(tournamentRepositoryPort, fixturePersistencePort, notificationPort);
    }

    @Bean
    public GenerateLeagueFixturePort generateLeagueFixturePort(
            TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            NotificationPort notificationPort) {
        return new GenerateLeagueFixtureUseCase(tournamentRepositoryPort, fixturePersistencePort, notificationPort);
    }

    @Bean
    public ReportMatchResultPort ReportMatchResultPort(FixturePersistencePort fixturePersistencePort,
            TournamentRepositoryPort tournamentRepositoryPort,
            NotificationPort notificationPort) {
        return new ReportMatchResultUseCase(fixturePersistencePort, tournamentRepositoryPort, notificationPort);
    }

    @Bean
    public ReportLeagueMatchResultPort reportLeagueMatchResultPort(FixturePersistencePort fixturePersistencePort,
            TournamentRepositoryPort tournamentRepositoryPort,
            NotificationPort notificationPort) {
        return new ReportLeagueMatchResultUseCase(fixturePersistencePort, tournamentRepositoryPort, notificationPort);
    }

    @Bean
    public GetLeagueStandingsPort getLeagueStandingsPort(TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            TeamQueryPort teamQueryPort) {
        return new GetLeagueStandingsUseCase(tournamentRepositoryPort, fixturePersistencePort, teamQueryPort);
    }

    @Bean
    public GetUserByIdAndEmailPort getUserByIdAndEmailPort(UserRepositoryPort userRepositoryPort) {
        return new GetUserByIdAndEmailUseCase(userRepositoryPort);
    }

    @Bean
    public ReportRaceResultsPort reportRaceResultsPort(TournamentRepositoryPort tournamentRepositoryPort,
            RaceResultPersistencePort raceResultPersistencePort,
            UserRepositoryPort userRepositoryPort) {
        return new ReportRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort, userRepositoryPort);
    }

    @Bean
    public GetRaceResultsPort getRaceResultsPort(TournamentRepositoryPort tournamentRepositoryPort,
            RaceResultPersistencePort raceResultPersistencePort) {
        return new GetRaceResultsUseCase(tournamentRepositoryPort, raceResultPersistencePort);
    }
    @Bean
    public ToOrganizerPort ToOrganizerPort(UserRepositoryPort userRepositoryPort) {
        return new ToOrganizerUseCase(userRepositoryPort);
    }

    @Bean
    public RemoveTeamFromTournamentPort removeTeamFromTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            TeamRemovalPort teamRemovalPort,
            NotificationPort notificationPort) {
        return new RemoveTeamFromTournamentUseCase(tournamentRepositoryPort, teamRemovalPort, notificationPort);
    }

    @Bean
    public GetUserNotificationsPort getUserNotificationsPort(NotificationPort notificationPort) {
        return new GetUserNotificationsUseCase(notificationPort);
    }

    @Bean
    public MarkNotificationAsReadPort markNotificationAsReadPort(NotificationPort notificationPort) {
        return new MarkNotificationAsReadUseCase(notificationPort);
    }

    @Bean
    public CreateNotificationPort createNotificationPort(
            NotificationPort notificationPort,
            NotificationSseService notificationSseService) {
        return new CreateNotificationUseCase(notificationPort, notificationSseService);
    }

    @Bean
    public CancelTournamentPort cancelTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            TournamentCleanupPort tournamentCleanupPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        return new CancelTournamentUseCase(tournamentRepositoryPort, tournamentCleanupPort, userRepositoryPort, notificationPort);
    }

    @Bean
    public StartTournamentPort startTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        return new StartTournamentUseCase(tournamentRepositoryPort, userRepositoryPort, notificationPort);
    }

    @Bean
    public FinalizeTournamentPort finalizeTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            FixturePersistencePort fixturePersistencePort,
            RaceResultPersistencePort raceResultPersistencePort,
            NotificationPort notificationPort) {
        return new FinalizeTournamentUseCase(tournamentRepositoryPort, fixturePersistencePort, raceResultPersistencePort, notificationPort);
    }

    @Bean
    public RateOrganizerPort rateOrganizerPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            ReputationRepositoryPort reputationRepositoryPort) {
        return new RateOrganizerUseCase(tournamentRepositoryPort, userRepositoryPort, reputationRepositoryPort);
    }

    @Bean
    public GetOrganizerReputationPort getOrganizerReputationPort(
            UserRepositoryPort userRepositoryPort,
            ReputationRepositoryPort reputationRepositoryPort) {
        return new GetOrganizerReputationUseCase(reputationRepositoryPort, userRepositoryPort);
    }

    @Bean
    public UpdateTournamentPort updateTournamentPort(TournamentRepositoryPort tournamentRepositoryPort, NotificationPort notificationPort) {
        return new UpdateTournamentUseCase(tournamentRepositoryPort, notificationPort);
    }

    // ==================== BLOG SYSTEM BEANS ====================
    
    @Bean
    public CreatePostPort createPostPort(PostRepositoryPort postRepository, UserRepositoryPort userRepository) {
        return new CreatePostUseCase(postRepository, userRepository);
    }
    
    @Bean
    public GetPostsPort getPostsPort(PostRepositoryPort postRepository) {
        return new GetPostsUseCase(postRepository);
    }
    
    @Bean
    public ClosePostPort closePostPort(PostRepositoryPort postRepository) {
        return new ClosePostUseCase(postRepository);
    }
    
    @Bean
    public CreateComentarioPort createComentarioPort(
            ComentarioRepositoryPort comentarioRepository,
            PostRepositoryPort postRepository,
            UserRepositoryPort userRepository) {
        return new CreateComentarioUseCase(comentarioRepository, postRepository, userRepository);
    }
    
    @Bean
    public GetComentariosPort getComentariosPort(ComentarioRepositoryPort comentarioRepository) {
        return new GetComentariosUseCase(comentarioRepository);
    }
    
    @Bean
    public ContactarAvisoPort contactarAvisoPort(
            ContactoReveladoRepositoryPort contactoRepository,
            PostRepositoryPort postRepository,
            UserRepositoryPort userRepository) {
        return new ContactarAvisoUseCase(contactoRepository, postRepository, userRepository);
    }
    
    @Bean
    public GetContactosPort getContactosPort(ContactoReveladoRepositoryPort contactoRepository) {
        return new GetContactosUseCase(contactoRepository);
    }
    
    @Bean
    public ChangePasswordPort changePasswordPort(
            com.example.demo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa userRepositoryJpa,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return new ChangePasswordUseCase(userRepositoryJpa, passwordEncoder);
    }
}

