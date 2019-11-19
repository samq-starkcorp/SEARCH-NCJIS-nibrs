
/*
 * Copyright 2016 SEARCH-The National Consortium for Justice Information and Statistics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.search.nibrs.stagingdata.service.summary;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.search.nibrs.model.reports.cargotheft.CargoTheftFormRow;
import org.search.nibrs.model.reports.cargotheft.CargoTheftReport;
import org.search.nibrs.stagingdata.AppProperties;
import org.search.nibrs.stagingdata.model.Agency;
import org.search.nibrs.stagingdata.repository.AgencyRepository;
import org.search.nibrs.stagingdata.service.AdministrativeSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoTheftReportService {

	private final Log log = LogFactory.getLog(this.getClass());
	@Autowired
	AdministrativeSegmentService administrativeSegmentService;
	@Autowired
	public AgencyRepository agencyRepository; 
	@Autowired
	public AppProperties appProperties; 

	public CargoTheftReport createCargoTheftReport(String ori, Integer year,  Integer month ) {
		
		CargoTheftReport cargoTheftReport = new CargoTheftReport(ori, year, month); 
		
		if (!"StateWide".equalsIgnoreCase(ori)){
			Agency agency = agencyRepository.findFirstByAgencyOri(ori); 
			if (agency!= null){
				cargoTheftReport.setAgencyName(agency.getAgencyName());
				cargoTheftReport.setStateName(agency.getStateName());
				cargoTheftReport.setStateCode(agency.getStateCode());
				cargoTheftReport.setPopulation(agency.getPopulation());
			}
			else{
				return cargoTheftReport; 
			}
		}
		else{
			cargoTheftReport.setAgencyName(ori);
			cargoTheftReport.setStateName("");
			cargoTheftReport.setStateCode("");
			cargoTheftReport.setPopulation(null);
		}
		processCargoTheftIncident(ori, year, month, cargoTheftReport);
		log.debug("cargoTheftReport: " + cargoTheftReport);
		return cargoTheftReport;
	}

	private void processCargoTheftIncident(String ori, Integer year, Integer month, CargoTheftReport cargoTheftReport) {
		List<CargoTheftFormRow> cargoTheftRows = 
				administrativeSegmentService.findCargoTheftRowsByOriAndIncidentDate(ori, year, month);
		cargoTheftReport.setCargoTheftRows(cargoTheftRows);
	}


}
