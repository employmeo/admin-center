package com.talytica.admin.triggers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.talytica.common.service.ServerAdminService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RespondantPipelineTriggers {
	
	@Autowired
	ServerAdminService serverAdminService;
	
	@Scheduled(initialDelayString = "${jobs.prescreenprediction.trigger.init.seconds:60}000", fixedDelayString = "${jobs.prescreenprediction.trigger.delay.seconds:60}000")
	@Async
	public synchronized void triggerRespondantPreScreen() {
		serverAdminService.triggerPipeline("prescreen");
	}
		
	@Scheduled(initialDelayString = "${jobs.submissionanalysis.trigger.init.seconds:60}000", fixedDelayString = "${jobs.submissionanalysis.trigger.delay.seconds:60}000")
	@Async
	public synchronized void triggerRespondantAssessmentScoring() {
		serverAdminService.triggerPipeline("scoring");
	}

	@Scheduled(initialDelayString = "${jobs.graderscoring.trigger.init.seconds:90}000", fixedDelayString = "${jobs.graderscoring.trigger.delay.seconds:900}000")
	@Async
	public synchronized void triggerGraderCompute() {
		serverAdminService.triggerPipeline("grading");
	}
	
	@Scheduled(initialDelayString = "${jobs.predictions.trigger.init.seconds:90}000", fixedDelayString = "${jobs.predictions.trigger.delay.seconds:900}000")
	@Async
	public synchronized void triggerPredictions() {
		serverAdminService.triggerPipeline("predictions");
	}
}
