package com.example.pamel;

//import org.apache.camel.Exchange;
//import io.jaegertracing.internal.JaegerTracer;
//import io.opentracing.Span;
//import io.opentracing.Tracer;
//import main.java.lib.Tracing;


public class tracedProcessor implements org.apache.camel.Processor{
	
	public tracedProcessor(){
		super();
		}

	@Override
	public void process(org.apache.camel.Exchange ex) throws Exception {
		
		io.jaegertracing.internal.JaegerTracer jtrc = main.java.lib.Tracing.init("pamel_amq");
	       try (io.opentracing.Scope scope = jtrc.buildSpan("ServiceIn").startActive(true)) {
            	main.java.lib.RawInData rid = (main.java.lib.RawInData) ex.getIn().getBody();
                scope.span().setTag("processing_json",""+rid.toString());
	        }
		
	}

}
