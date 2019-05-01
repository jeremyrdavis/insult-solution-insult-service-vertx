package com.redhat.summit2019;

import com.redhat.summit2019.model.Insult;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.StaticHandler;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class HttpApplication extends AbstractVerticle {

    static final String template = "Hello, %s!";

    WebClient webClient;

    private static final String ADJECTIVE_HOST = "insult-adjectives-redhat-summit-insult-workshop-vertx.b9ad.pro-us-east-1.openshiftapps.com";

    private static final String NOUN_HOST = "insult-nouns-redhat-summit-insult-workshop-vertx.b9ad.pro-us-east-1.openshiftapps.com";


    @Override
    public void start(Future<Void> future) {

        webClient = WebClient.create(vertx);

        // Create a router object.
        Router router = Router.router(vertx);

        router.get("/api/greeting").handler(this::greeting);
        router.get("/api/insult").handler(this::insultHandler);
        router.get("/*").handler(StaticHandler.create());

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(
                        // Retrieve the port from the configuration, default to 8080.
                        config().getInteger("http.port", 8080), ar -> {
                            if (ar.succeeded()) {
                                System.out.println("Server started on port " + ar.result().actualPort());
                            }
                            future.handle(ar.mapEmpty());
                        });

    }

    private void insultHandler(RoutingContext rc) {

        Single<JsonObject> noun = webClient
                .get(80, NOUN_HOST,"/api/noun")
                .rxSend()
                .doOnSuccess(r -> System.out.println((r.bodyAsString())))
                .map(HttpResponse::bodyAsJsonObject)
                .doOnError(e -> {
                    rc.response()
                            .setStatusCode(500)
                            .putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                            .end(new JsonObject().put("error", e.getMessage()).encodePrettily());
                });

        Single<JsonObject> adj1 = webClient
                .get(80, ADJECTIVE_HOST, "/api/adjective")
                .rxSend()
                .doOnSuccess(r -> System.out.println(r.bodyAsString()))
                .map(HttpResponse::bodyAsJsonObject)
                .doOnError(e -> {
                    rc.response()
                            .setStatusCode(500)
                            .putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                            .end(new JsonObject().put("error", e.getMessage()).encodePrettily());
                });

        Single<JsonObject> adj2 = webClient
                .get(80, ADJECTIVE_HOST, "/api/adjective")
                .rxSend()
                .doOnSuccess(r -> System.out.println(r.bodyAsString()))
                .map(HttpResponse::bodyAsJsonObject)
                .doOnError(e -> {
                    rc.response()
                            .setStatusCode(500)
                            .putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                            .end(new JsonObject().put("error", e.getMessage()).encodePrettily());
                });

        Single.zip(
                adj1.doOnError(error -> error(rc, error)),
                adj2.doOnError(error -> error(rc, error)),
                noun.doOnError(error -> error(rc, error)),
                Insult::new)
                .subscribe(r ->
                rc.response()
                        .putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                        .end(r.toString()));
    }

    private void error(RoutingContext rc, Throwable error){
        rc.response()
                .setStatusCode(500)
                .putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                .end(new JsonObject().put("error", error.getMessage()).encodePrettily());
    }


    private void greeting(RoutingContext rc) {
        String name = rc.request().getParam("name");
        if (name == null) {
            name = "World";
        }

        JsonObject response = new JsonObject()
                .put("content", String.format(template, name));

        rc.response()
                .putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
                .end(response.encodePrettily());
    }

}