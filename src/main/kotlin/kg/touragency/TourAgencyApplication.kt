package kg.touragency

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TourAgencyApplication

fun main(args: Array<String>) {
    runApplication<TourAgencyApplication>(*args)
}
