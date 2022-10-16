package com.kubperf.kubernetesperformanceback.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
@Getter
@Setter
public class User {
    private String id;
    private String secret;
    private String nick;
    private String token;
}
