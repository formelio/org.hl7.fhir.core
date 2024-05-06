package org.hl7.fhir.r5.renderers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.hl7.fhir.exceptions.DefinitionException;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.hl7.fhir.r5.model.Annotation;
import org.hl7.fhir.r5.model.Base;
import org.hl7.fhir.r5.model.ListResource;
import org.hl7.fhir.r5.model.ListResource.ListResourceEntryComponent;
import org.hl7.fhir.r5.model.Reference;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.renderers.utils.BaseWrappers.BaseWrapper;
import org.hl7.fhir.r5.renderers.utils.BaseWrappers.ResourceWrapper;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.r5.renderers.utils.Resolver.ResourceContext;
import org.hl7.fhir.r5.renderers.utils.Resolver.ResourceWithReference;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

public class ListRenderer extends ResourceRenderer {

  public ListRenderer(RenderingContext context) {
    super(context);
  }

  public ListRenderer(RenderingContext context, ResourceContext rcontext) {
    super(context, rcontext);
  }
  
  public boolean render(XhtmlNode x, Resource dr) throws FHIRFormatError, DefinitionException, IOException {
    return render(x, (ListResource) dr);
  }

  public boolean render(XhtmlNode x, ResourceWrapper list) throws FHIRFormatError, DefinitionException, IOException {
    if (list.has("title")) {
      x.h2().tx(list.get("title").primitiveValue());
    }
    XhtmlNode t = x.table("clstu");
    XhtmlNode tr = t.tr();
    XhtmlNode td = tr.td();
    if (list.has("date")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_DATE, displayDateTime(list.get("date").dateTimeValue()))+" ");
    } 
    if (list.has("mode")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_MODE, list.get("mode").primitiveValue())+" ");
    }
    if (list.has("status")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_STAT, list.get("status").primitiveValue())+" ");
    }
    if (list.has("code")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_CODE, displayBase(list.get("code")))+" ");
    }    
    tr = t.tr();
    td = tr.td();
    if (list.has("subject")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_SUB)+" ");
      shortForRef(td, list.get("subject"));
    }
    if (list.has("encounter")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_ENC)+" ");
      shortForRef(td, list.get("encounter"));
    }
    if (list.has("source")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_SRC)+" ");
      shortForRef(td, list.get("encounter"));
    }
    if (list.has("orderedBy")) {
      td.tx(context.formatMessage(RenderingContext.LIST_REND_ORD, displayBase(list.get("orderedBy")))+" ");
    }
    //    for (Annotation a : list.getNote()) {
    //      renderAnnotation(a, x);
    //    }
    boolean flag = false;
    boolean deleted = false;
    boolean date = false;
    for (BaseWrapper e : list.children("entry")) {
      flag = flag || e.has("flag");
      deleted = deleted || e.has("deleted");
      date = date || e.has("date");
    }
    t = x.table("grid");
    tr = t.tr().style("backgound-color: #eeeeee");
    tr.td().b().tx(context.formatMessage(RenderingContext.LIST_REND_ITEM));
    if (date) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_DAT));      
    }
    if (flag) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_FLAG));      
    }
    if (deleted) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_DEL));      
    }
    for (BaseWrapper e : list.children("entry")) {
      tr = t.tr();
      shortForRef(tr.td(), e.get("item"));
      if (date) {
        tr.td().tx(e.has("date") ? e.get("date").dateTimeValue().toHumanDisplay() : "");      
      }
      if (flag) {
        tr.td().tx(e.has("flag") ? displayBase(e.get("flag")) : "");      
      }
      if (deleted) {
        tr.td().tx(e.has("deleted") ? e.get("deleted").primitiveValue() : "");
      }
    }    
    return false;
  }
  public boolean render(XhtmlNode x, ListResource list) throws FHIRFormatError, DefinitionException, IOException {
    if (list.hasTitle()) {
      x.h2().tx(list.getTitle());
    }
    XhtmlNode t = x.table("clstu");
    XhtmlNode tr = t.tr();
    if (list.hasDate()) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_DATE, displayDateTime(list.getDateElement()))+" ");
    }
    if (list.hasMode()) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_MODE, list.getMode().getDisplay())+" ");
    }
    if (list.hasStatus()) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_STAT, list.getStatus().getDisplay())+" ");
    }
    if (list.hasCode()) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_CODE, display(list.getCode()))+" ");
    }    
    tr = t.tr();
    if (list.hasSubject()) {
      if (list.getSubject().size() == 1) {
        shortForRef(tr.td().txN("Subject: "), list.getSubjectFirstRep());
      } else {
        XhtmlNode td = tr.td();
        td.txN(context.formatMessage(RenderingContext.LIST_REND_SUB)+" ");
        int i = 0;
        for (Reference subj : list.getSubject()) {
          if (i == list.getSubject().size() - 1) {
            td.tx(" and ");
          } else if (i > 0) {
            td.tx(", ");
          }
          shortForRef(td, subj);          
        }
      }
    }
    if (list.hasEncounter()) {
      shortForRef(tr.td().txN(context.formatMessage(RenderingContext.LIST_REND_ENC)+" "), list.getEncounter());
    }
    if (list.hasSource()) {
      shortForRef(tr.td().txN(context.formatMessage(RenderingContext.LIST_REND_SRC)+" "), list.getEncounter());
    }
    if (list.hasOrderedBy()) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_ORD, display(list.getOrderedBy()))+" ");
    }
    for (Annotation a : list.getNote()) {
      renderAnnotation(x, a);
    }
    boolean flag = false;
    boolean deleted = false;
    boolean date = false;
    for (ListResourceEntryComponent e : list.getEntry()) {
      flag = flag || e.hasFlag();
      deleted = deleted || e.hasDeleted();
      date = date || e.hasDate();
    }
    t = x.table("grid");
    tr = t.tr().style("backgound-color: #eeeeee");
    tr.td().b().tx(context.formatMessage(RenderingContext.LIST_REND_ITEM));
    if (date) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_DAT));      
    }
    if (flag) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_FLAG));      
    }
    if (deleted) {
      tr.td().tx(context.formatMessage(RenderingContext.LIST_REND_DEL));      
    }
    for (ListResourceEntryComponent e : list.getEntry()) {
      tr = t.tr();
      shortForRef(tr.td(), e.getItem());
      if (date) {
        tr.td().tx(e.hasDate() ? e.getDate().toLocaleString() : "");      
      }
      if (flag) {
        tr.td().tx(e.hasFlag() ? display(e.getFlag()) : "");      
      }
      if (deleted) {
        tr.td().tx(e.hasDeleted() ? Boolean.toString(e.getDeleted()) : "");
      }
    }    
    return false;
  }

  public void describe(XhtmlNode x, ListResource list) {
    x.tx(display(list));
  }

  public String display(ListResource list) {
    return list.getTitle();
  }

  @Override
  public String display(Resource r) throws UnsupportedEncodingException, IOException {
    return ((ListResource) r).getTitle();
  }

  @Override
  public String display(ResourceWrapper r) throws UnsupportedEncodingException, IOException {
    if (r.has("title")) {
      return r.children("title").get(0).getBase().primitiveValue();
    }
    return "??";
  }

  private void shortForRef(XhtmlNode x, Reference ref) throws UnsupportedEncodingException, IOException {
    ResourceWithReference r = context.getResolver() == null ? null : context.getResolver().resolve(context, ref.getReference());
    if (r == null) { 
      x.tx(display(ref));
    } else {
      RendererFactory.factory(r.getResource().getName(), context).renderReference(r.getResource(), x, ref);
    }
  }

  private XhtmlNode shortForRef(XhtmlNode x, Base ref) throws UnsupportedEncodingException, IOException {
    if (ref == null) {
      x.tx("(null)");
    } else {
      String disp = ref.getChildByName("display") != null && ref.getChildByName("display").hasValues() ? ref.getChildByName("display").getValues().get(0).primitiveValue() : null;
      if (ref.getChildByName("reference").hasValues()) {
        String url = ref.getChildByName("reference").getValues().get(0).primitiveValue();
        if (url.startsWith("#")) {
          x.tx("?ngen-16a?");
        } else {
          ResourceWithReference r = context.getResolver().resolve(context, url);
          if (r == null) {
            if (disp == null) {
              disp = url;
            }
            x.tx(disp);
          } else if (r.getResource() != null) {
            RendererFactory.factory(r.getResource().getName(), context).renderReference(r.getResource(), x, (Reference) ref);
          } else {
            x.ah(r.getReference()).tx(url);
          }
        }
      } else if (disp != null) {
        x.tx(disp);      
      } else {
        x.tx("?ngen-16?");
      }     
    }
    return x;
  }
}
