package io.mentors.fullstack.getArraysFullStack.Service.Implementation;

import io.mentors.fullstack.getArraysFullStack.Enum.Status;
import io.mentors.fullstack.getArraysFullStack.Model.Server;
import io.mentors.fullstack.getArraysFullStack.Repository.ServerRepository;
import io.mentors.fullstack.getArraysFullStack.Service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

//nemusím dělat celé dependency injection ale stačí PF ServerRepository serverRepository
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImplementation implements ServerService {
    //USING Slf4j  private static final Logger LOGGER = LoggerFactory.getLogger(TerminalRepositoryImpl.class);
    private final ServerRepository serverRepository;

    @Override
    public Server create(Server server) {
        log.info("Saving new server: {}", server.getName());
        server.setImageUrl(setServerImageUrl());
        return serverRepository.save(server);
    }

    @Override
    public Server ping(String ipAddress) throws IOException {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = serverRepository.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        serverRepository.save(server);
        return server;
    }

    @Override
    public Collection<Server> list(int limit) {
        log.info("Fetching all servers");
        return serverRepository.findAll(PageRequest.of(0,limit)).toList();
    }

    @Override
    public Server get(Long id) {
        log.info("Fetching server by id: {}",id);
        return serverRepository.findById(id).get();
    }

    @Override
    public Server update(Server server) {
        log.info("Updating server: {}", server.getName());
        return serverRepository.save(server);
    }

    @Override
    public Boolean delete(Long id) {
        log.info("Deleting server by id: {}", id);
        serverRepository.deleteById(id);
        return Boolean.TRUE;
    }


    private String setServerImageUrl() {
        String[] imageNames = { "server1.png","server2.png", "server3.png", "server4.png" };

        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/server/image/" + imageNames[new Random().nextInt(4)]).toUriString();
    }


    //TODO CUSTOM QUERY WITH NATIVE QUERY
/*    @Override
    @SuppressWarnings("unchecked")
    public List<Terminal> getAll() {
        final String query;
        if (applicationProperties.isOracleDatabase()) {
            query = "SELECT tid.TERMINAL_ID AS terminalId, tid.TERM_SERIALNUM AS terminalSerialNumber, s.SITEIDENTIFIER AS terminalSiteId, tc.ACTIVE AS terminalStatus, s.STREET AS terminalStreet, s.HOUSENUMBER AS terminalHouseNo, s.CITY AS terminalCity, s.POSTALCODE AS terminalZip FROM TERMINAL_INIT_DATA tid, SMC_TERMINALCONFIG tc, SMC_SITE s WHERE tid.TERMINAL_ID = tc.TERMINALIDENTIFIER AND tc.ID = (SELECT ID FROM (SELECT tc1.ID FROM SMC_TERMINALCONFIG tc1 WHERE tc1.TERMINALIDENTIFIER = tc.TERMINALIDENTIFIER ORDER BY tc1.ID DESC) WHERE ROWNUM = 1) AND tc.FK_SITEID = s.ID ORDER BY tid.TERMINAL_ID";
        } else if (applicationProperties.isPostgreSQLDatabase()) {
            query = "SELECT tid.TERMINAL_ID AS terminalId, tid.TERM_SERIALNUM AS terminalSerialNumber, s.SITEIDENTIFIER AS terminalSiteId, tc.ACTIVE AS terminalStatus, s.STREET AS terminalStreet, s.HOUSENUMBER AS terminalHouseNo, s.CITY AS terminalCity, s.POSTALCODE AS terminalZip FROM TERMINAL_INIT_DATA tid, SMC_TERMINALCONFIG tc, SMC_SITE s WHERE tid.TERMINAL_ID = tc.TERMINALIDENTIFIER AND tc.ID = (SELECT id FROM smc_terminalconfig WHERE tc.terminalidentifier = tc.terminalidentifier ORDER BY id DESC LIMIT 1) AND tc.FK_SITEID = s.ID ORDER BY tid.TERMINAL_ID";
        } else {
            LOGGER.error("Undefined database connection. Database connection must be defined in application.properties. Database is choosed based on spring.datasource.driver-class-name property. Allowed database are Oracle and PostgreSQL.");

            throw new IllegalArgumentException("Undefined database connection. Database connection must be defined in application.properties. Database is choosed based on spring.datasource.driver-class-name property. Allowed database are Oracle and PostgreSQL.");
        }
        List<Terminal> terminals = entityManager.createNativeQuery(query, "TerminalSqlResultSetMapping").getResultList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("terminals = {}", Arrays.toString(terminals.toArray()));
        }

        return terminals;
    }*/


}
