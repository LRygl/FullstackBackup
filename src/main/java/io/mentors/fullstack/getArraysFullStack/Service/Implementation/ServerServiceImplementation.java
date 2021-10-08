package io.mentors.fullstack.getArraysFullStack.Service.Implementation;

import io.mentors.fullstack.getArraysFullStack.Enum.Status;
import io.mentors.fullstack.getArraysFullStack.Model.Server;
import io.mentors.fullstack.getArraysFullStack.Repository.ServerRepository;
import io.mentors.fullstack.getArraysFullStack.Service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Random;

//nemusím dělat celé dependency injection ale stačí PF ServerRepository serverRepository
@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class ServerServiceImplementation implements ServerService {
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
}
