package com.tutorneo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tutorneo.adapters.out.persistence.jpa.mappers.UserMapper;
import com.tutorneo.core.application.usecase.ChangePasswordUseCase;
import com.tutorneo.core.application.usecase.CreateTournamentUseCase;
import com.tutorneo.core.application.usecase.GetAllTournamentsUseCase;
import com.tutorneo.core.application.usecase.GetTournamentByIdUseCase;
import com.tutorneo.core.application.usecase.GetLatestTournamentsUseCase;
import com.tutorneo.core.application.usecase.GetTournamentUseCase;
import com.tutorneo.core.application.usecase.GetUserByIdAndEmailUseCase;
import com.tutorneo.core.application.usecase.GetUserByIdUseCase;
import com.tutorneo.core.application.usecase.ListDisciplinesUseCase;
import com.tutorneo.core.application.usecase.ListFormatsByDisciplineUseCase;
import com.tutorneo.core.application.usecase.ListPublicTournamentsUseCase;
import com.tutorneo.core.application.usecase.ListTournamentsByStatusUseCase;
import com.tutorneo.core.application.usecase.ListUsersAdminUseCase;
import com.tutorneo.core.application.usecase.ListUsersUseCase;
import com.tutorneo.core.application.usecase.GenerateEliminationFixtureUseCase;
import com.tutorneo.core.application.usecase.GenerateLeagueFixtureUseCase;
import com.tutorneo.core.application.usecase.RegisterRunnerToTournamentUseCase;
import com.tutorneo.core.application.usecase.GetRaceResultsUseCase;
import com.tutorneo.core.application.usecase.ReportRaceResultsUseCase;
import com.tutorneo.core.application.usecase.DeactivateUserUseCase;
import com.tutorneo.core.application.usecase.RestoreUserUseCase;
import com.tutorneo.core.application.usecase.RequestOrganizerRoleUseCase;
import com.tutorneo.core.application.usecase.GetMyOrganizerRoleRequestUseCase;
import com.tutorneo.core.application.usecase.ListOrganizerRoleRequestsUseCase;
import com.tutorneo.core.application.usecase.ReviewOrganizerRoleRequestUseCase;
import com.tutorneo.core.application.usecase.ToOrganizerUseCase;
import com.tutorneo.core.application.usecase.RegisterTeamToTournamentUseCase;
import com.tutorneo.core.application.usecase.RegisterToTournamentUseCase;
import com.tutorneo.core.application.usecase.RegisterUserUseCase;
import com.tutorneo.core.application.usecase.ReportLeagueMatchResultUseCase;
import com.tutorneo.core.application.usecase.ReportMatchResultUseCase;
import com.tutorneo.core.application.usecase.GetLeagueStandingsUseCase;
import com.tutorneo.core.application.usecase.UpdateUserUseCase;
import com.tutorneo.core.application.usecase.CancelTournamentUseCase;
import com.tutorneo.core.application.usecase.StartTournamentUseCase;
import com.tutorneo.core.application.usecase.FinalizeTournamentUseCase;
import com.tutorneo.core.application.usecase.RemoveTeamFromTournamentUseCase;
import com.tutorneo.core.application.usecase.GetUserNotificationsUseCase;
import com.tutorneo.core.application.usecase.MarkNotificationAsReadUseCase;
import com.tutorneo.core.application.usecase.CreateNotificationUseCase;
import com.tutorneo.core.application.usecase.RateOrganizerUseCase;
import com.tutorneo.core.application.usecase.GetOrganizerReputationUseCase;
import com.tutorneo.core.application.usecase.UpdateTournamentUseCase;
import com.tutorneo.core.application.usecase.AdminDeactivateTournamentUseCase;
import com.tutorneo.core.application.usecase.AdminReactivateTournamentUseCase;
import com.tutorneo.core.application.usecase.LeaveTournamentUseCase;
import com.tutorneo.core.application.usecase.CreatePostUseCase;
import com.tutorneo.core.application.usecase.GetPostsUseCase;
import com.tutorneo.core.application.usecase.ClosePostUseCase;
import com.tutorneo.core.application.usecase.CreateComentarioUseCase;
import com.tutorneo.core.application.usecase.GetComentariosUseCase;
import com.tutorneo.core.application.usecase.ContactarAvisoUseCase;
import com.tutorneo.core.application.usecase.GetContactosUseCase;
import com.tutorneo.core.application.usecase.SendContactMessageUseCase;
import com.tutorneo.core.application.service.NotificationSseService;
import com.tutorneo.core.ports.in.ChangePasswordPort;
import com.tutorneo.core.ports.in.CreateTournamentPort;
import com.tutorneo.core.ports.in.GetAllTournamentsPort;
import com.tutorneo.core.ports.in.RemoveTeamFromTournamentPort;
import com.tutorneo.core.ports.in.GetUserNotificationsPort;
import com.tutorneo.core.ports.in.MarkNotificationAsReadPort;
import com.tutorneo.core.ports.in.CreateNotificationPort;
import com.tutorneo.core.ports.in.GetTournamentByIdPort;
import com.tutorneo.core.ports.in.GetLatestTournamentsPort;
import com.tutorneo.core.ports.in.GetTournamentPort;
import com.tutorneo.core.ports.in.GetUserByIdAndEmailPort;
import com.tutorneo.core.ports.in.GetUserByIdPort;
import com.tutorneo.core.ports.in.ListDisciplinesPort;
import com.tutorneo.core.ports.in.ListFormatsByDisciplinePort;
import com.tutorneo.core.ports.in.ListUsersAdminPort;
import com.tutorneo.core.ports.in.ListPublicTournamentsPort;
import com.tutorneo.core.ports.in.ListTournamentsByStatusPort;
import com.tutorneo.core.ports.in.ListUsersPort;
import com.tutorneo.core.ports.in.DeactivateUserPort;
import com.tutorneo.core.ports.in.RestoreUserPort;
import com.tutorneo.core.ports.in.RequestOrganizerRolePort;
import com.tutorneo.core.ports.in.GetMyOrganizerRoleRequestPort;
import com.tutorneo.core.ports.in.ListOrganizerRoleRequestsPort;
import com.tutorneo.core.ports.in.ReviewOrganizerRoleRequestPort;
import com.tutorneo.core.ports.in.RegisterRunnerToTournamentPort;
import com.tutorneo.core.ports.in.GenerateLeagueFixturePort;
import com.tutorneo.core.ports.in.GetRaceResultsPort;
import com.tutorneo.core.ports.in.RegisterTeamToTournamentPort;
import com.tutorneo.core.ports.in.RegisterToTournamentPort;
import com.tutorneo.core.ports.in.RegisterUserPort;
import com.tutorneo.core.ports.in.ReportLeagueMatchResultPort;
import com.tutorneo.core.ports.in.ReportMatchResultPort;
import com.tutorneo.core.ports.in.ReportRaceResultsPort;
import com.tutorneo.core.ports.in.ToOrganizerPort;
import com.tutorneo.core.ports.in.GetLeagueStandingsPort;
import com.tutorneo.core.ports.in.UpdateProfilePort;
import com.tutorneo.core.ports.in.CancelTournamentPort;
import com.tutorneo.core.ports.in.StartTournamentPort;
import com.tutorneo.core.ports.in.FinalizeTournamentPort;
import com.tutorneo.core.ports.in.RateOrganizerPort;
import com.tutorneo.core.ports.in.GetOrganizerReputationPort;
import com.tutorneo.core.ports.in.UpdateTournamentPort;
import com.tutorneo.core.ports.in.AdminDeactivateTournamentPort;
import com.tutorneo.core.ports.in.AdminReactivateTournamentPort;
import com.tutorneo.core.ports.in.LeaveTournamentPort;
import com.tutorneo.core.ports.in.CreatePostPort;
import com.tutorneo.core.ports.in.GetPostsPort;
import com.tutorneo.core.ports.in.ClosePostPort;
import com.tutorneo.core.ports.in.CreateComentarioPort;
import com.tutorneo.core.ports.in.GetComentariosPort;
import com.tutorneo.core.ports.in.ContactarAvisoPort;
import com.tutorneo.core.ports.in.GetContactosPort;
import com.tutorneo.core.ports.in.SendContactMessagePort;
import com.tutorneo.core.ports.out.DisciplineRepositoryPort;
import com.tutorneo.core.ports.out.EmailSenderPort;
import com.tutorneo.core.ports.out.FindTournamentsByStatusPort;
import com.tutorneo.core.ports.out.FindTournamentsPort;
import com.tutorneo.core.ports.out.FormatRepositoryPort;
import com.tutorneo.core.ports.out.FixturePersistencePort;
import com.tutorneo.core.ports.out.TeamQueryPort;
import com.tutorneo.core.ports.out.TeamCaptainQueryPort;
import com.tutorneo.core.ports.out.RaceResultPersistencePort;
import com.tutorneo.core.ports.out.TeamRegistrationPort;
import com.tutorneo.core.ports.out.TeamRemovalPort;
import com.tutorneo.core.ports.out.NotificationPort;
import com.tutorneo.core.ports.out.TournamentRegistrationPort;
import com.tutorneo.core.ports.out.TournamentRepositoryPort;
import com.tutorneo.core.ports.out.UserRepositoryPort;
import com.tutorneo.core.ports.out.UserAdminReadPort;
import com.tutorneo.core.ports.out.OrganizerRoleRequestRepositoryPort;
import com.tutorneo.core.ports.out.TournamentCleanupPort;
import com.tutorneo.core.ports.out.ReputationRepositoryPort;
import com.tutorneo.core.ports.out.PostRepositoryPort;
import com.tutorneo.core.ports.out.ComentarioRepositoryPort;
import com.tutorneo.core.ports.out.ContactoReveladoRepositoryPort;

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
    public RequestOrganizerRolePort requestOrganizerRolePort(
            OrganizerRoleRequestRepositoryPort organizerRoleRequestRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        return new RequestOrganizerRoleUseCase(organizerRoleRequestRepositoryPort, userRepositoryPort);
    }

    @Bean
    public GetMyOrganizerRoleRequestPort getMyOrganizerRoleRequestPort(
            OrganizerRoleRequestRepositoryPort organizerRoleRequestRepositoryPort) {
        return new GetMyOrganizerRoleRequestUseCase(organizerRoleRequestRepositoryPort);
    }

    @Bean
    public ListOrganizerRoleRequestsPort listOrganizerRoleRequestsPort(
            OrganizerRoleRequestRepositoryPort organizerRoleRequestRepositoryPort) {
        return new ListOrganizerRoleRequestsUseCase(organizerRoleRequestRepositoryPort);
    }

    @Bean
    public ReviewOrganizerRoleRequestPort reviewOrganizerRoleRequestPort(
            OrganizerRoleRequestRepositoryPort organizerRoleRequestRepositoryPort,
            UserRepositoryPort userRepositoryPort) {
        return new ReviewOrganizerRoleRequestUseCase(organizerRoleRequestRepositoryPort, userRepositoryPort);
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
    public LeaveTournamentPort leaveTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            TournamentRegistrationPort tournamentRegistrationPort,
            TeamCaptainQueryPort teamCaptainQueryPort,
            TeamRemovalPort teamRemovalPort,
            CreateNotificationPort createNotificationPort) {
        return new LeaveTournamentUseCase(
                tournamentRepositoryPort,
                userRepositoryPort,
                tournamentRegistrationPort,
                teamCaptainQueryPort,
                teamRemovalPort,
                createNotificationPort);
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

    @Bean
    public AdminDeactivateTournamentPort adminDeactivateTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        return new AdminDeactivateTournamentUseCase(tournamentRepositoryPort, userRepositoryPort, notificationPort);
    }

    @Bean
    public AdminReactivateTournamentPort adminReactivateTournamentPort(
            TournamentRepositoryPort tournamentRepositoryPort,
            UserRepositoryPort userRepositoryPort,
            NotificationPort notificationPort) {
        return new AdminReactivateTournamentUseCase(tournamentRepositoryPort, userRepositoryPort, notificationPort);
    }

    @Bean
    public SendContactMessagePort sendContactMessagePort(
            EmailSenderPort emailSenderPort,
            @Value("${contact.to.email:gestiontorneosuy@gmail.com}") String toEmail) {
        return new SendContactMessageUseCase(emailSenderPort, toEmail);
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
            com.tutorneo.adapters.out.persistence.jpa.interfaces.UserRepositoryJpa userRepositoryJpa,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return new ChangePasswordUseCase(userRepositoryJpa, passwordEncoder);
    }
}

