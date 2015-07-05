## Navi-pbrpc===Navi-pbrpc provides a rpc solution for using protocol buffer. This library enables client and server to communicate in a peer-to-peer and full duplexing way. The server-side is built upon netty which supports asynchronous io, while the client-side provides a wide variety of options to communicate with server, which includes short live connection, keep-alive tcp connection, high availability and failover strategy.## Quick Start===### 1. Prerequisite
Add the below dependency to pom.xml for a maven enabled project.

	<dependency>    	<groupId>com.baidu.beidou</groupId>    	<artifactId>navi-pbrpc</artifactId>    	<version>1.0.0</version>	</dependency>### 2. Make a protobuf generated message
Use protoc command to compile an IDL proto file and generate a java source file. The IDL proto file can define the request and response type. Below is a simple sample:


```package com.baidu.beidou.navi.pbrpc.demo.proto; option cc_generic_services = true;message DemoRequest {    optional int32 user_id = 1;}message DemoResponse {    optional int32 user_id = 1;    optional string user_name = 2;    enum GenderType {        MALE = 1;        FEMALE = 2;    }      optional GenderType gender_type = 3;}
```### 3. Develop server-side service
Develop a server-side service implementation. Below is an example based on the IDL generated java code from the previous step.    public class DemoServiceImpl implements DemoService {        @Override        public DemoResponse doSmth(DemoRequest req) {            DemoResponse.Builder builder = DemoResponse.newBuilder();            builder.setUserId(1);            builder.setUserName("name-1");            builder.setGenderType(DemoResponse.GenderType.MALE);        return builder.build();        }        }
### 4. Expose service and start server

Below shows how to register or inject the service implementation and start the server on an specific port.```PbrpcServer server = new PbrpcServer(8088);server.register(100, new DemoServiceImpl());server.start();```### 5. Develop client to invoke remote service
The framework provides many options to communicate with the server in terms of short live connection or keep-alive connection and high availablity and failover strategy. You can check out on the project wiki.Below demostrates how to build a keep-alive connection pool and invoke rpc call.
```// Build client PbrpcClient client = PbrpcClientFactory.buildPooledConnection(new PooledConfiguration(),        "127.0.0.1", 8088, 60000);// Construct request data by using protobufDemoRequest.Builder req = DemoRequest.newBuilder();req.setUserId(1);byte[] data = req.build().toByteArray();// Build messagePbrpcMsg msg = new PbrpcMsg();msg.setServiceId(100);msg.setProvider("beidou");msg.setData(data);// Asynchronous invocationCallFuture<DemoResponse> future = client.asyncTransport(DemoResponse.class, msg);// Wait response to come. Once rpc call is done, the code will stop blocking right away.DemoResponse res = future.get();// Print out result.System.out.println(res);
```
### More information
Click here to wiki.
### Supports ![](http://neoremind.net/imgs/gmail.png)