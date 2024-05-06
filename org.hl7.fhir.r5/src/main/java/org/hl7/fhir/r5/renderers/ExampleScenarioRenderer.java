package org.hl7.fhir.r5.renderers;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.context.ContextUtilities;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.model.ExampleScenario.*;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.KnownLinkType;
import org.hl7.fhir.utilities.xhtml.XhtmlDocument;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleScenarioRenderer extends TerminologyRenderer {

  public ExampleScenarioRenderer(RenderingContext context) {
    super(context);
  }
  
  public boolean render(XhtmlNode x, Resource scen) throws IOException {
    return render(x, (ExampleScenario) scen);
  }

  public boolean render(XhtmlNode x, ExampleScenario scen) throws FHIRException {
    try {
      if (context.getScenarioMode() == null) {
        return renderActors(x, scen);
      } else {
        switch (context.getScenarioMode()) {
        case ACTORS:
          return renderActors(x, scen);
        case INSTANCES:
          return renderInstances(x, scen);
        case PROCESSES:
          return renderProcesses(x, scen);
        default:
          throw new FHIRException(context.formatMessage(RenderingContext.EX_SCEN_UN, context.getScenarioMode()) + " ");
        }
      }
    } catch (Exception e) {
      throw new FHIRException(context.formatMessage(RenderingContext.EX_SCEN_ERR_REN, scen.getUrl(), e) + " ");
    }
  }

  public String renderDiagram(ExampleScenario scen) throws IOException {
    String plantUml = toPlantUml(scen);
    SourceStringReader reader = new SourceStringReader(plantUml);
    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    reader.outputImage(os, new FileFormatOption(FileFormat.SVG));
    os.close();

    final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
    return svg;
  }

  protected String toPlantUml(ExampleScenario scen) throws IOException {
    String plantUml = "@startuml\r\n";
    plantUml += "Title " + (scen.hasTitle() ? scen.getTitle() : scen.getName()) + "\r\n\r\n";
    Map<String, String> actorKeys = new HashMap<String, String>();

    for (ExampleScenarioActorComponent actor: scen.getActor()) {
      String actorType = actor.getType().equals(Enumerations.ExampleScenarioActorType.PERSON) ? "actor" : "participant";
      actorKeys.put(actor.getKey(), escapeKey(actor.getKey()));
      plantUml += actorType + " \"" + creolLink(actor.getTitle(), "#a_" + actor.getKey(), actor.getDescription()) + "\" as " + actorKeys.get(actor.getKey()) + "\r\n";
    }
    plantUml += "\r\n";

    int processNum = 1;
    for (ExampleScenarioProcessComponent process: scen.getProcess()) {
      plantUml += toPlantUml(process, Integer.toString(processNum), scen, actorKeys);
      processNum++;
    }
    plantUml += "@enduml";

    return plantUml;
  }

  private String escapeKey(String origKey) {
    char[] chars = origKey.toCharArray();
    for (int i=0; i<chars.length; i++) {
      char c = chars[i];
      if (!((c>='A' && c<='Z') || (c>='a' && c<='z') || (c>='0' && c<='9') || c=='@' || c=='.'))
        chars[i] = '_';
    }
    return new String(chars);
  }

  protected String toPlantUml(ExampleScenarioProcessComponent process, String prefix, ExampleScenario scen, Map<String, String> actorKeys) throws IOException {
    String plantUml = "group " + process.getTitle() + " " + creolLink("details", "#p_" + prefix, process.getDescription()) + "\r\n";

    Map<String,Boolean> actorsActive = new HashMap<String, Boolean>();
    for (ExampleScenarioActorComponent actor : scen.getActor()) {
      actorsActive.put(actor.getKey(), Boolean.FALSE);
    }
    int stepCount = 1;
    for (ExampleScenarioProcessStepComponent step: process.getStep()) {
      plantUml += toPlantUml(step, stepPrefix(prefix, step, stepCount), scen, actorsActive, actorKeys);
      if (step.getPause())
        plantUml += context.formatMessage(RenderingContext.EX_SCEN_TIME);
      stepCount++;
    }

    plantUml += "end\r\n\r\n";
    return plantUml;
  }

  protected String toPlantUml(ExampleScenarioProcessStepComponent step, String prefix, ExampleScenario scen, Map<String,Boolean> actorsActive, Map<String, String> actorKeys) throws IOException {
    String plantUml = "";
    if (step.hasWorkflow()) {
      XhtmlNode n = new XhtmlDocument();
      renderCanonical(scen, n, step.getWorkflow());
      XhtmlNode ref = n.getChildNodes().get(0);
      plantUml += noteOver(scen.getActor(), /*!#*/"Step " + trimPrefix(prefix) + " - See scenario\n" + creolLink(ref.getContent(), ref.getAttribute("href")));
    } else if (step.hasProcess())
      plantUml += toPlantUml(step.getProcess(), prefix, scen, actorKeys);
    else {
      // Operation
      plantUml += toPlantUml(step.getOperation(), prefix, scen, actorsActive, actorKeys);
    }

    return plantUml;
  }

  protected String toPlantUml(ExampleScenarioProcessStepOperationComponent op, String prefix, ExampleScenario scen, Map<String,Boolean> actorsActive, Map<String, String> actorKeys) {
    StringBuilder plantUml = new StringBuilder();
    plantUml.append(handleActivation(op.getInitiator(), op.getInitiatorActive(), actorsActive, actorKeys));
    plantUml.append(handleActivation(op.getReceiver(), op.getReceiverActive(), actorsActive, actorKeys));
    plantUml.append(actorKeys.get(op.getInitiator()) + " -> " + actorKeys.get(op.getReceiver()) + ": ");
    plantUml.append(creolLink(op.getTitle(), "#s_" + prefix, op.getDescription()));
    if (op.hasRequest()) {
      plantUml.append(" (" + creolLink("payload", linkForInstance(op.getRequest())) + ")\r\n");
    }
    if (op.hasResponse()) {
      plantUml.append("activate " + actorKeys.get(op.getReceiver()) + "\r\n");
      plantUml.append(actorKeys.get(op.getReceiver()) + " --> " + actorKeys.get(op.getInitiator()) + ": ");
      plantUml.append(creolLink("response", "#s_" + prefix, op.getDescription()));
      plantUml.append(" (" + creolLink("payload", linkForInstance(op.getResponse())) + ")\r\n");
      plantUml.append("deactivate " + actorKeys.get(op.getReceiver()) + "\r\n");
    }
    plantUml.append(handleDeactivation(op.getInitiator(), op.getInitiatorActive(), actorsActive, actorKeys));
    plantUml.append(handleDeactivation(op.getReceiver(), op.getReceiverActive(), actorsActive, actorKeys));

    return plantUml.toString();
  }

  private String handleActivation(String actorId, boolean active, Map<String,Boolean> actorsActive, Map<String, String> actorKeys) {
    String plantUml = "";
    Boolean actorWasActive = actorsActive.get(actorId);
    if (active && !actorWasActive) {
      plantUml += "activate " + actorKeys.get(actorId) + "\r\n";
    }
    return plantUml;
  }

  private String handleDeactivation(String actorId, boolean active, Map<String,Boolean> actorsActive, Map<String, String> actorKeys) {
    String plantUml = "";
    Boolean actorWasActive = actorsActive.get(actorId);
    if (!active && actorWasActive) {
      plantUml += "deactivate " + actorKeys.get(actorId) + "\r\n";
    }
    if (active != actorWasActive) {
      actorsActive.remove(actorId);
      actorsActive.put(actorId, Boolean.valueOf(active));
    }
    return plantUml;
  }

  private String linkForInstance(ExampleScenarioInstanceContainedInstanceComponent ref) {
    String plantUml = "#i_" + ref.getInstanceReference();
    if (ref.hasVersionReference())
        plantUml += "v_" + ref.getVersionReference();
    return plantUml;
  }

  private String trimPrefix(String prefix){
    return prefix.substring(prefix.lastIndexOf(".") + 1);
  }

  private String noteOver(List<ExampleScenarioActorComponent> actors, String text) {
    String plantUml = "Note over ";
    List actorKeys = new ArrayList<String>();
    for (ExampleScenarioActorComponent actor: actors) {
      actorKeys.add(actor.getKey());
    }
    plantUml += String.join(", ", actorKeys);
    plantUml += " " + text;
    return plantUml;
  }

  private String creolLink(String text, String url) {
    return creolLink(text, url, null);
  }

  private String creolLink(String text, String url, String flyover) {
    String s = "[[" + url;
    if (flyover!=null)
      s += "{" + flyover + "}";
    s += " " + text + "]]";
    return s;
  }

  public boolean renderActors(XhtmlNode x, ExampleScenario scen) throws IOException {
    XhtmlNode tbl = x.table("table-striped table-bordered");
    XhtmlNode thead = tbl.tr();
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_NAME));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_TYPE));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_DESC));
    for (ExampleScenarioActorComponent actor : scen.getActor()) {
      XhtmlNode tr = tbl.tr();
      XhtmlNode nameCell = tr.td();
      nameCell.an("a_" + actor.getKey());
      nameCell.tx(actor.getTitle());
      tr.td().tx(actor.getType().getDisplay());
      addMarkdown(tr.td().style("overflow-wrap:break-word"), actor.getDescription());
    }
    return true;
  }

  public boolean renderInstances(XhtmlNode x, ExampleScenario scen) throws IOException {
    XhtmlNode tbl = x.table("table-striped table-bordered");
    XhtmlNode thead = tbl.tr();
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_NAME));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_TYPE));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_CONT));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_DESC));

    Map<String, String> instanceNames = new HashMap<String, String>();
    for (ExampleScenarioInstanceComponent instance : scen.getInstance()) {
      instanceNames.put("i_" + instance.getKey(), instance.getTitle());
      if (instance.hasVersion()) {
        for (ExampleScenarioInstanceVersionComponent version: instance.getVersion()) {
          instanceNames.put("i_" + instance.getKey() + "v_" + version.getKey(), version.getTitle());
        }
      }
    }

    for (ExampleScenarioInstanceComponent instance : scen.getInstance()) {
      XhtmlNode row = tbl.tr();
      XhtmlNode nameCell = row.td();
      nameCell.an("i_" + instance.getKey());
      nameCell.tx(instance.getTitle());
      XhtmlNode typeCell = row.td();
      if (instance.hasVersion())
        typeCell.attribute("rowSpan", Integer.toString(instance.getVersion().size()+1));

      if (!instance.hasStructureVersion() || instance.getStructureType().getSystem().equals("")) {
        if (instance.hasStructureVersion())
          typeCell.tx((context.formatMessage(RenderingContext.EX_SCEN_FVER, instance.getStructureVersion()) + " ") + " ");
        if (instance.hasStructureProfile()) {
          renderCanonical(scen, typeCell, instance.getStructureProfile().toString());
        } else {
          renderCanonical(scen, typeCell, "http://hl7.org/fhir/StructureDefinition/" + instance.getStructureType().getCode());
        }
      } else {
          render(typeCell, instance.getStructureVersionElement());
          typeCell.tx(" "+(context.formatMessage(RenderingContext.EX_SCEN_VER, instance.getStructureVersion())+" "));
        if (instance.hasStructureProfile()) {
          typeCell.tx(" ");
          renderCanonical(scen, typeCell, instance.getStructureProfile().toString());
        }
      }
      if (instance.hasContent() && instance.getContent().hasReference()) {
        // Force end-user mode to avoid ugly references
        RenderingContext.ResourceRendererMode mode = context.getMode();
        context.setMode(RenderingContext.ResourceRendererMode.END_USER);
        renderReference(scen, row.td(), instance.getContent().copy().setDisplay("here"));
        context.setMode(mode);
      } else
        row.td();

      XhtmlNode descCell = row.td();
      addMarkdown(descCell, instance.getDescription());
      if (instance.hasContainedInstance()) {
        descCell.b().tx(context.formatMessage(RenderingContext.EX_SCEN_CONTA) + " ");
        int containedCount = 1;
        for (ExampleScenarioInstanceContainedInstanceComponent contained: instance.getContainedInstance()) {
          String key = "i_" + contained.getInstanceReference();
          if (contained.hasVersionReference())
            key += "v_" + contained.getVersionReference();
          String description = instanceNames.get(key);
          if (description==null)
            throw new FHIRException("Unable to find contained instance " + key + " under " + instance.getKey());
          descCell.ah("#" + key).tx(description);
          containedCount++;
          if (instance.getContainedInstance().size() > containedCount)
            descCell.tx(", ");
        }
      }

      for (ExampleScenarioInstanceVersionComponent version: instance.getVersion()) {
        row = tbl.tr();
        nameCell = row.td().style("padding-left: 10px;");
        nameCell.an("i_" + instance.getKey() + "v_" + version.getKey());
        XhtmlNode nameItem = nameCell.ul().li();
        nameItem.tx(version.getTitle());

        if (version.hasContent() && version.getContent().hasReference()) {
          // Force end-user mode to avoid ugly references
          RenderingContext.ResourceRendererMode mode = context.getMode();
          context.setMode(RenderingContext.ResourceRendererMode.END_USER);
          renderReference(scen, row.td(), version.getContent().copy().setDisplay("here"));
          context.setMode(mode);
        } else
          row.td();

        descCell = row.td();
        addMarkdown(descCell, instance.getDescription());
      }
    }
    return true;
  }

  public boolean renderProcesses(XhtmlNode x, ExampleScenario scen) throws IOException {
    Map<String, ExampleScenarioActorComponent> actors = new HashMap<>();
    for (ExampleScenarioActorComponent actor: scen.getActor()) {
      actors.put(actor.getKey(), actor);
    }

    Map<String, ExampleScenarioInstanceComponent> instances = new HashMap<>();
    for (ExampleScenarioInstanceComponent instance: scen.getInstance()) {
      instances.put(instance.getKey(), instance);
    }

    int num = 1;
    for (ExampleScenarioProcessComponent process : scen.getProcess()) {
      renderProcess(x, process, Integer.toString(num), actors, instances);
      num++;
    }
    return true;
  }

  public void renderProcess(XhtmlNode x, ExampleScenarioProcessComponent process, String prefix, Map<String, ExampleScenarioActorComponent> actors, Map<String, ExampleScenarioInstanceComponent> instances) throws IOException {
    XhtmlNode div = x.div();
    div.an("p_" + prefix);
    div.b().tx(context.formatMessage(RenderingContext.EX_SCEN_PROC, process.getTitle())+" ");
    if (process.hasDescription())
      addMarkdown(div, process.getDescription());
    if (process.hasPreConditions()) {
      div.para().b().i().tx(context.formatMessage(RenderingContext.EX_SCEN_PRECON));
      addMarkdown(div, process.getPreConditions());
    }
    if (process.hasPostConditions()) {
      div.para().b().i().tx(context.formatMessage(RenderingContext.EX_SCEN_POSTCON));
      addMarkdown(div, process.getPostConditions());
    }
    XhtmlNode tbl = div.table("table-striped table-bordered").style("width:100%");
    XhtmlNode thead = tbl.tr();
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_STEP));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_NAME));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_DESC));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_IN));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_REC));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_REQ));
    thead.th().addText(context.formatMessage(RenderingContext.EX_SCEN_RES));
    int stepCount = 1;
    for (ExampleScenarioProcessStepComponent step: process.getStep()) {
      renderStep(tbl, step, stepPrefix(prefix, step, stepCount), actors, instances);
      stepCount++;
    }

    // Now go through the steps again and spit out any child processes
    stepCount = 1;
    for (ExampleScenarioProcessStepComponent step: process.getStep()) {
      stepSubProcesses(tbl, step, stepPrefix(prefix, step, stepCount), actors, instances);
      stepCount++;
    }
  }

  private String stepPrefix(String prefix, ExampleScenarioProcessStepComponent step, int stepCount) {
    String num = step.hasNumber() ? step.getNumber() : Integer.toString(stepCount);
    return (!prefix.isEmpty() ? prefix + "." : "") + num;
  }

  private String altStepPrefix(String prefix, ExampleScenarioProcessStepComponent step, int altNum, int stepCount) {
    return stepPrefix(prefix + "-Alt" + Integer.toString(altNum) + ".", step, stepCount);
  }

  private void stepSubProcesses(XhtmlNode x, ExampleScenarioProcessStepComponent step, String prefix, Map<String, ExampleScenarioActorComponent> actors, Map<String, ExampleScenarioInstanceComponent> instances) throws IOException {
    if (step.hasProcess())
      renderProcess(x, step.getProcess(), prefix, actors, instances);
    if (step.hasAlternative()) {
      int altNum = 1;
      for (ExampleScenarioProcessStepAlternativeComponent alt: step.getAlternative()) {
        int stepCount = 1;
        for (ExampleScenarioProcessStepComponent altStep: alt.getStep()) {
          stepSubProcesses(x, altStep, altStepPrefix(prefix, altStep, altNum, stepCount), actors, instances);
          stepCount++;
        }
        altNum++;
      }
    }
  }

  private boolean renderStep(XhtmlNode tbl, ExampleScenarioProcessStepComponent step, String stepLabel, Map<String, ExampleScenarioActorComponent> actors, Map<String, ExampleScenarioInstanceComponent> instances) throws IOException {
    XhtmlNode row = tbl.tr();
    XhtmlNode prefixCell = row.td();
    prefixCell.an("s_" + stepLabel);
    prefixCell.tx(stepLabel.substring(stepLabel.indexOf(".") + 1));
    if (step.hasProcess()) {
      XhtmlNode n = row.td().colspan(6);
      n.tx(context.formatMessage(RenderingContext.EX_SCEN_SEE));
      n.ah("#p_" + stepLabel, step.getProcess().getTitle());
      n.tx(" "+ context.formatMessage(RenderingContext.EX_SCEN_BEL));

    } else if (step.hasWorkflow()) {
      XhtmlNode n = row.td().colspan(6);
      n.tx(context.formatMessage(RenderingContext.EX_SCEN_OTH));
      String link = new ContextUtilities(context.getWorker()).getLinkForUrl(context.getLink(KnownLinkType.SPEC), step.getWorkflow());
      n.ah(link, step.getProcess().getTitle());

    } else {
      // Must be an operation
      ExampleScenarioProcessStepOperationComponent op = step.getOperation();
      XhtmlNode name = row.td();
      name.tx(op.getTitle());
      if (op.hasType()) {
        name.tx(" - ");
        renderCoding(name, op.getType());
      }
      XhtmlNode descCell = row.td();
      addMarkdown(descCell, op.getDescription());

      addActor(row, op.getInitiator(), actors);
      addActor(row, op.getReceiver(), actors);
      addInstance(row, op.getRequest(), instances);
      addInstance(row, op.getResponse(), instances);
    }

    int altNum = 1;
    for (ExampleScenarioProcessStepAlternativeComponent alt : step.getAlternative()) {
      XhtmlNode altHeading = tbl.tr().colspan(7).td();
      altHeading.para().i().tx(context.formatMessage(RenderingContext.EX_SCEN_ALT, alt.getTitle())+" ");
      if (alt.hasDescription())
        addMarkdown(altHeading, alt.getDescription());
      int stepCount = 1;
      for (ExampleScenarioProcessStepComponent subStep : alt.getStep()) {
        renderStep(tbl, subStep, altStepPrefix(stepLabel, step, altNum, stepCount), actors, instances);
        stepCount++;
      }
      altNum++;
    }

    return true;
  }

  private void addActor(XhtmlNode row, String actorId, Map<String, ExampleScenarioActorComponent> actors) throws FHIRException {
    XhtmlNode actorCell = row.td();
    if (actorId==null)
      return;
    ExampleScenarioActorComponent actor = actors.get(actorId);
    if (actor==null)
      throw new FHIRException(context.formatMessage(RenderingContext.EX_SCEN_UN_ACT, actorId)+" ");
    actorCell.ah("#a_" + actor.getKey(), actor.getDescription()).tx(actor.getTitle());
  }

  private void addInstance(XhtmlNode row, ExampleScenarioInstanceContainedInstanceComponent instanceRef, Map<String, ExampleScenarioInstanceComponent> instances) {
    XhtmlNode instanceCell =  row.td();
    if (instanceRef==null || instanceRef.getInstanceReference()==null)
      return;
    ExampleScenarioInstanceComponent instance = instances.get(instanceRef.getInstanceReference());
    if (instance==null)
      throw new FHIRException(context.formatMessage(RenderingContext.EX_SCEN_UN_INST, instanceRef.getInstanceReference())+" ");
    if (instanceRef.hasVersionReference()) {
      ExampleScenarioInstanceVersionComponent theVersion = null;
      for (ExampleScenarioInstanceVersionComponent version: instance.getVersion()) {
        if (version.getKey().equals(instanceRef.getVersionReference())) {
          theVersion = version;
          break;
        }
      }
      if (theVersion==null)
        throw new FHIRException(/*!#*/"Unable to find referenced version " + instanceRef.getVersionReference() + " within instance " + instanceRef.getInstanceReference());
      instanceCell.ah("#i_" + instance.getKey() + "v_"+ theVersion.getKey() , theVersion.getDescription()).tx(theVersion.getTitle());

    } else
      instanceCell.ah("#i_" + instance.getKey(), instance.getDescription()).tx(instance.getTitle());
  }
}
