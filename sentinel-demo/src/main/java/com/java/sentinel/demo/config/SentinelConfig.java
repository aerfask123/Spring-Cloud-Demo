package com.java.sentinel.demo.config;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig implements InitializingBean, DisposableBean, EnvironmentAware {

    private Environment env;

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @Bean
    public CommandLineRunner initSentinelRules() {
        return args -> {
            List<FlowRule> rules = new ArrayList<>();
            FlowRule rule = new FlowRule();
            rule.setResource("HelloWorld");
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(20);
            rules.add(rule);
            FlowRuleManager.loadRules(rules);
        };
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (PropertySource source : ((AbstractEnvironment) env).getPropertySources()) {
            System.out.println(source.getName() + "->" + source.getClass().getName());
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        env = environment;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy Bean");
    }

    @EventListener(value = {ContextClosedEvent.class})
    public void close() {
        System.out.println("close Bean");
    }
}
