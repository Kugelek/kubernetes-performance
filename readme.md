# kubernetes-performance
CMD:
```"C:\Program Files\Promtheus\prometheus-2.45.0.windows-amd64\prometheus.exe" --config.file=prom_conf.yaml```

### Jaeger
```docker run -d --name jaeger -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 -e METRICS_STORAGE_TYPE=prometheus -e MONITOR_MENU_ENABLED=true -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 jaegertracing/all-in-one:latest```

### Prometheus/ Alertmanager 

Przed wykonaniem komendy poniżej: w `etc/hosts` dodaj linijkę `alertmanager 127.0.0.1`

```cd IdeaProjects\kubernetes-performance\kubernetes-performance-back```

```docker-compose up``` w *kubernetes-performance-assets*
