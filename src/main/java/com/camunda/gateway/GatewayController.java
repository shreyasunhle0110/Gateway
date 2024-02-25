package com.camunda.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class GatewayController {
	private static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

	@Autowired
	ZeebeClient zeebe;

	@PostMapping("/startworkflow")
	public ResponseEntity<?> startWorkflowController(@RequestBody PropertyEvaluationRequest request) {
		try {
			logger.info("Starting workflow for request: {}", request);

			final ProcessInstanceResult result = zeebe.newCreateInstanceCommand().bpmnProcessId("BankPropertyEvalution")
					.latestVersion().variable("ParentRequest", request).withResult().send().join();

			logger.info("Workflow started successfully with BPMN process ID: {}", result.getBpmnProcessId());
			return ResponseEntity.ok(result.getBpmnProcessId());
		} catch (Exception e) {
			logger.error("Failed to start workflow", e);
			return ResponseEntity.status(401).body("Failed to start workflow");
		}
	}
}