package stenahe.java_sql_api.run;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/runs")
public class RunController {

    private final RunRepository runRepository;

    public RunController(RunRepository runRepository) {
        this.runRepository = runRepository;
    }

    @GetMapping
    List<Run> findAll() {
        return runRepository.findAll();
    }

    @GetMapping("/{id}")
    Run findById(@PathVariable Integer id) {
        Optional<Run> run = runRepository.findById(id);
        if (run.isEmpty()) {
            throw new RunNotFoundException("Run with id " + id + " not found.");
        }
        return run.get();
    }

    @GetMapping("/location/{location}")
    List<Run> findByLocation(@PathVariable String location) {
        return runRepository.findAllByLocation(location);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    void create(@Valid @RequestBody Run run) {
        runRepository.save(run);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void update(@PathVariable Integer id, @Valid @RequestBody Run run) {
        Run existingRun = runRepository.findById(id).orElseThrow(
                () -> new RunNotFoundException("Run with id " + id + " not found.")
        );
        Run updatedRun = new Run(
                existingRun.id(),
                run.title(),
                run.startedOn(),
                run.completedOn(),
                run.miles(),
                run.location(),
                existingRun.version()
        );
        runRepository.save(updatedRun);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable Integer id) {
        runRepository.findById(id).ifPresentOrElse(
                runRepository::delete,
                () -> {
                    throw new RunNotFoundException("Run with id " + id + " not found.");
                }
        );
    }

}
