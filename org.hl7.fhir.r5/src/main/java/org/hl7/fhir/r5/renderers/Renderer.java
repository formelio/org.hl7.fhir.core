package org.hl7.fhir.r5.renderers;

import java.util.Date;

import org.hl7.fhir.r5.comparison.VersionComparisonAnnotation;
import org.hl7.fhir.r5.context.IWorkerContext;
import org.hl7.fhir.r5.model.Base;
import org.hl7.fhir.r5.model.Enumeration;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.GenerationRules;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.KnownLinkType;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.ResourceRendererMode;
import org.hl7.fhir.utilities.MarkDownProcessor;
import org.hl7.fhir.utilities.StandardsStatus;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.MarkDownProcessor.Dialect;
import org.hl7.fhir.utilities.validation.ValidationOptions;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

/**
 * Rendering framework:
 * 
 *   * boolean render(DomainResource) : produce an HTML representation suitable for runtime / documentation, and insert it into the resource. Return true of any extensions encountered
 *   * boolean render(XhtmlNode, Resource: produce an HTML representation, and fill out the provided node with it. Return true of any extensions encountered
 *   * XhtmlNode build(DomainResource): same as render(DomainResource) but also return the XHtmlNode 
 *   
 *   * String display(Base) : produce a plan text concise representation that serves to describe the resource
 *   * void display(XhtmlNode, Base) : produce a plan text concise representation that serves to describe the resource
 *   
 *   * void describe(XhtmlNode, Resource) : produce a short summary of the resource with key details presented (potentially more verbose than display, but still suitable for a single line)  
 *   
 * if not specific code for rendering a resource has been provided, and there's no liquid script to guide it, a generic rendering based onthe profile will be performed
 *   
 * @author graha
 *
 */
public class Renderer  {

  protected RenderingContext context;
  
  public Renderer(RenderingContext context) {
    this.context = context;
  }

  public Renderer(IWorkerContext worker) {
    this.context = new RenderingContext(worker, new MarkDownProcessor(Dialect.COMMON_MARK), ValidationOptions.defaults(), "http://hl7.org/fhir/R5", "", null, ResourceRendererMode.END_USER, GenerationRules.IG_PUBLISHER);
  }


  protected String formatMessage(String theMessage, Object... theMessageArguments) {
    return context.formatMessage(theMessage, theMessageArguments);
  }

  public void genStandardsStatus(XhtmlNode td, StandardsStatus ss) {
    if (ss != null) {
      td.tx(" ");
      XhtmlNode a = td.ah(Utilities.pathURL(context.getLink(KnownLinkType.SPEC), "versions.html#std-process"), (context.formatMessage(RenderingContext.REND_STANDARDS, ss.toDisplay())+" "));
      a.style("padding-left: 3px; padding-right: 3px; border: 1px grey solid; font-weight: bold; color: black; background-color: "+ss.getColor());
      a.tx(ss.getAbbrev());
    }
  }

  protected XhtmlNode renderStatus(Base b, XhtmlNode x) {
    if (b == null || context.getChangeVersion() == null) {
      return x;
    }
    VersionComparisonAnnotation vca = (VersionComparisonAnnotation) b.getUserData(VersionComparisonAnnotation.USER_DATA_NAME);
    if (vca == null) {
      return x;
    }
    switch (vca.getType()) {
    case Added:
      XhtmlNode spanOuter = x.span("border: solid 1px #dddddd; margin: 2px; padding: 2px", null);
      XhtmlNode spanInner = spanOuter.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_SINCE, context.getChangeVersion())+" "));
      spanInner.img("icon-change-add.png", "icon");
      spanInner.tx(" "+context.formatMessage(RenderingContext.REND_ADDED));
      return spanOuter;
    case Changed:
      spanOuter = x.span("border: solid 1px #dddddd; margin: 2px; padding: 2px", null);
      spanInner = spanOuter.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_SINCE, context.getChangeVersion()+(vca.getOriginal() != null ? " (was '"+vca.getOriginal()+"')" : "")))+" ");
      spanInner.img("icon-change-edit.png", "icon");
      spanInner.tx(" "+context.formatMessage(RenderingContext.REND_CHANGED));
      return spanOuter;
    case Deleted:
      spanOuter = x.span("border: solid 1px #dddddd; margin: 2px; padding: 2px", null);
      spanInner = spanOuter.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_SINCE, context.getChangeVersion())+" "));
      spanInner.img("icon-change-remove.png", "icon");
      spanInner.tx(" "+context.formatMessage(RenderingContext.REND_REMOVED));
      return spanOuter.strikethrough();
    default:
      return x;
    }
  }

  protected XhtmlNode renderStatusDiv(Base b, XhtmlNode x) {
    if (b == null || context.getChangeVersion() == null) {
      return x;
    }
    VersionComparisonAnnotation vca = (VersionComparisonAnnotation) b.getUserData(VersionComparisonAnnotation.USER_DATA_NAME);
    if (vca == null) {
      return x;
    }
    switch (vca.getType()) {
    case Added:
      XhtmlNode divOuter = x.div("border: solid 1px #dddddd; margin: 2px; padding: 2px");
      XhtmlNode spanInner = divOuter.para().style("margin: 0").span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_SINCE, context.getChangeVersion())+" "));
      spanInner.img("icon-change-add.png", "icon");
      spanInner.tx(" "+context.formatMessage(RenderingContext.REND_ADDED));
      return divOuter;
    case Changed:
      divOuter = x.div("border: solid 1px #dddddd; margin: 2px; padding: 2px");
      spanInner = divOuter.para().style("margin: 0").span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_SINCE, context.getChangeVersion())+(vca.getOriginal() != null ? " (was '"+(vca.getOriginal())+"')" : ""))+" ");
      spanInner.img("icon-change-edit.png", "icon");
      spanInner.tx(" "+context.formatMessage(RenderingContext.REND_CHANGED));
      return divOuter;
    case Deleted:
      divOuter = x.div("border: solid 1px #dddddd; margin: 2px; padding: 2px");
      spanInner = divOuter.para().style("margin: 0").span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_SINCE, context.getChangeVersion())+" "));
      spanInner.img("icon-change-remove.png", "icon");
      spanInner.tx(" "+context.formatMessage(RenderingContext.REND_REMOVED));
      return divOuter.strikethrough();
    default:
      return x;
    }
  }
  

  protected XhtmlNode renderStatusRow(Base b, XhtmlNode tbl, XhtmlNode tr) {
    if (b == null || context.getChangeVersion() == null) {
      return tr.td();
    }
    VersionComparisonAnnotation vca = (VersionComparisonAnnotation) b.getUserData(VersionComparisonAnnotation.USER_DATA_NAME);
    if (vca == null) {
      return tr.td();
    }
    switch (vca.getType()) {
    case Added:
      if (tbl.isClass("grid")) {
        tr.style("border: solid 1px #dddddd; margin: 2px; padding: 2px");
      }
      XhtmlNode td = tr.td();
      XhtmlNode span = td.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", (context.formatMessage(RenderingContext.REND_ROW_SINCE, context.getChangeVersion())+" "));
      span.img("icon-change-add.png", "icon");
      span.tx(" "+/*!#*/"Added:");
      XhtmlNode x = new XhtmlNode(NodeType.Element, "holder");
      x.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", /*!#*/"This row of content has been added since "+context.getChangeVersion()).tx(" ");
      tr.styleCells(x);
      return td;
    case Changed:
      td = tr.td();
      span = td.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", /*!#*/"This row of content has been changed since"+context.getChangeVersion()+(vca.getOriginal() != null ? " (was '"+vca.getOriginal()+"')" : ""));
      span.img("icon-change-edit.png", "icon");
      span.tx(" "+/*!#*/"Changed:");
      return td;
    case Deleted:
      tr.style("text-decoration: line-through");
      td = tr.td();
      span = td.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", /*!#*/"This content has been removed since  "+context.getChangeVersion());
      span.img("icon-change-remove.png", "icon");
      span.tx(" "+/*!#*/"Removed:");
      x = new XhtmlNode(NodeType.Element, "holder");
      x.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px; text-decoration: none", /*!#*/"This row of content has been added since  "+context.getChangeVersion()).tx(" ");
      tr.styleCells(x);
      return td;
    default:
      return tr.td();
    }
  }

  public static void renderStatusSummary(Base base, XhtmlNode x, String version, String... metadataFields) {
    if (base.hasUserData(VersionComparisonAnnotation.USER_DATA_NAME)) {
      VersionComparisonAnnotation self = (VersionComparisonAnnotation) base.getUserData(VersionComparisonAnnotation.USER_DATA_NAME);
      switch (self.getType()) {
      case Added:
        XhtmlNode spanInner = x.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", /*!#*/"This content has been added since "+version);
        spanInner.img("icon-change-add.png", "icon");
        spanInner.tx(" "+/*!#*/"Added");
        return;
      case Changed:
        if (self.getComp().noChangeOtherThanMetadata(metadataFields)) {
          x.span("color: #eeeeee").tx("n/c");
          return;
        } else {
          spanInner = x.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", /*!#*/"This content has been changed since "+version+(self.getOriginal() != null ? " (was '"+(self.getOriginal())+"')" : ""));
          spanInner.img("icon-change-edit.png", "icon");
          spanInner.tx(" "+/*!#*/"Changed");
        }
        return;
      case Deleted:
        spanInner = x.span("background-color: #fff2ff; border-left: solid 3px #ffa0ff; margin: 2px; padding: 2px", /*!#*/"This content has been added since "+version);
        spanInner.img("icon-change-remove.png", "icon");
        spanInner.tx(" "+/*!#*/"Removed");
        return;
      default:
        x.span("color: #eeeeee").tx("n/c");
        return;
      }
    } else {
      x.span("color: #eeeeee").tx("--");
    }
  }


  public String egt(@SuppressWarnings("rawtypes") Enumeration<? extends Enum> value) {
    if (value == null || !value.hasPrimitiveValue()) {
      return null;
    } else {
      return (value == null || !value.hasPrimitiveValue()) ? null : value.asStringValue();
    }
  }

  public String toStr(int value) {
    return Integer.toString(value);
  }
  
  public String toStr(Date value) {
    return value.toString();
  }
}
