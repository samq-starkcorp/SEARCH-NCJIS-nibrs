/*
 * Copyright 2016 Research Triangle Institute
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
package org.search.nibrs.validation.groupa;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.search.nibrs.common.NIBRSError;
import org.search.nibrs.common.ParsedObject;
import org.search.nibrs.common.ReportSource;
import org.search.nibrs.model.ArresteeSegment;
import org.search.nibrs.model.GroupAIncidentReport;
import org.search.nibrs.model.OffenderSegment;
import org.search.nibrs.model.OffenseSegment;
import org.search.nibrs.model.PropertySegment;
import org.search.nibrs.model.VictimSegment;
import org.search.nibrs.model.codes.AggravatedAssaultHomicideCircumstancesCode;
import org.search.nibrs.model.codes.ClearedExceptionallyCode;
import org.search.nibrs.model.codes.NIBRSErrorCode;
import org.search.nibrs.model.codes.OffenseCode;
import org.search.nibrs.model.codes.PropertyDescriptionCode;
import org.search.nibrs.model.codes.RaceCode;
import org.search.nibrs.model.codes.RelationshipOfVictimToOffenderCode;
import org.search.nibrs.model.codes.SexCode;
import org.search.nibrs.model.codes.TypeOfPropertyLossCode;
import org.search.nibrs.model.codes.TypeOfVictimCode;
import org.search.nibrs.validation.rules.Rule;

public class GroupAIncidentReportRulesFactoryTest {
	
	private GroupAIncidentReportRulesFactory rulesFactory = new GroupAIncidentReportRulesFactory();
	
	@Test
	public void testRule560() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule560();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenderSegment offender = new OffenderSegment();
		report.addOffender(offender);
		OffenseSegment offense = new OffenseSegment();
		report.addOffense(offense);
		VictimSegment victim = new VictimSegment();
		report.addVictim(victim);
		offender.setSex(SexCode.F.code);
		victim.setSex(SexCode.F.code);
		offense.setUcrOffenseCode(OffenseCode._11B.code);
		e = rule.apply(report);
		assertNull(e);
		offense.setUcrOffenseCode(OffenseCode._11A.code);
		victim.setSex(SexCode.M.code);
		e = rule.apply(report);
		assertNull(e);
		victim.setSex(SexCode.F.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._560, e.getNIBRSErrorCode());
		assertEquals(victim.getSex(), e.getValue());
		assertEquals("38", e.getDataElementIdentifier());
		OffenderSegment offender2 = new OffenderSegment();
		report.addOffender(offender2);
		offender2.setSex(SexCode.M.code);
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule656() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule656();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		ArresteeSegment as = new ArresteeSegment(ArresteeSegment.GROUP_A_ARRESTEE_SEGMENT_TYPE_IDENTIFIER);
		report.addArrestee(as);
		OffenderSegment os = new OffenderSegment();
		os.setOffenderSequenceNumber(new ParsedObject<>(0));
		report.addOffender(os);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._656, e.getNIBRSErrorCode());
		assertNull(e.getValue());
		assertEquals("L 6", e.getDataElementIdentifier());
		os.setOffenderSequenceNumber(new ParsedObject<>(1));
		e = rule.apply(report);
		assertNull(e);
		as = new ArresteeSegment(ArresteeSegment.GROUP_A_ARRESTEE_SEGMENT_TYPE_IDENTIFIER);
		report.addArrestee(as);
		e = rule.apply(report);
		assertNotNull(e);
	}
	
	@Test
	public void testRule669() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule669();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		ArresteeSegment as = new ArresteeSegment(ArresteeSegment.GROUP_A_ARRESTEE_SEGMENT_TYPE_IDENTIFIER);
		report.addArrestee(as);
		OffenseSegment os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._09A.code);
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		os.setUcrOffenseCode(OffenseCode._09C.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._669, e.getNIBRSErrorCode());
		assertEquals(OffenseCode._09C.code, e.getValue());
		assertEquals("6", e.getDataElementIdentifier());
		report.removeArrestee(0);
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule559() {
		// note that we rely on the more thorough testing in rule 558 to test the shared functionality
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule559();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenderSegment os = new OffenderSegment();
		report.addOffender(os);
		os.setRace(RaceCode.A.code);
		os.setSex(SexCode.F.code);
		os.setAgeString("20  ");
		e = rule.apply(report);
		assertNull(e);
		os.setRace(RaceCode.U.code);
		e = rule.apply(report);
		assertNull(e);
		OffenseSegment offenseSegment = new OffenseSegment();
		offenseSegment.setUcrOffenseCode(OffenseCode._09A.code);
		report.addOffense(offenseSegment);
		e = rule.apply(report);
		assertNull(e);
		offenseSegment.setUcrOffenseCode(OffenseCode._09C.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("39", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._559, e.getNIBRSErrorCode());
		assertEquals(RaceCode.U.code, e.getValue());
	}
	
	@Test
	public void testRule558() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule558();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenderSegment os = new OffenderSegment();
		report.addOffender(os);
		os.setRace(RaceCode.A.code);
		os.setSex(SexCode.F.code);
		os.setAgeString("20  ");
		e = rule.apply(report);
		assertNull(e);
		os.setRace(RaceCode.U.code);
		e = rule.apply(report);
		assertNull(e);
		os.setRace(null);
		e = rule.apply(report);
		assertNull(e);
		report.setExceptionalClearanceCode(ClearedExceptionallyCode.A.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("L 5", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._558, e.getNIBRSErrorCode());
		assertEquals(null, e.getValue());
		os.setRace(RaceCode.U.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertNull(e.getValue());
		os = new OffenderSegment();
		report.addOffender(os);
		os.setRace(RaceCode.A.code);
		os.setSex(SexCode.F.code);
		os.setAgeString("20  ");
		e = rule.apply(report);
		assertNull(e);
		report.removeOffender(1);
		e = rule.apply(report);
		assertNotNull(e);
		os = report.getOffenders().get(0);
		os.setRace(RaceCode.A.code);
		e = rule.apply(report);
		assertNull(e);
		os.setSex(SexCode.U.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("L 5", e.getDataElementIdentifier());
		os.setSex(null);
		e = rule.apply(report);
		assertNotNull(e);
		os.setSex(SexCode.F.code);
		os.setAgeString("00  ");
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("L 5", e.getDataElementIdentifier());
		os.setAgeString(null);
		e = rule.apply(report);
		assertNotNull(e);
		report.setExceptionalClearanceCode(ClearedExceptionallyCode.N.code);
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule555() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule555();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenderSegment os = new OffenderSegment();
		os.setOffenderSequenceNumber(new ParsedObject<>(0));
		report.addOffender(os);
		e = rule.apply(report);
		assertNull(e);
		os = new OffenderSegment();
		os.setOffenderSequenceNumber(new ParsedObject<>(1));
		report.addOffender(os);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("36", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._555, e.getNIBRSErrorCode());
		assertEquals(new Integer(0), e.getValue());
		report.getOffenders().get(0).setOffenderSequenceNumber(new ParsedObject<>(2));
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule480() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule480();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		VictimSegment vs = new VictimSegment();
		report.addVictim(vs);
		vs.setAggravatedAssaultHomicideCircumstances(0, AggravatedAssaultHomicideCircumstancesCode._01.code);
		OffenseSegment os = new OffenseSegment();
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		os = new OffenseSegment();
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		vs.setAggravatedAssaultHomicideCircumstances(1, AggravatedAssaultHomicideCircumstancesCode._08.code);
		e = rule.apply(report);
		assertNull(e);
		report.removeOffense(1);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("31", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._480, e.getNIBRSErrorCode());
		assertEquals(AggravatedAssaultHomicideCircumstancesCode._08.code, e.getValue());
	}
	
	@Test
	public void testRule474() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule474();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		VictimSegment vs = new VictimSegment();
		report.addVictim(vs);
		vs.setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.VO.code);
		vs.setOffenderNumberRelated(0, new ParsedObject<>(1));
		e = rule.apply(report);
		assertNull(e);
		vs = new VictimSegment();
		report.addVictim(vs);
		vs.setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.VO.code);
		vs.setOffenderNumberRelated(0, new ParsedObject<>(2));
		e = rule.apply(report);
		assertNull(e);
		vs.setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.AQ.code);
		vs.setOffenderNumberRelated(0, new ParsedObject<>(1));
		e = rule.apply(report);
		assertNull(e);
		vs.setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.VO.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("35", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._474, e.getNIBRSErrorCode());
		assertEquals(RelationshipOfVictimToOffenderCode.VO.code, e.getValue());
	}
	
	@Test
	public void testRule470() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule470();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenderSegment os = new OffenderSegment();
		report.addOffender(os);
		os = new OffenderSegment();
		report.addOffender(os);
		VictimSegment vs = new VictimSegment();
		report.addVictim(vs);
		vs.setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.VO.code);
		vs = new VictimSegment();
		report.addVictim(vs);
		vs.setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.VO.code);
		e = rule.apply(report);
		assertNull(e);
		report.removeOffender(1);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("35", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._470, e.getNIBRSErrorCode());
		assertEquals(RelationshipOfVictimToOffenderCode.VO.code, e.getValue());
		report.addOffender(os);
		e = rule.apply(report);
		assertNull(e);
		report.removeVictim(1);
		e = rule.apply(report);
		assertNotNull(e);
		report.getVictims().get(0).setVictimOffenderRelationship(0, RelationshipOfVictimToOffenderCode.AQ.code);
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule466() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule466();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenseSegment os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._35A.code);
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		VictimSegment vs = new VictimSegment();
		vs.setUcrOffenseCodeConnection(0, OffenseCode._35A.code);
		report.addVictim(vs);
		e = rule.apply(report);
		assertNull(e);
		vs.setUcrOffenseCodeConnection(0, OffenseCode._35B.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("24", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._466, e.getNIBRSErrorCode());
		assertEquals(OffenseCode._35B.code, e.getValue());
	}
	
	@Test
	public void testRule382() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule382();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenseSegment os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._35A.code);
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		PropertySegment ps = new PropertySegment();
		ps.setPropertyDescription(0, PropertyDescriptionCode._10.code);
		ps.setValueOfProperty(0, ParsedObject.getMissingParsedObject());
		report.addProperty(ps);
		e = rule.apply(report);
		assertNull(e);
		os.setUcrOffenseCode(OffenseCode._35B.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("15", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._382, e.getNIBRSErrorCode());
		assertNull(e.getValue());
		ps.setValueOfProperty(0, new ParsedObject<>(1));
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule268() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule268();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenseSegment os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._13A.code);
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		PropertySegment ps = new PropertySegment();
		ps.setPropertyDescription(0, PropertyDescriptionCode._01.code);
		report.addProperty(ps);
		e = rule.apply(report);
		assertNull(e);
		os.setUcrOffenseCode(OffenseCode._23A.code);
		e = rule.apply(report);
		assertNull(e);
		ps.setPropertyDescription(1, PropertyDescriptionCode._03.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("15", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._268, e.getNIBRSErrorCode());
		assertNull(e.getValue());
		os.setUcrOffenseCode(OffenseCode._13A.code);
		e = rule.apply(report);
		assertNull(e);
		os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._23A.code);
		report.addOffense(os);
		ps = new PropertySegment();
		ps.setPropertyDescription(0, PropertyDescriptionCode._01.code);
		report.addProperty(ps);
		e = rule.apply(report);
		assertNull(e); // ok because there is one non-larceny offense...
	}

	@Test
	public void testRule266() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule266();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenseSegment os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._09C.code);
		report.addOffense(os);
		e = rule.apply(report);
		assertNull(e);
		os = new OffenseSegment();
		os.setUcrOffenseCode(OffenseCode._200.code);
		report.addOffense(os);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("6", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._266, e.getNIBRSErrorCode());
		assertEquals(Collections.singleton(OffenseCode._200.code), e.getValue());
	}
	
	@Test
	public void testRule263() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule263();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		String[] offenseCodes = (String[]) OffenseCode.codeSet().toArray(new String[] {});
		for (int i=0;i < 10;i++) {
			OffenseSegment os = new OffenseSegment();
			report.addOffense(os);
			os.setUcrOffenseCode(offenseCodes[i]);
		}
		e = rule.apply(report);
		assertNull(e);
		OffenseSegment os = new OffenseSegment();
		report.addOffense(os);
		os.setUcrOffenseCode(offenseCodes[11]);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._263, e.getNIBRSErrorCode());
		assertEquals(new Integer(11), e.getValue());
		assertNull(e.getDataElementIdentifier());
	}
	
	@Test
	public void testRule551() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule551();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenderSegment os1 = new OffenderSegment();
		os1.setOffenderSequenceNumber(new ParsedObject<>(1));
		report.addOffender(os1);
		e = rule.apply(report);
		assertNull(e);
		OffenderSegment os2 = new OffenderSegment();
		os2.setOffenderSequenceNumber(new ParsedObject<>(2));
		report.addOffender(os2);
		e = rule.apply(report);
		assertNull(e);
		OffenderSegment os3 = new OffenderSegment();
		os3.setOffenderSequenceNumber(new ParsedObject<>(3));
		report.addOffender(os3);
		e = rule.apply(report);
		assertNull(e);
		os3.setOffenderSequenceNumber(new ParsedObject<>(2));
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("36", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._551, e.getNIBRSErrorCode());
		Set<Integer> dups = new HashSet<>();
		dups.add(new Integer(2));
		assertEquals(dups, e.getValue());
	}
	
	@Test
	public void testRule451() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule451();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		VictimSegment vs1 = new VictimSegment();
		vs1.setVictimSequenceNumber(new ParsedObject<>(1));
		report.addVictim(vs1);
		e = rule.apply(report);
		assertNull(e);
		VictimSegment vs2 = new VictimSegment();
		vs2.setVictimSequenceNumber(new ParsedObject<>(2));
		report.addVictim(vs2);
		e = rule.apply(report);
		assertNull(e);
		VictimSegment vs3 = new VictimSegment();
		vs3.setVictimSequenceNumber(new ParsedObject<>(3));
		report.addVictim(vs3);
		e = rule.apply(report);
		assertNull(e);
		vs3.setVictimSequenceNumber(new ParsedObject<>(2));
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("23", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._451, e.getNIBRSErrorCode());
		Set<Integer> dups = new HashSet<>();
		dups.add(new Integer(2));
		assertEquals(dups, e.getValue());
	}
	
	@Test
	public void testRule376() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule376();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		PropertySegment ps1 = new PropertySegment();
		ps1.setTypeOfPropertyLoss(TypeOfPropertyLossCode._1.code);
		report.addProperty(ps1);
		e = rule.apply(report);
		assertNull(e);
		PropertySegment ps2 = new PropertySegment();
		ps2.setTypeOfPropertyLoss(TypeOfPropertyLossCode._2.code);
		report.addProperty(ps2);
		e = rule.apply(report);
		assertNull(e);
		PropertySegment ps3 = new PropertySegment();
		ps3.setTypeOfPropertyLoss(TypeOfPropertyLossCode._3.code);
		report.addProperty(ps3);
		e = rule.apply(report);
		assertNull(e);
		ps3.setTypeOfPropertyLoss(TypeOfPropertyLossCode._2.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("14", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._376, e.getNIBRSErrorCode());
		Set<String> dups = new HashSet<>();
		dups.add(TypeOfPropertyLossCode._2.code);
		assertEquals(dups, e.getValue());
	}
	
	@Test
	public void testRule084() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule084();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		PropertySegment ps1 = new PropertySegment();
		ps1.setTypeOfPropertyLoss(TypeOfPropertyLossCode._5.code);
		report.addProperty(ps1);
		e = rule.apply(report);
		assertNull(e);
		PropertySegment ps2 = new PropertySegment();
		ps2.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		report.addProperty(ps2);
		e = rule.apply(report);
		assertNull(e);
		ps1.setPropertyDescription(0, "09");
		ps1.setValueOfProperty(0, new ParsedObject<Integer>(4));
		e = rule.apply(report);
		assertNull(e);
		ps2.setPropertyDescription(0, "09");
		ps2.setValueOfProperty(0, new ParsedObject<Integer>(3));
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("15", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._084, e.getNIBRSErrorCode());
		assertEquals("09", e.getValue());
	}
	
	@Test
	public void testRule262() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule262();
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule.apply(report);
		assertNull(e);
		OffenseSegment os1 = new OffenseSegment();
		os1.setUcrOffenseCode(OffenseCode._720.code);
		report.addOffense(os1);
		e = rule.apply(report);
		assertNull(e);
		OffenseSegment os2 = new OffenseSegment();
		os2.setUcrOffenseCode(OffenseCode._200.code);
		report.addOffense(os2);
		e = rule.apply(report);
		assertNull(e);
		OffenseSegment os3 = new OffenseSegment();
		os3.setUcrOffenseCode(OffenseCode._13A.code);
		report.addOffense(os3);
		e = rule.apply(report);
		assertNull(e);
		os3.setUcrOffenseCode(OffenseCode._200.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals("6", e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._262, e.getNIBRSErrorCode());
		Set<String> dups = new HashSet<>();
		dups.add(OffenseCode._200.code);
		assertEquals(dups, e.getValue());
	}
	
	@Test
	public void testRule71() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule071();
		GroupAIncidentReport report = buildBaseReport();
		ArresteeSegment arresteeSegment = new ArresteeSegment(ArresteeSegment.GROUP_A_ARRESTEE_SEGMENT_TYPE_IDENTIFIER);
		report.addArrestee(arresteeSegment);

		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.DECEMBER, 31);
		report.setExceptionalClearanceDate(new ParsedObject<>(c.getTime()));
		NIBRSError nibrsError = rule.apply(report);
		assertNull(nibrsError);
		c.set(2016, Calendar.DECEMBER, 31);
		report.setExceptionalClearanceDate(new ParsedObject<>(c.getTime()));
		nibrsError = rule.apply(report);
		assertNull(nibrsError);
		report.setExceptionalClearanceCode(ClearedExceptionallyCode.N.code);
		nibrsError = rule.apply(report);
		assertNull(nibrsError);
		report.setExceptionalClearanceCode(ClearedExceptionallyCode.A.code);
		nibrsError = rule.apply(report);
		assertNotNull(nibrsError);
		assertEquals(NIBRSErrorCode._071, nibrsError.getNIBRSErrorCode());
		assertEquals("04", nibrsError.getDataElementIdentifier());
		assertEquals("A", nibrsError.getValue());
		assertEquals(report, nibrsError.getReport());
	}
	
	@Test
	public void testRule80() {
		
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule080();
		GroupAIncidentReport report = buildBaseReport();
		
		NIBRSError e = rule.apply(report);
		assertNull(e);
		
		VictimSegment v1 = new VictimSegment();
		v1.setTypeOfVictim(TypeOfVictimCode.B.code);
		report.addVictim(v1);
		VictimSegment v2 = new VictimSegment();
		v2.setTypeOfVictim(TypeOfVictimCode.I.code);
		report.addVictim(v2);
		
		OffenseSegment os1 = new OffenseSegment();
		os1.setUcrOffenseCode(OffenseCode._720.code);
		report.addOffense(os1);
		
		OffenseSegment os2 = new OffenseSegment();
		os2.setUcrOffenseCode(OffenseCode._200.code);
		report.addOffense(os2);
		
		e = rule.apply(report);
		assertNull(e);
		
		report.removeOffense(1);
		
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._080, e.getNIBRSErrorCode());
		
		v1.setTypeOfVictim(TypeOfVictimCode.S.code);
		e = rule.apply(report);
		assertNotNull(e);
		
		v2.setTypeOfVictim(TypeOfVictimCode.S.code);
		e = rule.apply(report);
		assertNotNull(e);
		
		report.removeVictim(1);
		e = rule.apply(report);
		assertNull(e);
		
	}
	
	@Test
	public void testRule75() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule075();
		GroupAIncidentReport report = buildBaseReport();
		report.removeOffenders();
		report.removeOffenses();
		report.removeVictims();
		NIBRSError e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._075, e.getNIBRSErrorCode());
		report.addOffender(new OffenderSegment());
		e = rule.apply(report);
		assertNotNull(e);
		report.addOffense(new OffenseSegment());
		e = rule.apply(report);
		assertNotNull(e);
		report.addVictim(new VictimSegment());
		e = rule.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule76() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule076();
		GroupAIncidentReport report = buildBaseReport();
		OffenseSegment offenseSegment = new OffenseSegment();
		report.addOffense(offenseSegment);
		PropertySegment stolenSegment = new PropertySegment();
		stolenSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		report.addProperty(stolenSegment);
		offenseSegment.setUcrOffenseCode(OffenseCode._100.code);
		NIBRSError e = rule.apply(report);
		assertNull(e);
		offenseSegment.setUcrOffenseCode(OffenseCode._13A.code);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._076, e.getNIBRSErrorCode());
		assertNull(e.getValue());
		assertEquals("L 3", e.getDataElementIdentifier());
	}
	
	@Test
	public void testRule77() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule077();
		GroupAIncidentReport report = buildBaseReport();
		OffenseSegment offenseSegment = new OffenseSegment();
		report.addOffense(offenseSegment);
		PropertySegment stolenSegment = new PropertySegment();
		stolenSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		report.addProperty(stolenSegment);
		offenseSegment.setUcrOffenseCode(OffenseCode._100.code);
		NIBRSError e = rule.apply(report);
		assertNull(e);
		offenseSegment.setOffenseAttemptedCompleted("A");
		
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._077, e.getNIBRSErrorCode());
		assertThat(e.getValue(), is("100"));
		assertEquals("14", e.getDataElementIdentifier());
	}
	
	@Test
	public void testRule78() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule078();
		GroupAIncidentReport report = buildBaseReport();
		OffenseSegment offenseSegment = new OffenseSegment();
		report.addOffense(offenseSegment);
		PropertySegment stolenSegment = new PropertySegment();
		stolenSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		report.addProperty(stolenSegment);
		offenseSegment.setUcrOffenseCode(OffenseCode._290.code);
		NIBRSError e = rule.apply(report);
		assertNull(e);
		offenseSegment.setOffenseAttemptedCompleted("C");
		
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._078, e.getNIBRSErrorCode());
		assertThat(e.getValue(), is("290"));
		assertEquals("14", e.getDataElementIdentifier());
	}
	
	@Test
	public void testRule081() {
		for (OffenseCode offense: OffenseCode.values()){
			switch (offense){
			case _280: 
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(true));
				break; 
			case _100: case _220: case _510:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(false));
				break; 
			case _35A: case _35B:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(true));
				break;
			case _200:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(true));
				break;
			case _250:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(true));
				break;
			case _290:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(true));
				break;
			case _120: case _210: case _240:
			case _270: case _26A: case _26B: 
			case _26C: case _26D: case _26E: 
			case _26G: case _23A: case _23B:
			case _23C: case _23D: case _23E:
			case _23F: case _23G: case _23H:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(true));
				break;
			default:
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._1), is(false));
				assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(false));
				if (OffenseCode.isCrimeAgainstPropertyCode(offense.code) 
						|| OffenseCode.isGamblingOffenseCode(offense.code)){
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(false));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(false));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(false));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(false));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(false));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(false));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._8), is(false));
				}
				else{
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._2), is(true));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._3), is(true));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._4), is(true));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._5), is(true));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._6), is(true));
					assertThat(reject081(offense, "C",  TypeOfPropertyLossCode._7), is(true));
				}
				break;
			}
		}

		for (OffenseCode offense: OffenseCode.values()){
			switch (offense){
			case _100: case _35A: case _200:
			case _510: case _220: case _250:
			case _290: case _270: case _210:
			case _26A: case _26B: case _26C:
			case _26D: case _26E: case _26F:
			case _26G: case _23A: case _23B:
			case _23C: case _23D: case _23E:
			case _23F: case _23G: case _23H:
			case _240: case _120: case _280:
			case _39A: case _39B: case _39C:
			case _39D:
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._1), is(false));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._5), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._8), is(false));
				break;
			default:	
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._1), is(false));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._2), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._3), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._4), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._5), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._6), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._7), is(true));
				assertThat(reject081(offense, "A",  TypeOfPropertyLossCode._8), is(false));
			}
		}
		
	}
	
	private boolean reject081(OffenseCode offenseCode, String offenseAttemptedCompleted,  TypeOfPropertyLossCode propertyLossCode) {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule081();
		GroupAIncidentReport report = buildBaseReport();
		OffenseSegment offenseSegment = new OffenseSegment();
		offenseSegment.setUcrOffenseCode(offenseCode.code);
		offenseSegment.setOffenseAttemptedCompleted(offenseAttemptedCompleted);
		report.addOffense(offenseSegment);
		
		PropertySegment property = new PropertySegment(); 
		property.setTypeOfPropertyLoss(propertyLossCode.code);
		report.addProperty(property);
		
		NIBRSError e = rule.apply(report);
		
		if ( e == null){
			return false; 
		}
		else{
			assertEquals(NIBRSErrorCode._081, e.getNIBRSErrorCode());
			assertEquals(Arrays.asList(propertyLossCode.code), e.getValue());
			assertEquals("14", e.getDataElementIdentifier());
			return true;
		}
		
	}

	@Test
	public void testRule74() {
		Set<String> testCodes = new HashSet<>();
		testCodes.add(OffenseCode._100.code);
		testCodes.add(OffenseCode._35A.code);
		testCodes.add(OffenseCode._39A.code);
		testCodes.add(OffenseCode._220.code);
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule074();
		GroupAIncidentReport report = buildBaseReport();
		OffenseSegment offenseSegment = new OffenseSegment();
		report.addOffense(offenseSegment);
		PropertySegment stolenSegment = new PropertySegment();
		stolenSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		report.addProperty(stolenSegment);
		NIBRSError e = null;
		for (String offenseCode : testCodes) {
			offenseSegment.setUcrOffenseCode(offenseCode);
			e = rule.apply(report);
			assertNull(e);
		}
		report.removeProperties();
		for (String offenseCode : testCodes) {
			offenseSegment.setUcrOffenseCode(offenseCode);
			e = rule.apply(report);
			assertNotNull(e);
			assertEquals(NIBRSErrorCode._074, e.getNIBRSErrorCode());
			assertThat(e.getValue(), is(offenseCode));
			assertEquals("L 3", e.getDataElementIdentifier());
		}
	}
	
	@Test
	public void testRule73() {
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule073();
		GroupAIncidentReport report = buildBaseReport();
		report.setReportActionType('I');
		PropertySegment stolenSegment = new PropertySegment();
		stolenSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		PropertySegment recoveredSegment = new PropertySegment();
		report.addProperty(recoveredSegment);
		recoveredSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._5.code);
		NIBRSError e = rule.apply(report);
		assertNull(e);
		recoveredSegment.setNumberOfRecoveredMotorVehicles(new ParsedObject<>(5));
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._073, e.getNIBRSErrorCode());
		assertEquals(recoveredSegment.getNumberOfRecoveredMotorVehicles().getValue(), e.getValue());
		stolenSegment.setNumberOfStolenMotorVehicles(new ParsedObject<>(4));
		report.addProperty(stolenSegment);
		e = rule.apply(report);
		assertNotNull(e);
		stolenSegment.setNumberOfStolenMotorVehicles(new ParsedObject<>(5));
		e = rule.apply(report);
		assertNull(e);
		stolenSegment.setNumberOfStolenMotorVehicles(new ParsedObject<>(6));
		e = rule.apply(report);
		assertNull(e);
		
		stolenSegment.setNumberOfStolenMotorVehicles(new ParsedObject<>());
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._073, e.getNIBRSErrorCode());
		assertEquals(recoveredSegment.getNumberOfRecoveredMotorVehicles().getValue(), e.getValue());
		
		
	}
	
	@Test
	public void testRule72() {
		
		Rule<GroupAIncidentReport> rule = rulesFactory.getRule072();
		GroupAIncidentReport report = buildBaseReport();
		PropertySegment stolenSegment = new PropertySegment();
		stolenSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._7.code);
		PropertySegment recoveredSegment = new PropertySegment();
		report.addProperty(recoveredSegment);
		recoveredSegment.setTypeOfPropertyLoss(TypeOfPropertyLossCode._5.code);
		
		OffenseSegment offenseSegment = new OffenseSegment();
		report.addOffense(offenseSegment);
		offenseSegment.setUcrOffenseCode(OffenseCode._250.code);
		recoveredSegment.setPropertyDescription(0, PropertyDescriptionCode._21.code);
		
		NIBRSError e = rule.apply(report);
		assertNull(e);
		
		recoveredSegment.setPropertyDescription(0, PropertyDescriptionCode._17.code);
		recoveredSegment.setPropertyDescription(1, PropertyDescriptionCode._19.code);
		offenseSegment.setUcrOffenseCode(OffenseCode._220.code);
		
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(null, e.getDataElementIdentifier());
		assertEquals(NIBRSErrorCode._072, e.getNIBRSErrorCode());
		assertEquals(null, e.getValue());
		
		stolenSegment.setPropertyDescription(0, PropertyDescriptionCode._17.code);
		report.addProperty(stolenSegment);
		e = rule.apply(report);
		assertNotNull(e);
		assertEquals(null, e.getValue());

		stolenSegment.setPropertyDescription(1, PropertyDescriptionCode._19.code);
		e = rule.apply(report);
		assertNull(e);
		
		recoveredSegment.setPropertyDescription(1, null);
		stolenSegment.setPropertyDescription(1, null);
		
		recoveredSegment.setPropertyDescription(0, PropertyDescriptionCode._38.code);
		stolenSegment.setPropertyDescription(0, PropertyDescriptionCode._04.code);
		
		e = rule.apply(report);
		assertNotNull(e);
		
		stolenSegment.setPropertyDescription(0, PropertyDescriptionCode._03.code);
		e = rule.apply(report);
		assertNull(e);

	}
	
	@Test
	public void testRule101() {
		
		Rule<GroupAIncidentReport> rule101 = rulesFactory.getRule101("ori", "1");
		GroupAIncidentReport report = buildBaseReport();
		NIBRSError e = rule101.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._101, e.getNIBRSErrorCode());
		assertNull(e.getValue());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getSource(), e.getContext());
		assertEquals("1", e.getDataElementIdentifier());
		report.setOri("Not blank");
		e = rule101.apply(report);
		assertNull(e);
		
		rule101 = rulesFactory.getRule101("incidentNumber", "2");
		report = buildBaseReport();
		e = rule101.apply(report);
		assertNotNull(e);
		assertEquals("2", e.getDataElementIdentifier());
		report.setIncidentNumber("Not blank");
		e = rule101.apply(report);
		assertNull(e);
		
		rule101 = rulesFactory.getRule101("exceptionalClearanceCode", "4");
		report = buildBaseReport();
		report.setExceptionalClearanceCode("K");
		e = rule101.apply(report);
		assertNotNull(e);
		assertEquals("4", e.getDataElementIdentifier());
		report.setExceptionalClearanceCode("A");
		e = rule101.apply(report);
		assertNull(e);
		
	}
	
	@Test
	public void testRule104ForCargoTheftIndicator() {
		
		Rule<GroupAIncidentReport> rule104 = rulesFactory.getRule104("cargoTheftIndicator");
		GroupAIncidentReport report = buildBaseReport();
		
		report.setIncludesCargoTheft(false);
		report.setCargoTheftIndicator(null);
		NIBRSError e = rule104.apply(report);
		assertNull(e);
		
		report.setIncludesCargoTheft(true);
		report.setCargoTheftIndicator("Y");
		e = rule104.apply(report);
		assertNull(e);
		
		report.setCargoTheftIndicator("X");
		e = rule104.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._104, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getCargoTheftIndicator(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
		
	}
	
	@Test
	public void testRule104ForMonthOfTape() {
		Rule<GroupAIncidentReport> rule104 = rulesFactory.getRule104("monthOfTape");
		GroupAIncidentReport report = buildBaseReport();
		report.setMonthOfTape(3);
		NIBRSError e = rule104.apply(report);
		assertNull(e);
		report.setMonthOfTape(null);
		e = rule104.apply(report);
		assertNull(e);
		report.setMonthOfTape(15);
		e = rule104.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._104, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getMonthOfTape(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule104ForYearOfTape() {
		Rule<GroupAIncidentReport> rule104 = rulesFactory.getRule104("yearOfTape");
		GroupAIncidentReport report = buildBaseReport();
		report.setYearOfTape(2012);
		NIBRSError e = rule104.apply(report);
		assertNull(e);
		report.setYearOfTape(null);
		e = rule104.apply(report);
		assertNull(e);
		report.setYearOfTape(1925);
		e = rule104.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._104, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getYearOfTape(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule104ForReportDateIndicator() {
		Rule<GroupAIncidentReport> rule104 = rulesFactory.getRule104("reportDateIndicator");
		GroupAIncidentReport report = buildBaseReport();
		report.setReportDateIndicator("R");
		NIBRSError e = rule104.apply(report);
		assertNull(e);
		report.setReportDateIndicator(null);
		e = rule104.apply(report);
		assertNull(e);
		report.setReportDateIndicator("X");
		e = rule104.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._104, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getReportDateIndicator(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule115() {
		Rule<GroupAIncidentReport> rule115 = rulesFactory.getRule115();
		GroupAIncidentReport report = buildBaseReport();
		report.setIncidentNumber("");
		NIBRSError e = rule115.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._115, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getIncidentNumber(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
		report.setIncidentNumber("A");
		assertNull(rule115.apply(report));
		report.setIncidentNumber("A B         ");
		assertNotNull(rule115.apply(report));
		report.setIncidentNumber(" AB         ");
		assertNotNull(rule115.apply(report));
		report.setIncidentNumber("AB         ");
		assertNull(rule115.apply(report));
		report.setIncidentNumber("AB           ");
		assertNotNull(rule115.apply(report));
		report.setIncidentNumber("AB          ");
		assertNull(rule115.apply(report));
		report.setIncidentNumber("ABBBBBBBBBBB");
		assertNull(rule115.apply(report));
	}
	
	@Test
	public void testRule117() {
		Rule<GroupAIncidentReport> rule117 = rulesFactory.getRule117();
		GroupAIncidentReport report = buildBaseReport();
		report.setIncidentNumber("11-123-SC   ");
		NIBRSError e = rule117.apply(report);
		assertNull(e);
		report.setIncidentNumber("11+123*SC   ");
		e = rule117.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._117, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getIncidentNumber(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
		report.setIncidentNumber("11-123-SC");
		e = rule117.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule119() {
		Rule<GroupAIncidentReport> rule119 = rulesFactory.getRule119();
		GroupAIncidentReport report = buildBaseReport();
		report.setCargoTheftIndicator("Yes");
		OffenseSegment o = new OffenseSegment();
		report.addOffense(o);
		o.setUcrOffenseCode(OffenseCode._35A.code);
		NIBRSError e = rule119.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._119, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getCargoTheftIndicator(), "Yes");
		assertEquals(report.getSource(), e.getContext());
		o = new OffenseSegment();
		report.addOffense(o);
		o.setUcrOffenseCode(OffenseCode._120.code);
		e = rule119.apply(report);
		assertNull(e);
		report.setCargoTheftIndicator("No");
		e = rule119.apply(report);
		assertNull(e);
	}
	
	@Test
	public void testRule152() {
		Rule<GroupAIncidentReport> rule152 = rulesFactory.getRule152();
		GroupAIncidentReport report = buildBaseReport();
		report.setIncidentHour(new ParsedObject<Integer>(2));
		NIBRSError e = rule152.apply(report);
		assertNull(e);
		report.setIncidentHour(new ParsedObject<Integer>(null));
		e = rule152.apply(report);
		assertNull(e);
		report.setIncidentHour(new ParsedObject<Integer>(30));
		e = rule152.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._152, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getIncidentHour().getValue(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}

	@Test
	public void testRule170() {
		Rule<GroupAIncidentReport> rule170 = rulesFactory.getRule170();
		GroupAIncidentReport report = buildBaseReport();
		report.setYearOfTape(2015);
		report.setMonthOfTape(12);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.JANUARY);
		c.set(Calendar.DAY_OF_MONTH, 1);
		report.setIncidentDate(ParsedObject.getMissingParsedObject());
		NIBRSError e = rule170.apply(report);
		assertNull(e);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		e = rule170.apply(report);
		assertNull(e);
		c.set(Calendar.YEAR, 2016);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		e = rule170.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._170, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getIncidentDate().getValue(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule171() {
		Rule<GroupAIncidentReport> rule171 = rulesFactory.getRule171();
		GroupAIncidentReport report = buildBaseReport();
		report.setYearOfTape(2015);
		report.setMonthOfTape(12);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.MARCH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		report.setIncidentDate(ParsedObject.getMissingParsedObject());
		NIBRSError e = rule171.apply(report);
		assertNull(e);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		e = rule171.apply(report);
		assertNull(e);
		c.set(Calendar.YEAR, 2013);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		e = rule171.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._171, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getIncidentDate().getValue(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule172() {
		Rule<GroupAIncidentReport> rule172 = rulesFactory.getRule172();
		GroupAIncidentReport report = buildBaseReport();
		report.setYearOfTape(2015);
		report.setMonthOfTape(12);
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.MARCH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		report.setIncidentDate(ParsedObject.getMissingParsedObject());
		NIBRSError e = rule172.apply(report);
		assertNull(e);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		e = rule172.apply(report);
		assertNull(e);
		c.set(Calendar.YEAR, 1990);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		e = rule172.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._172, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getIncidentDate().getValue(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule153() {
		Rule<GroupAIncidentReport> rule153 = rulesFactory.getRule153();
		GroupAIncidentReport report = buildBaseReport();
		report.setExceptionalClearanceCode("N");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.MARCH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		report.setExceptionalClearanceDate(ParsedObject.getMissingParsedObject());
		assertNull(rule153.apply(report));
		report.setExceptionalClearanceDate(new ParsedObject<>(c.getTime()));
		NIBRSError e = rule153.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._153, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getExceptionalClearanceCode(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule155() {
		Rule<GroupAIncidentReport> rule155 = rulesFactory.getRule155();
		GroupAIncidentReport report = buildBaseReport();
		report.setExceptionalClearanceCode("A");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.MARCH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		report.setExceptionalClearanceDate(ParsedObject.getMissingParsedObject());
		assertNull(rule155.apply(report));
		report.setExceptionalClearanceDate(new ParsedObject<>(c.getTime()));
		report.setIncidentDate(ParsedObject.getMissingParsedObject());
		assertNull(rule155.apply(report));
		c.set(Calendar.MONTH, Calendar.APRIL);
		report.setIncidentDate(new ParsedObject<>(c.getTime()));
		NIBRSError e = rule155.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._155, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getExceptionalClearanceDate().getValue(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
	}
	
	@Test
	public void testRule156() {
		Rule<GroupAIncidentReport> rule156 = rulesFactory.getRule156();
		GroupAIncidentReport report = buildBaseReport();
		report.setExceptionalClearanceCode("A");
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2015);
		c.set(Calendar.MONTH, Calendar.MARCH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		report.setExceptionalClearanceDate(new ParsedObject<>(c.getTime()));
		assertNull(rule156.apply(report));
		report.setExceptionalClearanceDate(ParsedObject.getMissingParsedObject());
		NIBRSError e = rule156.apply(report);
		assertNotNull(e);
		assertEquals(NIBRSErrorCode._156, e.getNIBRSErrorCode());
		assertEquals(GroupAIncidentReport.ADMIN_SEGMENT_TYPE_IDENTIFIER, e.getSegmentType());
		assertEquals(report.getExceptionalClearanceCode(), e.getValue());
		assertEquals(report.getSource(), e.getContext());
		assertEquals(report, e.getReport());
	}
	
	private GroupAIncidentReport buildBaseReport() {
		GroupAIncidentReport report = new GroupAIncidentReport();
		ReportSource source = new ReportSource();
		source.setSourceLocation(getClass().getName());
		source.setSourceName(getClass().getName());
		report.setSource(source);
		return report;
	}
}
