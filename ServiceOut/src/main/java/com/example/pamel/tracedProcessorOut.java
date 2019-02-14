package com.example.pamel;

import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.UserTaskServicesClient;

//import org.apache.camel.Exchange;
//import io.jaegertracing.internal.JaegerTracer;
//import io.opentracing.Span;
//import io.opentracing.Tracer;
//import main.java.lib.Tracing;


public class tracedProcessorOut implements org.apache.camel.Processor{
	

    private String serverUrl = "http://localhost:8080/kie-server/services/rest/server";
    private String user = "kserver";
    private String password = "66Server";
    private String containerId ="pamelblu_1.0.0";
    private io.jaegertracing.internal.JaegerTracer jtrc;
	
	public tracedProcessorOut(io.jaegertracing.internal.JaegerTracer tracer){	
		super();
		jtrc = tracer;
	}

	@Override
	public void process(org.apache.camel.Exchange ex) throws Exception {
		
	//	io.jaegertracing.internal.JaegerTracer jtrc = main.java.lib.Tracing.init("pamel_amq");
	       try (io.opentracing.Scope scope = jtrc.buildSpan("ServiceOut_process").startActive(true)) {
	    	   
            	main.java.lib.RawInData rid = (main.java.lib.RawInData) ex.getIn().getBody();
                scope.span().setTag("consuming_json",""+rid.toString());
                Long iId = Long.parseLong(ex.getIn().getHeader("processInstanceId",String.class));
                executeTask(iId,rid);
	        }
		
	}
	
	
	//,io.opentracing.Scope scope
	public void executeTask(Long pid,main.java.lib.RawInData data) throws Exception {
	//	io.jaegertracing.internal.JaegerTracer jtrc = main.java.lib.Tracing.init("pamel_amq");

		  try (io.opentracing.Scope scope = jtrc.buildSpan("ServiceOut_complete").startActive(true)) {
		    	
			scope.span().setTag("completing_user_task",""+pid);
			
			
			
			//creating the parameters to complete the user task
			java.util.Map<String,Object> params = new java.util.HashMap<String, Object>();
			params.put("processed",(Object) new main.java.lib.CookedOutData());
			
			
			
			KieServicesConfiguration configuration     =  KieServicesFactory.newRestConfiguration(serverUrl, user, password);
            KieServicesClient 		 kieServicesClient =  KieServicesFactory.newKieServicesClient(configuration);
            UserTaskServicesClient taskClient = kieServicesClient.getServicesClient(UserTaskServicesClient.class);
			
			java.util.List<String> ll = new java.util.ArrayList<>();
            							ll.add("Created");
            							ll.add("Ready");
            							ll.add("Reserved");
          //  ll.add("Completed");
            java.util.List<TaskSummary> taskByProcessInstanceId = 
            			taskClient.findTasksByStatusByProcessInstanceId(pid,ll,0, 10);
            System.out.println(taskByProcessInstanceId);
               
            taskByProcessInstanceId.forEach(task -> {
            								taskClient.startTask(containerId, task.getId(), user);
            								taskClient.completeTask(containerId, task.getId(), user,params);
            								System.out.println("completed task :\n"+task.toString());});
		
		  	}}
}
