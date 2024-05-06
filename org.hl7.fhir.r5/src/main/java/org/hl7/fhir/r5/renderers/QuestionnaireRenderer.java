package org.hl7.fhir.r5.renderers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.context.ContextUtilities;
import org.hl7.fhir.r5.model.CanonicalResource;
import org.hl7.fhir.r5.model.CanonicalType;
import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Expression;
import org.hl7.fhir.r5.model.Extension;
import org.hl7.fhir.r5.model.Questionnaire;
import org.hl7.fhir.r5.model.Questionnaire.QuestionnaireItemAnswerOptionComponent;
import org.hl7.fhir.r5.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.r5.model.Questionnaire.QuestionnaireItemEnableWhenComponent;
import org.hl7.fhir.r5.model.Questionnaire.QuestionnaireItemInitialComponent;
import org.hl7.fhir.r5.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.hl7.fhir.r5.model.ValueSet;
import org.hl7.fhir.r5.model.ValueSet.ValueSetExpansionContainsComponent;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.GenerationRules;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.KnownLinkType;
import org.hl7.fhir.r5.terminologies.expansion.ValueSetExpansionOutcome;
import org.hl7.fhir.r5.utils.ToolingExtensions;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.xhtml.HierarchicalTableGenerator;
import org.hl7.fhir.utilities.xhtml.HierarchicalTableGenerator.Cell;
import org.hl7.fhir.utilities.xhtml.HierarchicalTableGenerator.Piece;
import org.hl7.fhir.utilities.xhtml.HierarchicalTableGenerator.Row;
import org.hl7.fhir.utilities.xhtml.HierarchicalTableGenerator.TableModel;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

import javax.annotation.Nonnull;

public class QuestionnaireRenderer extends TerminologyRenderer {
  public static final String EXT_QUESTIONNAIRE_ITEM_TYPE_ORIGINAL = "http://hl7.org/fhir/4.0/StructureDefinition/extension-Questionnaire.item.type";

  public QuestionnaireRenderer(RenderingContext context) {
    super(context);
  }
  
  public boolean render(XhtmlNode x, Resource q) throws UnsupportedEncodingException, IOException {
    return render(x, (Questionnaire) q);
  }
  
  public boolean render(XhtmlNode x, Questionnaire q) throws UnsupportedEncodingException, IOException {
    switch (context.getQuestionnaireMode()) {
    case FORM:  return renderForm(x, q);
    case LINKS: return renderLinks(x, q);
    case LOGIC: return renderLogic(x, q);
    case DEFNS: return renderDefns(x, q);
    case TREE:  return renderTree(x, q);
    default:
      throw new Error("Unknown Questionnaire Renderer Mode");
    }
  }
  
  public boolean renderTree(XhtmlNode x, Questionnaire q) throws UnsupportedEncodingException, IOException {
    boolean hasFlags = checkForFlags(q.getItem());
    boolean doOpts = context.getDefinitionsTarget() == null && hasAnyOptions(q.getItem()); 

    if (doOpts) {
      x.b().tx(context.formatMessage(RenderingContext.QUEST_STRUCT));
    }
    HierarchicalTableGenerator gen = new HierarchicalTableGenerator(context, context.getDestDir(), context.isInlineGraphics(), true);
    TableModel model = gen.new TableModel("qtree="+q.getId(), context.getRules() == GenerationRules.IG_PUBLISHER);    
    model.setAlternating(true);
    if (context.getRules() == GenerationRules.VALID_RESOURCE || context.isInlineGraphics()) {
      model.setDocoImg(HierarchicalTableGenerator.help16AsData());    
    } else {
      model.setDocoImg(Utilities.pathURL(context.getLink(KnownLinkType.SPEC), "help16.png"));
    }
    model.setDocoRef(context.getLink(KnownLinkType.SPEC)+"formats.html#table");
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), (context.formatMessage(RenderingContext.QUEST_LINKID)), (context.formatMessage(RenderingContext.QUEST_LINK)), null, 0));
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), (context.formatMessage(RenderingContext.QUEST_TEXT)), (context.formatMessage(RenderingContext.QUEST_TEXTFOR)), null, 0));
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), (context.formatMessage(RenderingContext.QUEST_CARD)), (context.formatMessage(RenderingContext.QUEST_TIMES)), null, 0));
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), (context.formatMessage(RenderingContext.QUEST_TYPE)), (context.formatMessage(RenderingContext.QUEST_TYPE_ITEM)), null, 0));
    if (hasFlags) {
      model.getTitles().add(gen.new Title(null, model.getDocoRef(), (context.formatMessage(RenderingContext.QUEST_FLAG)), (context.formatMessage(RenderingContext.QUEST_ATTRIBUTES)), null, 0));
    }
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), (context.formatMessage(RenderingContext.QUEST_DESC)), (context.formatMessage(RenderingContext.QUEST_ADD_INFO)), null, 0));

    boolean hasExt = false;
    // first we add a root for the questionaire itself
    Row row = addTreeRoot(gen, model.getRows(), q, hasFlags);
    for (QuestionnaireItemComponent i : q.getItem()) {
      hasExt = renderTreeItem(gen, row.getSubRows(), q, i, hasFlags) || hasExt;
    }
    XhtmlNode xn = gen.generate(model, context.getLocalPrefix(), 1, null);
    x.getChildNodes().add(xn);
    if (doOpts) {
      renderOptions(q, x);
    }
    return hasExt;
  }

  private void renderOptions(Questionnaire q, XhtmlNode x) {
    if (hasAnyOptions(q.getItem())) {
      x.hr();
      x.para().b().tx(context.formatMessage(RenderingContext.QUEST_OPT));
      renderOptions(q.getItem(), x);
    }    
  }

  private void renderOptions(List<QuestionnaireItemComponent> items, XhtmlNode x) {    
    for (QuestionnaireItemComponent i : items) {
      renderItemOptions(x, i);
      renderOptions(i.getItem(), x);
    }    
  }

  public void renderItemOptions(XhtmlNode x, QuestionnaireItemComponent i) {
    if (i.hasAnswerOption()) {
      boolean useSelect = false;
      for (QuestionnaireItemAnswerOptionComponent opt : i.getAnswerOption()) {
        useSelect = useSelect || opt.getInitialSelected(); 
      }
      x.an("opt-item."+i.getLinkId());
      x.para().b().tx(context.formatMessage(RenderingContext.QUEST_ANSW, i.getLinkId())+" ");
      XhtmlNode ul = x.ul();
      for (QuestionnaireItemAnswerOptionComponent opt : i.getAnswerOption()) {
        XhtmlNode li = ul.li();
        li.style("font-size: 11px");
        if (useSelect) {
          if (opt.getInitialSelected()) {
            li.img("icon-selected.png", "icon");
          } else {
            li.img("icon-not-selected.png", "icon");            
          }
        }
        if (opt.getValue().isPrimitive()) {
          li.tx(opt.getValue().primitiveValue());
        } else if (opt.getValue() instanceof Coding) {
          Coding c = (Coding) opt.getValue(); 
          String link = c.hasSystem() ? new ContextUtilities(context.getWorker()).getLinkForUrl(context.getLink(KnownLinkType.SPEC), c.getSystem()) : null;
          if (link == null) {
            li.tx(c.getSystem()+"#"+c.getCode());
          } else {
            li.ah(link).tx(displaySystem(c.getSystem()));
            li.tx(": "+c.getCode());              
          }
          if (c.hasDisplay()) {
            li.tx(" (\""+c.getDisplay()+"\")");              
          }
        } else {
          li.tx("??");            
        }
      }
    }
  }

  private boolean hasAnyOptions(List<QuestionnaireItemComponent> items) {
    for (QuestionnaireItemComponent i : items) {
      if (i.hasAnswerOption()) {
        return true;
      }
      if (hasAnyOptions(i.getItem())) {
        return true;
      }
    }
    return false;
  }

  private boolean checkForFlags(List<QuestionnaireItemComponent> items) {
    for (QuestionnaireItemComponent i : items) {
      if (checkForFlags(i)) {
        return true;
      }
    }
    return false;
  }

  private boolean checkForFlags(QuestionnaireItemComponent i) {
    if (i.getReadOnly()) {
      return true;
    }
    if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_IS_SUBJ)) {
      return true;
    }
    if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_HIDDEN)) {
      return true;
    }
    if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_OTP_DISP)) {
      return true;
    }
    if (i.hasExtension(ToolingExtensions.EXT_O_LINK_PERIOD)) {
      return true;
    }
    if (i.hasExtension(ToolingExtensions.EXT_Q_CHOICE_ORIENT)) {
      return true;
    }
    if (i.hasExtension(ToolingExtensions.EXT_Q_DISPLAY_CAT)) {
      return true;
    }
    return checkForFlags(i.getItem());
  }
    


  private Row addTreeRoot(HierarchicalTableGenerator gen, List<Row> rows, Questionnaire q, boolean hasFlags) throws IOException {
    Row r = gen.new Row();
    rows.add(r);

    r.setIcon("icon_q_root.gif", context.formatMessage(RenderingContext.QUEST_ROOT));
    r.getCells().add(gen.new Cell(null, null, q.getName(), null, null));
    r.getCells().add(gen.new Cell(null, null, q.getDescription(), null, null));
    r.getCells().add(gen.new Cell(null, null, "", null, null));
    r.getCells().add(gen.new Cell(null, null, context.formatMessage(RenderingContext.QUEST_QUEST), null, null));
    if (hasFlags) {
      r.getCells().add(gen.new Cell(null, null, "", null, null));
    }
    r.getCells().add(gen.new Cell(null, null, q.hasUrl() ? q.hasVersion() ? q.getUrl()+"#"+q.getVersion() : q.getUrl() : "", null, null));
    return r;    
  }
  
  private String getSpecLink(String path) {
    return Utilities.pathURL(context.getLink(KnownLinkType.SPEC), path);
  }

  private String getSDCLink(String url, String path) {
    StructureDefinition sd = context.getContext().fetchResource(StructureDefinition.class, url);
    if (sd == null) {
      sd = context.getContext().fetchResource(StructureDefinition.class, path);
    }
    if (sd != null && sd.hasWebPath()) {
      return sd.getWebPath();
    } else if (Utilities.isAbsoluteUrl(path)) {
      return path.replace("StructureDefinition/", "StructureDefinition-")+".html";
    } else {
      return Utilities.pathURL("http://hl7.org/fhir/uv/sdc", path); // for now?
    }
  }

  private boolean renderTreeItem(HierarchicalTableGenerator gen, List<Row> rows, Questionnaire q, QuestionnaireItemComponent i, boolean hasFlags) throws IOException {
    Row r = gen.new Row();
    rows.add(r);
    boolean hasExt = false;

    r.setIcon("icon-q-"+i.getType().toCode().toLowerCase()+".png", i.getType().getDisplay());
    Cell c1 = gen.new Cell(null, context.getDefinitionsTarget() == null ? "" : context.getDefinitionsTarget()+"#item."+i.getLinkId(), i.getLinkId(), null, null);
    c1.setId("item."+i.getLinkId());
    r.getCells().add(c1);
    String txt = (i.hasPrefix() ? i.getPrefix() + ". " : "") + i.getText();
    r.getCells().add(gen.new Cell(null, null, txt, null, null));
    r.getCells().add(gen.new Cell(null, null, (i.getRequired() ? "1" : "0")+".."+(i.getRepeats() ? "*" : "1"), null, null));
    if (i.getTypeElement().hasExtension(EXT_QUESTIONNAIRE_ITEM_TYPE_ORIGINAL)) {
      String t = i.getTypeElement().getExtensionString(EXT_QUESTIONNAIRE_ITEM_TYPE_ORIGINAL);
      r.getCells().add(gen.new Cell(null, context.getLink(KnownLinkType.SPEC)+"codesystem-item-type.html#item-type-"+t, t, null, null));
    } else {
      r.getCells().add(gen.new Cell(null, context.getLink(KnownLinkType.SPEC)+"codesystem-item-type.html#item-type-"+i.getType().toCode(), i.getType().toCode(), null, null));
    }

    if (hasFlags) {
      // flags:
      Cell flags = gen.new Cell();
      r.getCells().add(flags);
      if (i.getReadOnly()) {
        flags.addPiece(gen.new Piece(Utilities.pathURL(context.getLink(KnownLinkType.SPEC), "questionnaire-definitions.html#Questionnaire.item.readOnly"), null, context.formatMessage(RenderingContext.QUEST_READONLY)).addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-readonly.png"))));
      }
      if (ToolingExtensions.readBoolExtension(i, "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-isSubject")) {
        flags.addPiece(gen.new Piece(getSDCLink("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-isSubject", "StructureDefinition-sdc-questionnaire-isSubject.html"), null, context.formatMessage(RenderingContext.QUEST_SUBJECT)).addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-subject.png"))));
      }
      if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_HIDDEN)) {
        flags.addPiece(gen.new Piece(getSpecLink("extension-questionnaire-hidden.html"), null, context.formatMessage(RenderingContext.QUEST_HIDDEN)).addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-hidden.png"))));
      }
      if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_OTP_DISP)) {
        flags.addPiece(gen.new Piece(getSDCLink("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-optionalDisplay", "StructureDefinition-sdc-questionnaire-optionalDisplay.html"), null, context.formatMessage(RenderingContext.QUEST_DISPLAY)).addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-optional.png"))));
      }
      if (i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod")) {
        flags.addPiece(gen.new Piece(getSDCLink("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod", "StructureDefinition-sdc-questionnaire-observationLinkPeriod.html"), null, context.formatMessage(RenderingContext.QUEST_LINKED)).addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-observation.png"))));
      }
      if (i.hasExtension(ToolingExtensions.EXT_Q_CHOICE_ORIENT)) {
        String code = ToolingExtensions.readStringExtension(i,  ToolingExtensions.EXT_Q_CHOICE_ORIENT);
        flags.addPiece(gen.new Piece(getSpecLink("extension-questionnaire-choiceorientation.html"), null, context.formatMessage(RenderingContext.QUEST_ORIENTATION, code)+" ").addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-" + code + ".png"))));
      }
      if (i.hasExtension(ToolingExtensions.EXT_Q_DISPLAY_CAT)) {
        CodeableConcept cc = i.getExtensionByUrl(ToolingExtensions.EXT_Q_DISPLAY_CAT).getValueCodeableConcept();
        String code = cc.getCode("http://hl7.org/fhir/questionnaire-display-category");
        flags.addPiece(gen.new Piece("https://hl7.org/fhir/R4/extension-questionnaire-displayCategory.html", null, context.formatMessage(RenderingContext.QUEST_CAT, code)+" ").addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", getImgPath("icon-qi-" + code + ".png"))));
      }
    }    
    Cell defn = gen.new Cell();
    r.getCells().add(defn);

    if (i.hasMaxLength()) {
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_MAX)+" "), null));
      defn.getPieces().add(gen.new Piece(null, Integer.toString(i.getMaxLength()), null));
    }
    if (i.hasDefinition()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_DEF)+" "), null));
      genDefinitionLink(gen, i, defn, q);      
    }
    if (i.hasEnableWhen()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      Piece p = gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_ENABLE)+" "), null);
      defn.getPieces().add(p);
      if (i.getEnableWhen().size() == 1) {
        XhtmlNode x = new XhtmlNode(NodeType.Element, "span");
        p.getChildren().add(x);
        renderEnableWhen(x, i.getEnableWhenFirstRep());        
      } else {
        XhtmlNode x = new XhtmlNode(NodeType.Element, "ul");
        p.getChildren().add(x);
        for (QuestionnaireItemEnableWhenComponent qi : i.getEnableWhen()) {
          renderEnableWhen(x.li(), qi);
        }
      }
    }
    if (i.hasAnswerValueSet()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_VALUE)+" "), null));
      if (!Utilities.noString(i.getAnswerValueSet()) && i.getAnswerValueSet().startsWith("#")) {
        ValueSet vs = (ValueSet) q.getContained(i.getAnswerValueSet().substring(1));
        if (vs == null) {
          defn.getPieces().add(gen.new Piece(null, i.getAnswerValueSet(), null));                    
        } else {
          defn.getPieces().add(gen.new Piece(vs.getWebPath(), vs.present(), null));                              
        }
      } else {
        ValueSet vs = context.getWorker().findTxResource(ValueSet.class, i.getAnswerValueSet(), q);
        if (vs == null  || !vs.hasWebPath()) {
          defn.getPieces().add(gen.new Piece(null, i.getAnswerValueSet(), null));                    
        } else {
          defn.getPieces().add(gen.new Piece(vs.getWebPath(), vs.present(), null));                    
        }             
      }
    }
    if (i.hasAnswerOption()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_OPTIONS)+" "), null));
      if (context.getDefinitionsTarget() == null) {
        // if we don't have a definitions target, we'll add them below. 
        defn.getPieces().add(gen.new Piece("#opt-item."+i.getLinkId(), Integer.toString(i.getAnswerOption().size())+" "+Utilities.pluralize("option", i.getAnswerOption().size()), null));
      } else {
        defn.getPieces().add(gen.new Piece(context.getDefinitionsTarget()+"#item."+i.getLinkId(), Integer.toString(i.getAnswerOption().size())+" "+Utilities.pluralize("option", i.getAnswerOption().size()), null));
      }
    }
    if (i.hasInitial()) {
      for (QuestionnaireItemInitialComponent v : i.getInitial()) {
        if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
        defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_INITIAL)+" "), null));
        defn.getPieces().add(gen.new Piece(null, v.getValue().fhirType(), null));
        defn.getPieces().add(gen.new Piece(null, " = ", null));
        if (v.getValue().isPrimitive()) {
          defn.getPieces().add(gen.new Piece(null, v.getValue().primitiveValue(), null));
        } else if (v.hasValueCoding()) {
          renderCoding(gen, defn.getPieces(), v.getValueCoding());          
        } else if (v.hasValueQuantity()) {
          renderQuantity(gen, defn.getPieces(), v.getValueQuantity(), false);        
        } else if (v.hasValueReference()) {
          renderReference(q, gen, defn.getPieces(), v.getValueReference(), true);       
        } else if (v.hasValueAttachment()) {
          // renderAttachment(gen, defn.getPieces(), v.getValueAttachment());          
        }
      }
    }
    // still todo

//
//http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn
//
//http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-width
//http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod
//http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl
//http://hl7.org/fhir/StructureDefinition/questionnaire-sliderStepValue
    
    if (i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression")) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_EXP)+" "), null));
      Piece p = gen.new Piece("ul");
      defn.getPieces().add(p);
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_INT), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_CONT), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_ITEM_CONT), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_EN), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_CALC), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_CAND), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression");
      } 
    }

    for (QuestionnaireItemComponent c : i.getItem()) {
      hasExt = renderTreeItem(gen, r.getSubRows(), q, c, hasFlags) || hasExt;
    }
    return hasExt;    
  }

  public void genDefinitionLink(HierarchicalTableGenerator gen, QuestionnaireItemComponent i, Cell defn, Questionnaire q) {
    // can we resolve the definition? 
    String path = null;
    String d = i.getDefinition();
    if (d.contains("#")) {
      path = d.substring(d.indexOf("#")+1);
      d = d.substring(0, d.indexOf("#"));
    }
    StructureDefinition sd = context.getWorker().fetchResource(StructureDefinition.class, d, q);
    if (sd != null) {
      String url = sd.getWebPath();
      if (url != null) {
        defn.getPieces().add(gen.new Piece(url+"#"+path, path, null));          
      } else {
        defn.getPieces().add(gen.new Piece(null, i.getDefinition(), null));
      }
    } else {
      defn.getPieces().add(gen.new Piece(null, i.getDefinition(), null));
    }
  }

  public void genDefinitionLink(XhtmlNode x, QuestionnaireItemComponent i, Questionnaire q) {
    // can we resolve the definition? 
    String path = null;
    String d = i.getDefinition();
    if (d.contains("#")) {
      path = d.substring(d.indexOf("#")+1);
      d = d.substring(0, d.indexOf("#"));
    }
    StructureDefinition sd = context.getWorker().fetchResource(StructureDefinition.class, d, q);
    if (sd != null) {
      String url = sd.getWebPath();
      if (url != null) {
        x.ah(url+"#"+path).tx(path);          
      } else {
        x.tx(i.getDefinition());
      }
    } else {
      x.tx(i.getDefinition());
    }
  }

  private void addExpression(Piece p, Expression exp, String label, String url) {
    XhtmlNode x = new XhtmlNode(NodeType.Element, "li").style("font-size: 11px");
    p.addHtml(x);
    CanonicalResource cr = (CanonicalResource) context.getContext().fetchResource(Resource.class, url);
    if (cr != null && cr.hasWebPath()) {
      x.ah(cr.getWebPath()).tx(label);
    } else {
      x.ah(url).tx(label);
    }
    x.tx(": ");
    x.code(exp.getExpression());
  }

  private boolean renderLogic(XhtmlNode x, Questionnaire q) throws FHIRException, IOException {
    HierarchicalTableGenerator gen = new HierarchicalTableGenerator(context, context.getDestDir(), context.isInlineGraphics(), true);
    TableModel model = gen.new TableModel("qtree="+q.getId(), true);    
    model.setAlternating(true);
    if (context.getRules() == GenerationRules.VALID_RESOURCE || context.isInlineGraphics()) {
      model.setDocoImg(HierarchicalTableGenerator.help16AsData());    
    } else {
      model.setDocoImg(Utilities.pathURL(context.getLink(KnownLinkType.SPEC), "help16.png"));
    }
    model.setDocoRef(context.getLink(KnownLinkType.SPEC)+"formats.html#table");
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), context.formatMessage(RenderingContext.QUEST_LINKID), context.formatMessage(RenderingContext.QUEST_LINK), null, 0));
    model.getTitles().add(gen.new Title(null, model.getDocoRef(), context.formatMessage(RenderingContext.QUEST_DESC), context.formatMessage(RenderingContext.QUEST_ADD_INFO), null, 0));

    boolean hasExt = false;
    if (!q.hasItem()) {
      gen.emptyRow(model, 2);
    } else {
      for (QuestionnaireItemComponent i : q.getItem()) {
        hasExt = renderLogicItem(gen, model.getRows(), q, i) || hasExt;
      }
    }
    XhtmlNode xn = gen.generate(model, context.getLocalPrefix(), 1, null);
    x.getChildNodes().add(xn);
    return hasExt;  
  }

  private boolean renderLogicItem(HierarchicalTableGenerator gen, List<Row> rows, Questionnaire q, QuestionnaireItemComponent i) throws IOException {
    Row r = gen.new Row();
    rows.add(r);
    boolean hasExt = false;

    r.setIcon("icon-q-"+i.getType().toCode().toLowerCase()+".png", i.getType().getDisplay());
    r.getCells().add(gen.new Cell(null, context.getDefinitionsTarget() == null ? "" : context.getDefinitionsTarget()+"#item."+i.getLinkId(), i.getLinkId(), null, null));
    Cell defn = gen.new Cell();
    r.getCells().add(defn);

    if (i.hasMaxLength()) {
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_MAX)+" "), null));
      defn.getPieces().add(gen.new Piece(null, Integer.toString(i.getMaxLength()), null));
    }
    if (i.hasDefinition()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_DEF)+" "), null));
      genDefinitionLink(gen, i, defn, q);            
    }
    if (i.hasEnableWhen()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_ENABLE)+" "), null));
      defn.getPieces().add(gen.new Piece(null, context.formatMessage(RenderingContext.QUEST_TODO), null));      
    }
    if (i.hasAnswerValueSet()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_VALUE)+" "), null));
      if (Utilities.noString(i.getAnswerValueSet()) && i.getAnswerValueSet().startsWith("#")) {
        ValueSet vs = (ValueSet) q.getContained(i.getAnswerValueSet().substring(1));
        if (vs == null) {
          defn.getPieces().add(gen.new Piece(null, i.getAnswerValueSet(), null));                    
        } else {
          defn.getPieces().add(gen.new Piece(vs.getWebPath(), vs.present(), null));                              
        }
      } else {
        ValueSet vs = context.getWorker().findTxResource(ValueSet.class, i.getAnswerValueSet(), q);
        if (vs == null  || !vs.hasWebPath()) {
          defn.getPieces().add(gen.new Piece(null, i.getAnswerValueSet(), null));                    
        } else {
          defn.getPieces().add(gen.new Piece(vs.getWebPath(), vs.present(), null));                    
        }             
      }
    }
    if (i.hasAnswerOption()) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_OPTIONS)+" "), null));
      defn.getPieces().add(gen.new Piece(context.getDefinitionsTarget()+"#item."+i.getLinkId(), Integer.toString(i.getAnswerOption().size())+" "+Utilities.pluralize("option", i.getAnswerOption().size()), null));            
    }
    if (i.hasInitial()) {
      for (QuestionnaireItemInitialComponent v : i.getInitial()) {
        if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
        defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_INITIAL)+" "), null));
        defn.getPieces().add(gen.new Piece(null, v.getValue().fhirType(), null));
        defn.getPieces().add(gen.new Piece(null, " = ", null));
        if (v.getValue().isPrimitive()) {
          defn.getPieces().add(gen.new Piece(null, v.getValue().primitiveValue(), null));
        } else if (v.hasValueCoding()) {
          renderCoding(gen, defn.getPieces(), v.getValueCoding());          
        } else if (v.hasValueQuantity()) {
          renderQuantity(gen, defn.getPieces(), v.getValueQuantity(), false);          
        } else if (v.hasValueReference()) {
          renderReference(q, gen, defn.getPieces(), v.getValueReference(), false);          
//        } else if (v.hasValueAttachment()) {
//          renderAttachment(gen, defn.getPieces(), v.getValueAttachment());          
        }
      }
    }
    // still todo

//
//http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-choiceColumn
//
//http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-width
//http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod
//http://hl7.org/fhir/StructureDefinition/questionnaire-itemControl
//http://hl7.org/fhir/StructureDefinition/questionnaire-sliderStepValue
    
    if (i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression")) {
      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
      defn.getPieces().add(gen.new Piece(null, (context.formatMessage(RenderingContext.QUEST_EXP)+" "), null));
      Piece p = gen.new Piece("ul");
      defn.getPieces().add(p);
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_INT), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_CONT), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_ITEM_CONT), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_EN), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_CALC), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression");
      }
      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression")) {
        addExpression(p, e.getValueExpression(), context.formatMessage(RenderingContext.QUEST_CAND), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression");
      } 
    }

    for (QuestionnaireItemComponent c : i.getItem()) {
      hasExt = renderLogicItem(gen, r.getSubRows(), q, c) || hasExt;
    }
    return hasExt;
    
  }


  public boolean renderForm(XhtmlNode x, Questionnaire q) throws UnsupportedEncodingException, IOException {
    boolean hasExt = false;
    XhtmlNode d = x.div();
    boolean hasPrefix = false;
    for (QuestionnaireItemComponent c : q.getItem()) {
      hasPrefix = hasPrefix || doesItemHavePrefix(c);
    }
    int i = 1;
    for (QuestionnaireItemComponent c : q.getItem()) {
      hasExt = renderFormItem(d, q, c, hasPrefix ? null : Integer.toString(i), 0) || hasExt;
      i++;
    }
    return hasExt; 
  }

  private boolean doesItemHavePrefix(QuestionnaireItemComponent i) {
    if (i.hasPrefix()) {
      return true;
    }
    for (QuestionnaireItemComponent c : i.getItem()) {
      if (doesItemHavePrefix(c)) {
        return true;
      }
    }
    return false;
  }

  private boolean renderFormItem(XhtmlNode x, Questionnaire q, QuestionnaireItemComponent i, String pfx, int indent) throws IOException {
    boolean hasExt = false;
    XhtmlNode d = x.div().style("width: "+Integer.toString(900-indent*10)+"px; border-top: 1px #eeeeee solid");
    if (indent > 0) {
      d.style("margin-left: "+Integer.toString(10*indent)+"px");
    }
    XhtmlNode display = d.div().style("display: inline-block; width: "+Integer.toString(500-indent*10)+"px");
    XhtmlNode details = d.div().style("border: 1px #ccccff solid; padding: 2px; display: inline-block; background-color: #fefce7; width: 380px");
    XhtmlNode p = display.para();
    if (i.getType() == QuestionnaireItemType.GROUP) {
      p = p.b();
    }
    if (i.hasPrefix()) {
      p.tx(i.getPrefix());
      p.tx(": ");
    }
    p.span(null, "linkId: "+i.getLinkId()).tx(i.getText());
    if (i.getRequired()) {
      p.span("color: red", context.formatMessage(RenderingContext.QUEST_MAND)).tx("*");
    }

    XhtmlNode input = null;
    switch (i.getType()) {
    case STRING:
      p.tx(" ");
      input = p.input(i.getLinkId(), "text", i.getType().getDisplay(), 60);
      break;
    case ATTACHMENT:
      break;
    case BOOLEAN:
      p.tx(" ");
      input = p.input(i.getLinkId(), "checkbox", i.getType().getDisplay(), 1);
      break;
    case CODING:
      input = p.select(i.getLinkId());
      listOptions(q, i, input);
      break;
    case DATE:
      p.tx(" ");
      input = p.input(i.getLinkId(), "date", i.getType().getDisplay(), 10);
      break;
    case DATETIME:
      p.tx(" ");
      input = p.input(i.getLinkId(), "datetime-local", i.getType().getDisplay(), 25);
      break;
    case DECIMAL:
      p.tx(" ");
      input = p.input(i.getLinkId(), "number", i.getType().getDisplay(), 15);
      break;
    case DISPLAY:
      break;
    case GROUP:
      
      break;
    case INTEGER:
      p.tx(" ");
      input = p.input(i.getLinkId(), "number", i.getType().getDisplay(), 10);
      break;
    case QUANTITY:
      p.tx(" ");
      input = p.input(i.getLinkId(), "number", "value", 15);
      p.tx(" ");
      input = p.input(i.getLinkId(), "unit", "unit", 10);
      break;
    case QUESTION:
      break;
    case REFERENCE:
      break;
    case TEXT:
      break;
    case TIME:
      break;
    case URL:
      break;
    default:
      break;
    }
    if (input != null) {
      if (i.getReadOnly()) {
        input.attribute("readonly", "1");
        input.style("background-color: #eeeeee");
      }
    }
    
//  if (i.hasExtension(ToolingExtensions.EXT_Q_CHOICE_ORIENT)) {
//  String code = ToolingExtensions.readStringExtension(i,  ToolingExtensions.EXT_Q_CHOICE_ORIENT);
//  flags.addPiece(gen.new Piece("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod", null, "Orientation: "+code).addHtml(new XhtmlNode(NodeType.Element, "img").attribute("alt", "icon").attribute("src", Utilities.path(context.getLocalPrefix(), "icon-qi-"+code+".png"))));
//}

    
    XhtmlNode ul = details.ul();
    boolean hasFlag = false; 
    XhtmlNode flags = item(ul, "Flags");
    item(ul, "linkId", i.getLinkId());
    
    if (ToolingExtensions.readBoolExtension(i, "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-isSubject")) {
      hasFlag = true;
      flags.ah(getSDCLink("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-isSubject", "StructureDefinition-sdc-questionnaire-isSubject.html"), context.formatMessage(RenderingContext.QUEST_SUBJECT)).img(getImgPath("icon-qi-subject.png"), "icon");
    }
    if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_HIDDEN)) {
      hasFlag = true;
      flags.ah(Utilities.pathURL(context.getLink(KnownLinkType.SPEC), "extension-questionnaire-hidden.html"), context.formatMessage(RenderingContext.QUEST_HIDDEN)).img(getImgPath("icon-qi-hidden.png"), "icon");
      d.style("background-color: #eeeeee");
    }
    if (ToolingExtensions.readBoolExtension(i, ToolingExtensions.EXT_Q_OTP_DISP)) {
      hasFlag = true;
      flags.ah(getSDCLink("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-optionalDisplay", "StructureDefinition-sdc-questionnaire-optionalDisplay.html"), context.formatMessage(RenderingContext.QUEST_DISPLAY)).img(getImgPath("icon-qi-optional.png"), "icon");
    }
    if (i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod")) {
      hasFlag = true;
      flags.ah(getSDCLink("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod", "StructureDefinition-sdc-questionnaire-observationLinkPeriod.html"), context.formatMessage(RenderingContext.QUEST_LINKED)).img(getImgPath("icon-qi-observation.png"), "icon");
    }
    if (i.hasExtension(ToolingExtensions.EXT_Q_DISPLAY_CAT)) {
      CodeableConcept cc = i.getExtensionByUrl(ToolingExtensions.EXT_Q_DISPLAY_CAT).getValueCodeableConcept();
      String code = cc.getCode("http://hl7.org/fhir/questionnaire-display-category");
      hasFlag = true;
      flags.ah("https://hl7.org/fhir/R4/extension-questionnaire-displayCategory.html", (context.formatMessage(RenderingContext.QUEST_CAT, code)+" ")).img(getImgPath("icon-qi-" + code + ".png"), "icon");
    }

    if (i.hasMaxLength()) {
      item(ul, context.formatMessage(RenderingContext.QUEST_MAX), Integer.toString(i.getMaxLength()));
    }
    if (i.hasDefinition()) {
      genDefinitionLink(item(ul, context.formatMessage(RenderingContext.QUEST_DEF)), i, q);      
    }
    if (i.hasEnableWhen()) {
      item(ul, context.formatMessage(RenderingContext.QUEST_EN), "todo");
    }
    if (i.hasAnswerValueSet()) {
      XhtmlNode ans = item(ul, context.formatMessage(RenderingContext.QUEST_ANSWERS));
      if (!Utilities.noString(i.getAnswerValueSet()) && i.getAnswerValueSet().startsWith("#")) {
        ValueSet vs = (ValueSet) q.getContained(i.getAnswerValueSet().substring(1));
        if (vs == null || !vs.hasWebPath()) {
          ans.tx(i.getAnswerValueSet());                    
        } else {
          ans.ah(vs.getWebPath()).tx(vs.present());                              
        }
      } else {
        ValueSet vs = context.getWorker().findTxResource(ValueSet.class, i.getAnswerValueSet(), q);
        if (vs == null  || !vs.hasWebPath()) {
          ans.tx(i.getAnswerValueSet());                    
        } else {
          ans.ah(vs.getWebPath()).tx(vs.present());                              
        }             
      }
    }
    if (i.hasAnswerOption()) {
      item(ul, context.formatMessage(RenderingContext.QUEST_ANSWERS), Integer.toString(i.getAnswerOption().size())+" "+Utilities.pluralize("option", i.getAnswerOption().size()), context.getDefinitionsTarget()+"#item."+i.getLinkId());
    }
    if (i.hasInitial()) {
      XhtmlNode vi = item(ul, context.formatMessage(RenderingContext.QUEST_INT));
      boolean first = true;
      for (QuestionnaireItemInitialComponent v : i.getInitial()) {
        if (first) first = false; else vi.tx(", ");
        if (v.getValue().isPrimitive()) {
          vi.tx(v.getValue().primitiveValue());
        } else if (v.hasValueCoding()) {
          renderCoding(vi, v.getValueCoding(), true);           
        } else if (v.hasValueReference()) {
          renderReference(vi, v.getValueReference());           
        } else if (v.hasValueQuantity()) {
          renderQuantity(vi, v.getValueQuantity());           
//        } else if (v.hasValueAttachment()) {
//          renderAttachment(vi, v.getValueAttachment());           
        }
      }
    }
    if (!hasFlag) {
      ul.remove(flags);
    }
//    if (i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression") || i.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression")) {
//      if (!defn.getPieces().isEmpty()) defn.addPiece(gen.new Piece("br"));
//      defn.getPieces().add(gen.new Piece(null, "Expressions: ", null));
//      Piece p = gen.new Piece("ul");
//      defn.getPieces().add(p);
//      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression")) {
//        addExpression(p, e.getValueExpression(), "Initial Value", "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression");
//      }
//      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression")) {
//        addExpression(p, e.getValueExpression(), "Context", "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-contextExpression");
//      }
//      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext")) {
//        addExpression(p, e.getValueExpression(), "Item Context", "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext");
//      }
//      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression")) {
//        addExpression(p, e.getValueExpression(), "Enable When", "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-enableWhenExpression");
//      }
//      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression")) {
//        addExpression(p, e.getValueExpression(), "Calculated Value", "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-calculatedExpression");
//      }
//      for (Extension e : i.getExtensionsByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression")) {
//        addExpression(p, e.getValueExpression(), "Candidates", "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-candidateExpression");
//      } 
//    }
//

    int t = 1;
    for (QuestionnaireItemComponent c : i.getItem()) {
      hasExt = renderFormItem(x, q, c, pfx == null ? null : pfx+"."+Integer.toString(t), indent+1) || hasExt;
      t++;
    }
    return hasExt; 
  }

  @Nonnull
  private String getImgPath(String code) throws IOException {
      return context.getLocalPrefix().length() > 0
        ? Utilities.path(context.getLocalPrefix(), code)
        : Utilities.path(code);
  }

  private void item(XhtmlNode ul, String name, String value, String valueLink) {
    if (!Utilities.noString(value)) {
      ul.li().style("font-size: 10px").ah(valueLink).tx(name+": "+value);
    }
  }

  private void item(XhtmlNode ul, String name, String value) {
    if (!Utilities.noString(value)) {
      ul.li().style("font-size: 10px").tx(name+": "+value);
    }
  }
  private XhtmlNode item(XhtmlNode ul, String name) {
    XhtmlNode li = ul.li();
    li.style("font-size: 10px").tx(name+": ");
    return li;
  }


  private void listOptions(Questionnaire q, QuestionnaireItemComponent i, XhtmlNode select) {
    if (i.hasAnswerValueSet()) {
      ValueSet vs = null;
      if (!Utilities.noString(i.getAnswerValueSet()) && i.getAnswerValueSet().startsWith("#")) {
        vs = (ValueSet) q.getContained(i.getAnswerValueSet().substring(1));
        if (vs != null && !vs.hasUrl()) {
          vs = vs.copy();
          vs.setUrl(q.getUrl()+"--"+q.getContained(i.getAnswerValueSet().substring(1)));
        }
      } else {
        vs = context.getContext().findTxResource(ValueSet.class, i.getAnswerValueSet(), q);
      }
      if (vs != null) {
        ValueSetExpansionOutcome exp = context.getContext().expandVS(vs, true, false);
        if (exp.getValueset() != null) {
          for (ValueSetExpansionContainsComponent cc : exp.getValueset().getExpansion().getContains()) {
            select.option(cc.getCode(), cc.hasDisplay() ? cc.getDisplay() : cc.getCode(), false);    
          }
          return;
        }
      }
    } else if (i.hasAnswerOption()) {
      renderItemOptions(select, i); 
    } 
    select.option("a", "??", false);    
  }

  public String display(Resource dr) throws UnsupportedEncodingException, IOException {
    return display((Questionnaire) dr);
  }

  public String display(Questionnaire q) throws UnsupportedEncodingException, IOException {
    return context.formatMessage(RenderingContext.QUEST_QUESTIONNAIRE, q.present())+" ";
  }
 
  private boolean renderLinks(XhtmlNode x, Questionnaire q) {
    x.para().tx(context.formatMessage(RenderingContext.QUEST_TRY));
    XhtmlNode ul = x.ul();
    ul.li().ah("http://todo.nlm.gov/path?mode=ig&src="+Utilities.pathURL(context.getLink(KnownLinkType.SELF), "package.tgz")+"&q="+q.getId()+".json").tx(context.formatMessage(RenderingContext.QUEST_NLM));
    return false;
  }

  private boolean renderDefns(XhtmlNode x, Questionnaire q) throws IOException {
    XhtmlNode tbl = x.table("dict");
    boolean ext = false;
    ext = renderRootDefinition(tbl, q, new ArrayList<>()) || ext;
    for (QuestionnaireItemComponent qi : q.getItem()) {
      ext = renderDefinition(tbl, q, qi, new ArrayList<>()) || ext;
    }
    return ext;
  }

  private boolean renderRootDefinition(XhtmlNode tbl, Questionnaire q, List<QuestionnaireItemComponent> parents) throws IOException {
    boolean ext = false;
    XhtmlNode td = tbl.tr().td("structure").colspan("2").span(null, null).attribute("class", "self-link-parent");
    td.an(q.getId());
    td.img(getImgPath("icon_q_root.gif"), "icon");
    td.tx(" "+(context.formatMessage(RenderingContext.QUEST_QUEST)+" "));
    td.b().tx(q.getId());
    
    // general information
    defn(tbl, context.formatMessage(RenderingContext.QUEST_URL), q.getUrl());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_VERSION), q.getVersion());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_NAME), q.getName());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_TITLE), q.getTitle());
    if (q.hasDerivedFrom()) {
      td = defn(tbl, context.formatMessage(RenderingContext.QUEST_DERIVED));
      boolean first = true;
      for (CanonicalType c : q.getDerivedFrom()) {
        if (first) first = false; else td.tx(", ");
        td.tx(c.asStringValue()); // todo: make these a reference
      }
    }
    defn(tbl, context.formatMessage(RenderingContext.QUEST_STATUS), q.getStatus().getDisplay());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_EXPER), q.getExperimental());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_PUB), q.getDateElement().primitiveValue());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_APP), q.getApprovalDateElement().primitiveValue());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_REV_DATE), q.getLastReviewDateElement().primitiveValue());
    if (q.hasEffectivePeriod()) {
      renderPeriod(defn(tbl, context.formatMessage(RenderingContext.QUEST_EFF_PERIOD)), q.getEffectivePeriod());
    }
    
    if (q.hasSubjectType()) {
      td = defn(tbl, context.formatMessage(RenderingContext.QUEST_SUB_TYPE));
      boolean first = true;
      for (CodeType c : q.getSubjectType()) {
        if (first) first = false; else td.tx(", ");
        td.tx(c.asStringValue());
      }
    }
    defn(tbl, context.formatMessage(RenderingContext.QUEST_DESCRIPTION), q.getDescription());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_PURPOSE), q.getPurpose());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_COPYRIGHT), q.getCopyright());
    if (q.hasCode()) {
      td = defn(tbl, Utilities.pluralize("Code", q.getCode().size()));
      boolean first = true;
      for (Coding c : q.getCode()) {
        if (first) first = false; else td.tx(", ");
        renderCodingWithDetails(td,  c);
      }
    }
    return false;
  }
  
  private boolean renderDefinition(XhtmlNode tbl, Questionnaire q, QuestionnaireItemComponent qi, List<QuestionnaireItemComponent> parents) throws IOException {
    boolean ext = false;
    XhtmlNode td = tbl.tr().td("structure").colspan("2").span(null, null).attribute("class", "self-link-parent");
    td.an("item."+qi.getLinkId());
    for (QuestionnaireItemComponent p : parents) {
      td.ah("#item."+p.getLinkId()).img(getImgPath("icon_q_item.png"), "icon");
      td.tx(" > ");
    }
    td.img(getImgPath("icon_q_item.png"), "icon");
    td.tx(" Item ");
    td.b().tx(qi.getLinkId());
    
    // general information
    defn(tbl, context.formatMessage(RenderingContext.QUEST_ID), qi.getLinkId());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_PREFIX), qi.getPrefix());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_TEXT), qi.getText());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_TYPE), qi.getType().getDisplay());
    defn(tbl, context.formatMessage(RenderingContext.QUEST_REQ), qi.getRequired(), true);
    defn(tbl, context.formatMessage(RenderingContext.QUEST_REP), qi.getRepeats(), true);
    defn(tbl, context.formatMessage(RenderingContext.QUEST_READ_ONLY), qi.getReadOnly(), false);
    if (ToolingExtensions.readBoolExtension(qi, "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-isSubject")) {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_SUB), "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-isSubject", "This element changes who the subject of the question is", null);
    }
    
    // content control
    defn(tbl, context.formatMessage(RenderingContext.QUEST_MAX_LENGTH), qi.getMaxLength());
    if (qi.hasAnswerValueSet()) {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_VALUE_SET), qi.getDefinition(), context.getWorker().findTxResource(ValueSet.class,  qi.getAnswerValueSet(), q));
    }
    if (qi.hasAnswerOption()) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(context.formatMessage(RenderingContext.QUEST_ALLOWED));
      XhtmlNode ul = tr.td().ul();
      for (QuestionnaireItemAnswerOptionComponent ans : qi.getAnswerOption()) {
        XhtmlNode li = ul.li();
        render(li, ans.getValue());
        if (ans.getInitialSelected()) {
          li.tx(" "+(context.formatMessage(RenderingContext.QUEST_INITIALLY)));
        }
      }      
    }
    if (qi.hasInitial()) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(Utilities.pluralize((context.formatMessage(RenderingContext.QUEST_INITIAL_ANSWER)), qi.getInitial().size()));
      if (qi.getInitial().size() == 1) {
        render(tr.td(), qi.getInitialFirstRep().getValue());
      } else {
        XhtmlNode ul = tr.td().ul();
        for (QuestionnaireItemInitialComponent ans : qi.getInitial()) {
          XhtmlNode li = ul.li();
          render(li, ans.getValue());
        }
      }      
    }

    // appearance 
    if (qi.hasExtension(ToolingExtensions.EXT_Q_DISPLAY_CAT)) {
      XhtmlNode tr = tbl.tr();
      tr.td().ah(ToolingExtensions.EXT_Q_DISPLAY_CAT).tx("Display Category");
      render(tr.td(), qi.getExtensionByUrl(ToolingExtensions.EXT_Q_DISPLAY_CAT).getValue());
    }
    if (ToolingExtensions.readBoolExtension(qi, ToolingExtensions.EXT_Q_HIDDEN)) {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_HIDDEN_ITEM), ToolingExtensions.EXT_Q_DISPLAY_CAT, "This item is a hidden question", null);
    }
    if (ToolingExtensions.readBoolExtension(qi, ToolingExtensions.EXT_Q_OTP_DISP)) {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_HIDDEN_ITEM), ToolingExtensions.EXT_Q_OTP_DISP, "This item is optional to display", null);
    }
    
    // formal definitions
    if (qi.hasDefinition()) {
      genDefinitionLink(defn(tbl, context.formatMessage(RenderingContext.QUEST_DEFINITION)), qi, q);
    }
      
    if (qi.hasCode()) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(Utilities.pluralize(context.formatMessage(RenderingContext.QUEST_CODE), qi.getCode().size()));
      XhtmlNode ul = tr.td().ul();
      for (Coding c : qi.getCode()) {
        renderCodingWithDetails(ul.li(), c);
      }
    }
    if (qi.hasExtension("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod")) {
      XhtmlNode tr = tbl.tr();
      StructureDefinition sd = context.getContext().fetchResource(StructureDefinition.class, ToolingExtensions.EXT_O_LINK_PERIOD);
      if (sd != null && sd.hasWebPath()) {
        tr.td().ah(sd.getWebPath()).tx(context.formatMessage(RenderingContext.QUEST_OBSERVATION));
      } else {
        tr.td().ah("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod").tx(context.formatMessage(RenderingContext.QUEST_OBSERVATION));
      }
      render(tr.td(), qi.getExtensionByUrl("http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-observationLinkPeriod").getValue());
    }
    
    // dynamic management
    if (qi.hasEnableWhen()) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(context.formatMessage(RenderingContext.QUEST_EN));
      td = tr.td();
      if (qi.getEnableWhen().size() == 1) {
        renderEnableWhen(td, qi.getEnableWhen().get(0));
      } else {
        if (qi.hasEnableBehavior()) {
          td.tx(qi.getEnableBehavior().getDisplay()+" "+(context.formatMessage(RenderingContext.QUEST_TRUE)));
        } else {
          td.tx(context.formatMessage(RenderingContext.QUEST_ARE_TRUE));
        }
        XhtmlNode ul = td.ul();
        for (QuestionnaireItemEnableWhenComponent ew : qi.getEnableWhen()) {
          renderEnableWhen(ul.li(), ew);
        }
      }      
    }
    
    
    // other stuff
    

    
    List<QuestionnaireItemComponent> curr = new ArrayList<>();
    curr.addAll(parents);
    curr.add(qi);
    for (QuestionnaireItemComponent qic : qi.getItem()) {
      ext = renderDefinition(tbl, q, qic, curr) || ext;
    }
    return ext;
  }

  private void defn(XhtmlNode tbl, String name, String url, Resource res) throws UnsupportedEncodingException, IOException {
    if (res != null && res.hasWebPath()) {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_DEFINITION), RendererFactory.factory(res, context).display(res), res.getWebPath());
    } else if (Utilities.isAbsoluteUrlLinkable(url)) {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_DEFINITION), url, url);
    } {
      defn(tbl, context.formatMessage(RenderingContext.QUEST_DEFINITION), url);
    }
 
  }

  private void renderEnableWhen(XhtmlNode x, QuestionnaireItemEnableWhenComponent ew) {
    x.ah("#item."+ew.getQuestion()).tx(ew.getQuestion());
    x.tx(" ");
    x.tx(ew.getOperator().toCode());
    x.tx(" ");
    x.tx(display(ew.getAnswer()));
  }

  private XhtmlNode defn(XhtmlNode tbl, String name) {
    XhtmlNode tr = tbl.tr();
    tr.td().tx(name);
    return tr.td();
  }
  
  private void defn(XhtmlNode tbl, String name, int value) {
    if (value > 0) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(name);
      tr.td().tx(value);
    }    
  }
 
  
  private void defn(XhtmlNode tbl, String name, boolean value) {
    XhtmlNode tr = tbl.tr();
    tr.td().tx(name);
    tr.td().tx(Boolean.toString(value));
  }
 
  private void defn(XhtmlNode tbl, String name, String value) {
    if (!Utilities.noString(value)) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(name);
      tr.td().tx(value);
    }    
  }
  
  private void defn(XhtmlNode tbl, String name, String value, String url) {
    if (!Utilities.noString(value)) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(name);
      tr.td().ah(url).tx(value);
    }    
  }

  private void defn(XhtmlNode tbl, String name, String nurl, String value, String url) {
    if (!Utilities.noString(value)) {
      XhtmlNode tr = tbl.tr();
      tr.td().ah(nurl).tx(name);
      if (url != null) {
        tr.td().ah(url).tx(value);
      } else {
        tr.td().tx(value);
      }
    }    
  }

  private void defn(XhtmlNode tbl, String name, boolean value, boolean ifFalse) {
    if (ifFalse || value) {
      XhtmlNode tr = tbl.tr();
      tr.td().tx(name);
      tr.td().tx(Boolean.toString(value));
    }    
  }

}
