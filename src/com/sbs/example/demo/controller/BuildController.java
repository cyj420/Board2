package com.sbs.example.demo.controller;

import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.BuildService;

public class BuildController extends Controller {
	private BuildService buildService;

	public BuildController() {
		buildService = Factory.getBuildService();
	}

	@Override
	public void doAction(Request reqeust) {
		if (reqeust.getActionName().equals("site")) {
			actionSite(reqeust);
		}
	}

	private void actionSite(Request reqeust) {
		buildService.buildSite();
	}
}