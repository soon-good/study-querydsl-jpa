package io.study.transaction.transaction_study.connection;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

@Testcontainers
@ActiveProfiles("test-docker")
@SpringBootTest
public class TestContainerConnectionTest {

    static final DockerComposeContainer container;

    static{
        container = new DockerComposeContainer(new File("src/test/resources/docker/docker-compose/docker-compose.yml"));
        container.start();
    }

    @Test
    public void TEST_DOCKER_COMPOSE_CONTAINER_LOADING(){

    }
}
