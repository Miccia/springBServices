package com.example.pamel;

	
	
	import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
	import org.apache.camel.model.rest.*;
	//import org.apache.camel.*;
	import org.springframework.stereotype.Component;

import java.util.List;

import org.apache.camel.Exchange;
	import org.apache.camel.LoggingLevel;
	import org.apache.camel.Processor;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.*;
	
	@Component
	public class CamelRoutingOut extends RouteBuilder{

	@Override
	    public void configure() throws Exception {
		JacksonDataFormat jsonDataFormat = new JacksonDataFormat(main.java.lib.RawInData.class);
		
        //String serverUrl = "http://localhost:8080/kie-server/services/rest/server";
     //  String user = "kserver";
    //    String password = "66Server";
     //   String containerId ="pamelblu_1.0.0";
		
        
      	from("jms:queue:pamqueue")
			 .log(LoggingLevel.INFO,"SERVICEOut\t:\tmessage consumed from pamqueue")
	         .unmarshal(jsonDataFormat)   
			 .process(new tracedProcessorOut(main.java.lib.Tracing.init("pamel_amq"))
					 ).marshal(jsonDataFormat)
	            .to("jms:queue:camelJms");
	    }

	}


