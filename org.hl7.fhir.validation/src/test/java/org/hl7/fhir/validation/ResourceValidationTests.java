package org.hl7.fhir.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.r5.formats.XmlParser;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.test.utils.TestingUtilities;
import org.hl7.fhir.utilities.FhirPublication;
import org.hl7.fhir.utilities.i18n.I18nConstants;
import org.hl7.fhir.utilities.settings.FhirSettings;
import org.hl7.fhir.utilities.validation.ValidationMessage;
import org.hl7.fhir.validation.instance.InstanceValidator;
import org.hl7.fhir.validation.tests.utilities.TestUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResourceValidationTests {


  private static IWorkerContext ctxt;
  private static ValidationEngine engine;
  private static InstanceValidator val;


  private List<ValidationMessage> runTest(String filename) throws IOException, FileNotFoundException, Exception {
    TestingUtilities.injectCorePackageLoader();
    if (val == null) {
      ctxt = TestingUtilities.getSharedWorkerContext();
      engine = TestUtilities.getValidationEngine("hl7.fhir.r5.core#5.0.0", FhirSettings.getTxFhirDevelopment(), null, FhirPublication.R5, true, "5.0.0");
      val = engine.getValidator(null);
      val.setDebug(false);
    }
    List<ValidationMessage> errors = new ArrayList<>();
    Resource res = (Resource) new XmlParser().parse(TestingUtilities.loadTestResourceStream("r5", filename));
    val.validate(val, errors, res);
    Assertions.assertNotNull(errors);
    return errors;
  }


  @Test
  public void testObservation() throws Exception {
    runTest("observation-example.xml");
  }

  @Test
  public void testPatient() throws Exception {
    runTest("patient-example.xml");
  }

  @Test
  public void testPatientGlossy() throws Exception {
    runTest("patient-glossy-example.xml");
  }

  @Test
  public void testPatientPeriod() throws Exception {
    runTest("patient-example-period.xml");
  }

  @Test
  public void testPatientXds() throws Exception {
    runTest("patient-example-xds.xml");
  }

  @Test
  public void testQuestionnaire() throws Exception {
    runTest("questionnaire-example.xml");
  }

  @Test
  public void testQuestionnaireLifelines() throws Exception {
    runTest("questionnaire-example-f201-lifelines.xml");
  }

  @Test
  public void testValueSet() throws Exception {
    runTest("valueset-example-expansion.xml");
  }
  
  @Test
  public void testParameters() throws Exception {
    runTest("parameters-example.xml");
  }

  @Test
  public void testParametersTypes() throws Exception {
    runTest("parameters-example-types.xml");
  }

  @Test
  public void testObservation2() throws Exception {
    runTest("observation-example-20minute-apgar-score.xml");
  }

  @Test
  public void testDispense() throws Exception {
    runTest("medicationdispenseexample8.xml");
  }

  @Test
  public void testDispense2() throws Exception {
    runTest("medicationdispense8.xml");
  }


  @Test
  public void testList() throws Exception {
    runTest("list-example-long.xml");
  }

  @Test
  public void testCondition() throws Exception {
    runTest("condition-example.xml");
  }


  @Test
  public void testCodesystem() throws Exception {
    runTest("codesystem-example.xml");
  }

}