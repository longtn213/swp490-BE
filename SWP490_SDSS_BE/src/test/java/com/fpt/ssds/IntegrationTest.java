//package com.fpt.ssds;
//
//import com.fpt.ssds.SsdsApp;
//import com.fpt.ssds.config.AsyncSyncConfiguration;
//import com.fpt.ssds.config.EmbeddedRedis;
//import com.fpt.ssds.config.EmbeddedSQL;
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.DirtiesContext;
//
///**
// * Base composite annotation for integration tests.
// */
//@Target(ElementType.TYPE)
//@Retention(RetentionPolicy.RUNTIME)
//@SpringBootTest(classes = { SsdsApp.class, AsyncSyncConfiguration.class })
//@EmbeddedRedis
//@EmbeddedSQL
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//public @interface IntegrationTest {
//}
