module cn.mmooo
{
    requires java.base;
    requires java.se;
    requires jdk.net;
    requires kotlin.reflect;
    requires kotlin.stdlib;
    requires io.vertx.core;
    requires io.vertx.web;
    requires io.vertx.stomp;
    requires io.vertx.eventbusbridge.common;
    requires io.netty.handler;
    requires io.netty.common;
    requires io.netty.resolver.dns;
    requires io.netty.codec.dns;
//    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.kotlin;
    requires ch.qos.logback.classic;
    requires org.slf4j.jdk.platform.logging;
    opens webroot;
}
