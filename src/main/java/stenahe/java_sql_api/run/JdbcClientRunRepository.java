package stenahe.java_sql_api.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcClientRunRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcClientRunRepository.class);
    private final JdbcClient jdbcClient;

    public JdbcClientRunRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void create(Run run) {
        var updated = jdbcClient.sql("INSERT INTO Run(id,title,started_on,completed_on,miles,location) values(?,?,?,?,?,?)")
                .params(List.of(run.id(),run.title(),run.startedOn(),run.completedOn(),run.miles(),run.location().toString()))
                .update();

        Assert.state(updated == 1, "Failed to create run " + run.title());
    }

    public int count() {
        return jdbcClient.sql("select * from run").query().listOfRows().size();
    }

    public void saveAll(List<Run> runs) {
        runs.stream().forEach(this::create);
    }

}