package org.hl7.fhir.r5.renderers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.model.CodeType;
import org.hl7.fhir.r5.model.Enumeration;
import org.hl7.fhir.r5.model.Enumerations.SearchComparator;
import org.hl7.fhir.r5.model.Enumerations.SearchModifierCode;
import org.hl7.fhir.r5.model.Enumerations.VersionIndependentResourceTypesAll;
import org.hl7.fhir.r5.model.OperationDefinition;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.model.SearchParameter;
import org.hl7.fhir.r5.model.SearchParameter.SearchParameterComponentComponent;
import org.hl7.fhir.r5.model.StringType;
import org.hl7.fhir.r5.model.StructureDefinition;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.r5.renderers.utils.RenderingContext.KnownLinkType;
import org.hl7.fhir.r5.renderers.utils.Resolver.ResourceContext;
import org.hl7.fhir.r5.utils.EOperationOutcome;
import org.hl7.fhir.r5.utils.ToolingExtensions;
import org.hl7.fhir.utilities.StandardsStatus;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

public class SearchParameterRenderer extends TerminologyRenderer {

  public SearchParameterRenderer(RenderingContext context) {
    super(context);
  }

  public SearchParameterRenderer(RenderingContext context, ResourceContext rcontext) {
    super(context, rcontext);
  }
  
  public boolean render(XhtmlNode x, Resource dr) throws IOException, FHIRException, EOperationOutcome {
    return render(x, (SearchParameter) dr);
  }

  public boolean render(XhtmlNode x, SearchParameter spd) throws IOException, FHIRException, EOperationOutcome {
    XhtmlNode h2 = x.h2();
    h2.addText(spd.getName());
    StandardsStatus ss = ToolingExtensions.getStandardsStatus(spd);
    if (ss != context.getDefaultStandardsStatus()) {
      genStandardsStatus(h2, ss);
    }
    XhtmlNode p =  x.para();
    p.tx(context.formatMessage(RenderingContext.SEARCH_PAR_PAR)+" ");
    p.code().tx(spd.getCode());
    p.tx(":");
    p.code().tx(spd.getType().toCode());
    addMarkdown(x, spd.getDescription());

    XhtmlNode tbl = x.table("grid");
    XhtmlNode tr = tbl.tr();
    tr.td().tx(Utilities.pluralize(/*!#*/"Resource", spd.getBase().size()));
    XhtmlNode td = tr.td();
    for (Enumeration<VersionIndependentResourceTypesAll> t : spd.getBase()) {
      StructureDefinition sd = context.getWorker().fetchTypeDefinition(t.getCode());
      if (sd != null && sd.hasWebPath()) {
        td.sep(", ");
        td.ah(sd.getWebPath()).tx(t.getCode());
      } else {
        td.sep(", ");
        td.tx(t.getCode());
      }
    }
    tr = tbl.tr();
    tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_EXP));
    if (spd.hasExpression()) {
      tr.td().code().tx(spd.getExpression());
    } else {
      tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_NONE));
    }
    if (spd.hasProcessingMode()) {
      tr = tbl.tr();
      tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_PROC));
      tr.td().tx(spd.getProcessingMode().getDisplay());      
    }
    if (spd.hasTarget()) {
      tr = tbl.tr();
      tr.td().tx(Utilities.pluralize(/*!#*/"Target Resources", spd.getTarget().size()));
      td = tr.td();
      if (isAllConcreteResources(spd.getTarget())) {
        td.ah(Utilities.pathURL(context.getLink(KnownLinkType.SPEC), "resourcelist.html")).tx(context.formatMessage(RenderingContext.SEARCH_PAR_RES));
      } else {
        for (Enumeration<VersionIndependentResourceTypesAll> t : spd.getTarget()) {
          StructureDefinition sd = context.getWorker().fetchTypeDefinition(t.getCode());
          if (sd != null && sd.hasWebPath()) {
            td.sep(", ");
            td.ah(sd.getWebPath()).tx(t.getCode());
          } else {
            td.sep(", ");
            td.tx(t.getCode());
          }
        }
      }
    }
    tr = tbl.tr();    
    tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLES));
    XhtmlNode ul = tr.td().ul();
    if (!spd.hasMultipleAnd()) {
      ul.li().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLE_AND_SERVER));
    } else if (spd.getMultipleAnd()) {
      ul.li().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLE_AND_REPEAT));
    } else {
      ul.li().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLE_AND_APPEAR));
    }
    if (!spd.hasMultipleOr()) {
      ul.li().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLE_OR_SERVER));
    } else if (spd.getMultipleOr()) {
      ul.li().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLE_OR_MULTIPLE));
    } else {
      ul.li().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MULTIPLE_OR_ONE));
    }

    if (spd.hasComparator()) {
      tr = tbl.tr();
      tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_COMP));
      td = tr.td();
      td.tx(context.formatMessage(RenderingContext.SEARCH_PAR_ALLOWED)+" ");
      for (Enumeration<SearchComparator> t : spd.getComparator()) {
        td.sep(", ");
        td.tx(t.asStringValue());
      }      
    }
    if (spd.hasModifier()) {
      tr = tbl.tr();
      tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_MOD));
      td = tr.td();
      td.tx(context.formatMessage(RenderingContext.SEARCH_PAR_ALLOWED)+" ");
      for (Enumeration<SearchModifierCode> t : spd.getModifier()) {
        td.sep(", ");
        td.tx(t.asStringValue());
      }      
    }
    if (spd.hasChain()) {
      tr = tbl.tr();
      tr.td().tx(context.formatMessage(RenderingContext.SEARCH_PAR_CHAIN));
      td = tr.td();
      td.tx(context.formatMessage(RenderingContext.SEARCH_PAR_ALLOWED)+" ");
      for (StringType t : spd.getChain()) {
        td.sep(", ");
        td.tx(t.asStringValue());
      }      
    }
    
    if (spd.hasComponent()) {
      x.para().b().tx(context.formatMessage(RenderingContext.SEARCH_PAR_COMP));
      tbl = x.table("grid");
      for (SearchParameterComponentComponent t : spd.getComponent()) {
        tr = tbl.tr();
        SearchParameter tsp = context.getWorker().fetchResource(SearchParameter.class, t.getDefinition(), spd);
        if (tsp != null && tsp.hasWebPath()) {
          tr.td().ah(tsp.getWebPath()).tx(tsp.present());          
        } else {
          tr.td().tx(t.getDefinition());
        }
        tr.td().code().tx(t.getExpression());
      }
    }
    return false;
  }

  private boolean isAllConcreteResources(List<Enumeration<VersionIndependentResourceTypesAll>> list) {
    for (String s : context.getWorker().getResourceNames()) {
      StructureDefinition sd = context.getWorker().fetchTypeDefinition(s);
      if (!sd.getAbstract() && !Utilities.existsInList(sd.getType(), context.formatMessage(RenderingContext.SEARCH_PAR_PAR))) {
        boolean found = false;
        for (Enumeration<VersionIndependentResourceTypesAll> c : list) {
          found = found || sd.getName().equals(c.getCode());
        }
        if (!found) {
          return false;
        }
      }
    }
    return true;
  }

  public void describe(XhtmlNode x, OperationDefinition opd) {
    x.tx(display(opd));
  }

  public String display(OperationDefinition opd) {
    return opd.present();
  }

  @Override
  public String display(Resource r) throws UnsupportedEncodingException, IOException {
    return ((SearchParameter) r).present();
  }

}
