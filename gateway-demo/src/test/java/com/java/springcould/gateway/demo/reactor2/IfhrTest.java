package com.java.springcould.gateway.demo.reactor2;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IfhrTest {

    @Test
    public void test1() {
        Flux<String> ids = ifhrIds();

        Flux<String> combinations =
                ids.flatMap(id -> {
                    Mono<String> nameTask = ifhrName(id);
                    Mono<Integer> statTask = ifhrStat(id);

                    return nameTask.zipWith(statTask,
                            (name, stat) -> {
                                String result = "Name " + name + " has stats " + stat;
                                System.out.println(result);
                                return result;
                            });
                });

        Mono<List<String>> result = combinations.collectList();

        List<String> results = result.block();
        assertThat(results).containsExactly(
                "Name NameJoe has stats 103",
                "Name NameBart has stats 104",
                "Name NameHenry has stats 105",
                "Name NameNicole has stats 106",
                "Name NameABSLAJNFOAJNFOANFANSF has stats 121"
        );
    }

    private Mono<String> ifhrName(String id) {
        return Mono.fromSupplier(() -> {
            int delimiter = id.indexOf(":");
            if (delimiter == -1) {
                return "";
            }
            return "Name" + id.substring(0, delimiter);
        });
    }

    private Mono<Integer> ifhrStat(String id) {
        return Mono.fromSupplier(() -> {
            int delimiter = id.indexOf(":");
            if (delimiter == -1) {
                return null;
            }
            String num = id.substring(delimiter + 1);
            return Integer.parseInt(num);
        });
    }

    public Flux<String> ifhrIds() {
        return Flux.just("Joe:103", "Bart:104", "Henry:105", "Nicole:106", "ABSLAJNFOAJNFOANFANSF:121")
                .delayElements(Duration.ofSeconds(1));
    }
}
