package org.hl7.fhir.r5.model;


/*
  Copyright (c) 2011+, HL7, Inc.
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without modification, \
  are permitted provided that the following conditions are met:
  
   * Redistributions of source code must retain the above copyright notice, this \
     list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, \
     this list of conditions and the following disclaimer in the documentation \
     and/or other materials provided with the distribution.
   * Neither the name of HL7 nor the names of its contributors may be used to 
     endorse or promote products derived from this software without specific 
     prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND \
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED \
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. \
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, \
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT \
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR \
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, \
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) \
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE \
  POSSIBILITY OF SUCH DAMAGE.
  */

// Generated on Thu, Mar 23, 2023 19:59+1100 for FHIR v5.0.0

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.r5.model.Enumerations.*;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.ICompositeType;
import ca.uhn.fhir.model.api.annotation.ResourceDef;
import ca.uhn.fhir.model.api.annotation.SearchParamDefinition;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import ca.uhn.fhir.model.api.annotation.Child;
import ca.uhn.fhir.model.api.annotation.ChildOrder;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.model.api.annotation.Block;

/**
 * Describes a measurement, calculation or setting capability of a device.  The DeviceMetric resource is derived from the ISO/IEEE 11073-10201 Domain Information Model standard, but is more widely applicable. 
 */
@ResourceDef(name="DeviceMetric", profile="http://hl7.org/fhir/StructureDefinition/DeviceMetric")
public class DeviceMetric extends DomainResource {

    public enum DeviceMetricCalibrationState {
        /**
         * The metric has not been calibrated.
         */
        NOTCALIBRATED, 
        /**
         * The metric needs to be calibrated.
         */
        CALIBRATIONREQUIRED, 
        /**
         * The metric has been calibrated.
         */
        CALIBRATED, 
        /**
         * The state of calibration of this metric is unspecified.
         */
        UNSPECIFIED, 
        /**
         * added to help the parsers with the generic types
         */
        NULL;
        public static DeviceMetricCalibrationState fromCode(String codeString) throws FHIRException {
            if (codeString == null || "".equals(codeString))
                return null;
        if ("not-calibrated".equals(codeString))
          return NOTCALIBRATED;
        if ("calibration-required".equals(codeString))
          return CALIBRATIONREQUIRED;
        if ("calibrated".equals(codeString))
          return CALIBRATED;
        if ("unspecified".equals(codeString))
          return UNSPECIFIED;
        if (Configuration.isAcceptInvalidEnums())
          return null;
        else
          throw new FHIRException("Unknown DeviceMetricCalibrationState code '"+codeString+"'");
        }
        public String toCode() {
          switch (this) {
            case NOTCALIBRATED: return "not-calibrated";
            case CALIBRATIONREQUIRED: return "calibration-required";
            case CALIBRATED: return "calibrated";
            case UNSPECIFIED: return "unspecified";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getSystem() {
          switch (this) {
            case NOTCALIBRATED: return "http://hl7.org/fhir/metric-calibration-state";
            case CALIBRATIONREQUIRED: return "http://hl7.org/fhir/metric-calibration-state";
            case CALIBRATED: return "http://hl7.org/fhir/metric-calibration-state";
            case UNSPECIFIED: return "http://hl7.org/fhir/metric-calibration-state";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDefinition() {
          switch (this) {
            case NOTCALIBRATED: return "The metric has not been calibrated.";
            case CALIBRATIONREQUIRED: return "The metric needs to be calibrated.";
            case CALIBRATED: return "The metric has been calibrated.";
            case UNSPECIFIED: return "The state of calibration of this metric is unspecified.";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
            case NOTCALIBRATED: return "Not Calibrated";
            case CALIBRATIONREQUIRED: return "Calibration Required";
            case CALIBRATED: return "Calibrated";
            case UNSPECIFIED: return "Unspecified";
            case NULL: return null;
            default: return "?";
          }
        }
    }

  public static class DeviceMetricCalibrationStateEnumFactory implements EnumFactory<DeviceMetricCalibrationState> {
    public DeviceMetricCalibrationState fromCode(String codeString) throws IllegalArgumentException {
      if (codeString == null || "".equals(codeString))
            if (codeString == null || "".equals(codeString))
                return null;
        if ("not-calibrated".equals(codeString))
          return DeviceMetricCalibrationState.NOTCALIBRATED;
        if ("calibration-required".equals(codeString))
          return DeviceMetricCalibrationState.CALIBRATIONREQUIRED;
        if ("calibrated".equals(codeString))
          return DeviceMetricCalibrationState.CALIBRATED;
        if ("unspecified".equals(codeString))
          return DeviceMetricCalibrationState.UNSPECIFIED;
        throw new IllegalArgumentException("Unknown DeviceMetricCalibrationState code '"+codeString+"'");
        }
        public Enumeration<DeviceMetricCalibrationState> fromType(PrimitiveType<?> code) throws FHIRException {
          if (code == null)
            return null;
          if (code.isEmpty())
            return new Enumeration<DeviceMetricCalibrationState>(this, DeviceMetricCalibrationState.NULL, code);
          String codeString = ((PrimitiveType) code).asStringValue();
          if (codeString == null || "".equals(codeString))
            return new Enumeration<DeviceMetricCalibrationState>(this, DeviceMetricCalibrationState.NULL, code);
        if ("not-calibrated".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationState>(this, DeviceMetricCalibrationState.NOTCALIBRATED, code);
        if ("calibration-required".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationState>(this, DeviceMetricCalibrationState.CALIBRATIONREQUIRED, code);
        if ("calibrated".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationState>(this, DeviceMetricCalibrationState.CALIBRATED, code);
        if ("unspecified".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationState>(this, DeviceMetricCalibrationState.UNSPECIFIED, code);
        throw new FHIRException("Unknown DeviceMetricCalibrationState code '"+codeString+"'");
        }
    public String toCode(DeviceMetricCalibrationState code) {
       if (code == DeviceMetricCalibrationState.NULL)
           return null;
       if (code == DeviceMetricCalibrationState.NOTCALIBRATED)
        return "not-calibrated";
      if (code == DeviceMetricCalibrationState.CALIBRATIONREQUIRED)
        return "calibration-required";
      if (code == DeviceMetricCalibrationState.CALIBRATED)
        return "calibrated";
      if (code == DeviceMetricCalibrationState.UNSPECIFIED)
        return "unspecified";
      return "?";
   }
    public String toSystem(DeviceMetricCalibrationState code) {
      return code.getSystem();
      }
    }

    public enum DeviceMetricCalibrationType {
        /**
         * Metric calibration method has not been identified.
         */
        UNSPECIFIED, 
        /**
         * Offset metric calibration method.
         */
        OFFSET, 
        /**
         * Gain metric calibration method.
         */
        GAIN, 
        /**
         * Two-point metric calibration method.
         */
        TWOPOINT, 
        /**
         * added to help the parsers with the generic types
         */
        NULL;
        public static DeviceMetricCalibrationType fromCode(String codeString) throws FHIRException {
            if (codeString == null || "".equals(codeString))
                return null;
        if ("unspecified".equals(codeString))
          return UNSPECIFIED;
        if ("offset".equals(codeString))
          return OFFSET;
        if ("gain".equals(codeString))
          return GAIN;
        if ("two-point".equals(codeString))
          return TWOPOINT;
        if (Configuration.isAcceptInvalidEnums())
          return null;
        else
          throw new FHIRException("Unknown DeviceMetricCalibrationType code '"+codeString+"'");
        }
        public String toCode() {
          switch (this) {
            case UNSPECIFIED: return "unspecified";
            case OFFSET: return "offset";
            case GAIN: return "gain";
            case TWOPOINT: return "two-point";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getSystem() {
          switch (this) {
            case UNSPECIFIED: return "http://hl7.org/fhir/metric-calibration-type";
            case OFFSET: return "http://hl7.org/fhir/metric-calibration-type";
            case GAIN: return "http://hl7.org/fhir/metric-calibration-type";
            case TWOPOINT: return "http://hl7.org/fhir/metric-calibration-type";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDefinition() {
          switch (this) {
            case UNSPECIFIED: return "Metric calibration method has not been identified.";
            case OFFSET: return "Offset metric calibration method.";
            case GAIN: return "Gain metric calibration method.";
            case TWOPOINT: return "Two-point metric calibration method.";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
            case UNSPECIFIED: return "Unspecified";
            case OFFSET: return "Offset";
            case GAIN: return "Gain";
            case TWOPOINT: return "Two Point";
            case NULL: return null;
            default: return "?";
          }
        }
    }

  public static class DeviceMetricCalibrationTypeEnumFactory implements EnumFactory<DeviceMetricCalibrationType> {
    public DeviceMetricCalibrationType fromCode(String codeString) throws IllegalArgumentException {
      if (codeString == null || "".equals(codeString))
            if (codeString == null || "".equals(codeString))
                return null;
        if ("unspecified".equals(codeString))
          return DeviceMetricCalibrationType.UNSPECIFIED;
        if ("offset".equals(codeString))
          return DeviceMetricCalibrationType.OFFSET;
        if ("gain".equals(codeString))
          return DeviceMetricCalibrationType.GAIN;
        if ("two-point".equals(codeString))
          return DeviceMetricCalibrationType.TWOPOINT;
        throw new IllegalArgumentException("Unknown DeviceMetricCalibrationType code '"+codeString+"'");
        }
        public Enumeration<DeviceMetricCalibrationType> fromType(PrimitiveType<?> code) throws FHIRException {
          if (code == null)
            return null;
          if (code.isEmpty())
            return new Enumeration<DeviceMetricCalibrationType>(this, DeviceMetricCalibrationType.NULL, code);
          String codeString = ((PrimitiveType) code).asStringValue();
          if (codeString == null || "".equals(codeString))
            return new Enumeration<DeviceMetricCalibrationType>(this, DeviceMetricCalibrationType.NULL, code);
        if ("unspecified".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationType>(this, DeviceMetricCalibrationType.UNSPECIFIED, code);
        if ("offset".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationType>(this, DeviceMetricCalibrationType.OFFSET, code);
        if ("gain".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationType>(this, DeviceMetricCalibrationType.GAIN, code);
        if ("two-point".equals(codeString))
          return new Enumeration<DeviceMetricCalibrationType>(this, DeviceMetricCalibrationType.TWOPOINT, code);
        throw new FHIRException("Unknown DeviceMetricCalibrationType code '"+codeString+"'");
        }
    public String toCode(DeviceMetricCalibrationType code) {
       if (code == DeviceMetricCalibrationType.NULL)
           return null;
       if (code == DeviceMetricCalibrationType.UNSPECIFIED)
        return "unspecified";
      if (code == DeviceMetricCalibrationType.OFFSET)
        return "offset";
      if (code == DeviceMetricCalibrationType.GAIN)
        return "gain";
      if (code == DeviceMetricCalibrationType.TWOPOINT)
        return "two-point";
      return "?";
   }
    public String toSystem(DeviceMetricCalibrationType code) {
      return code.getSystem();
      }
    }

    public enum DeviceMetricCategory {
        /**
         * Observations generated for this DeviceMetric are measured.
         */
        MEASUREMENT, 
        /**
         * Observations generated for this DeviceMetric is a setting that will influence the behavior of the Device.
         */
        SETTING, 
        /**
         * Observations generated for this DeviceMetric are calculated.
         */
        CALCULATION, 
        /**
         * The category of this DeviceMetric is unspecified.
         */
        UNSPECIFIED, 
        /**
         * added to help the parsers with the generic types
         */
        NULL;
        public static DeviceMetricCategory fromCode(String codeString) throws FHIRException {
            if (codeString == null || "".equals(codeString))
                return null;
        if ("measurement".equals(codeString))
          return MEASUREMENT;
        if ("setting".equals(codeString))
          return SETTING;
        if ("calculation".equals(codeString))
          return CALCULATION;
        if ("unspecified".equals(codeString))
          return UNSPECIFIED;
        if (Configuration.isAcceptInvalidEnums())
          return null;
        else
          throw new FHIRException("Unknown DeviceMetricCategory code '"+codeString+"'");
        }
        public String toCode() {
          switch (this) {
            case MEASUREMENT: return "measurement";
            case SETTING: return "setting";
            case CALCULATION: return "calculation";
            case UNSPECIFIED: return "unspecified";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getSystem() {
          switch (this) {
            case MEASUREMENT: return "http://hl7.org/fhir/metric-category";
            case SETTING: return "http://hl7.org/fhir/metric-category";
            case CALCULATION: return "http://hl7.org/fhir/metric-category";
            case UNSPECIFIED: return "http://hl7.org/fhir/metric-category";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDefinition() {
          switch (this) {
            case MEASUREMENT: return "Observations generated for this DeviceMetric are measured.";
            case SETTING: return "Observations generated for this DeviceMetric is a setting that will influence the behavior of the Device.";
            case CALCULATION: return "Observations generated for this DeviceMetric are calculated.";
            case UNSPECIFIED: return "The category of this DeviceMetric is unspecified.";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
            case MEASUREMENT: return "Measurement";
            case SETTING: return "Setting";
            case CALCULATION: return "Calculation";
            case UNSPECIFIED: return "Unspecified";
            case NULL: return null;
            default: return "?";
          }
        }
    }

  public static class DeviceMetricCategoryEnumFactory implements EnumFactory<DeviceMetricCategory> {
    public DeviceMetricCategory fromCode(String codeString) throws IllegalArgumentException {
      if (codeString == null || "".equals(codeString))
            if (codeString == null || "".equals(codeString))
                return null;
        if ("measurement".equals(codeString))
          return DeviceMetricCategory.MEASUREMENT;
        if ("setting".equals(codeString))
          return DeviceMetricCategory.SETTING;
        if ("calculation".equals(codeString))
          return DeviceMetricCategory.CALCULATION;
        if ("unspecified".equals(codeString))
          return DeviceMetricCategory.UNSPECIFIED;
        throw new IllegalArgumentException("Unknown DeviceMetricCategory code '"+codeString+"'");
        }
        public Enumeration<DeviceMetricCategory> fromType(PrimitiveType<?> code) throws FHIRException {
          if (code == null)
            return null;
          if (code.isEmpty())
            return new Enumeration<DeviceMetricCategory>(this, DeviceMetricCategory.NULL, code);
          String codeString = ((PrimitiveType) code).asStringValue();
          if (codeString == null || "".equals(codeString))
            return new Enumeration<DeviceMetricCategory>(this, DeviceMetricCategory.NULL, code);
        if ("measurement".equals(codeString))
          return new Enumeration<DeviceMetricCategory>(this, DeviceMetricCategory.MEASUREMENT, code);
        if ("setting".equals(codeString))
          return new Enumeration<DeviceMetricCategory>(this, DeviceMetricCategory.SETTING, code);
        if ("calculation".equals(codeString))
          return new Enumeration<DeviceMetricCategory>(this, DeviceMetricCategory.CALCULATION, code);
        if ("unspecified".equals(codeString))
          return new Enumeration<DeviceMetricCategory>(this, DeviceMetricCategory.UNSPECIFIED, code);
        throw new FHIRException("Unknown DeviceMetricCategory code '"+codeString+"'");
        }
    public String toCode(DeviceMetricCategory code) {
       if (code == DeviceMetricCategory.NULL)
           return null;
       if (code == DeviceMetricCategory.MEASUREMENT)
        return "measurement";
      if (code == DeviceMetricCategory.SETTING)
        return "setting";
      if (code == DeviceMetricCategory.CALCULATION)
        return "calculation";
      if (code == DeviceMetricCategory.UNSPECIFIED)
        return "unspecified";
      return "?";
   }
    public String toSystem(DeviceMetricCategory code) {
      return code.getSystem();
      }
    }

    public enum DeviceMetricOperationalStatus {
        /**
         * The DeviceMetric is operating and will generate Observations.
         */
        ON, 
        /**
         * The DeviceMetric is not operating.
         */
        OFF, 
        /**
         * The DeviceMetric is operating, but will not generate any Observations.
         */
        STANDBY, 
        /**
         * The DeviceMetric was entered in error.
         */
        ENTEREDINERROR, 
        /**
         * added to help the parsers with the generic types
         */
        NULL;
        public static DeviceMetricOperationalStatus fromCode(String codeString) throws FHIRException {
            if (codeString == null || "".equals(codeString))
                return null;
        if ("on".equals(codeString))
          return ON;
        if ("off".equals(codeString))
          return OFF;
        if ("standby".equals(codeString))
          return STANDBY;
        if ("entered-in-error".equals(codeString))
          return ENTEREDINERROR;
        if (Configuration.isAcceptInvalidEnums())
          return null;
        else
          throw new FHIRException("Unknown DeviceMetricOperationalStatus code '"+codeString+"'");
        }
        public String toCode() {
          switch (this) {
            case ON: return "on";
            case OFF: return "off";
            case STANDBY: return "standby";
            case ENTEREDINERROR: return "entered-in-error";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getSystem() {
          switch (this) {
            case ON: return "http://hl7.org/fhir/metric-operational-status";
            case OFF: return "http://hl7.org/fhir/metric-operational-status";
            case STANDBY: return "http://hl7.org/fhir/metric-operational-status";
            case ENTEREDINERROR: return "http://hl7.org/fhir/metric-operational-status";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDefinition() {
          switch (this) {
            case ON: return "The DeviceMetric is operating and will generate Observations.";
            case OFF: return "The DeviceMetric is not operating.";
            case STANDBY: return "The DeviceMetric is operating, but will not generate any Observations.";
            case ENTEREDINERROR: return "The DeviceMetric was entered in error.";
            case NULL: return null;
            default: return "?";
          }
        }
        public String getDisplay() {
          switch (this) {
            case ON: return "On";
            case OFF: return "Off";
            case STANDBY: return "Standby";
            case ENTEREDINERROR: return "Entered In Error";
            case NULL: return null;
            default: return "?";
          }
        }
    }

  public static class DeviceMetricOperationalStatusEnumFactory implements EnumFactory<DeviceMetricOperationalStatus> {
    public DeviceMetricOperationalStatus fromCode(String codeString) throws IllegalArgumentException {
      if (codeString == null || "".equals(codeString))
            if (codeString == null || "".equals(codeString))
                return null;
        if ("on".equals(codeString))
          return DeviceMetricOperationalStatus.ON;
        if ("off".equals(codeString))
          return DeviceMetricOperationalStatus.OFF;
        if ("standby".equals(codeString))
          return DeviceMetricOperationalStatus.STANDBY;
        if ("entered-in-error".equals(codeString))
          return DeviceMetricOperationalStatus.ENTEREDINERROR;
        throw new IllegalArgumentException("Unknown DeviceMetricOperationalStatus code '"+codeString+"'");
        }
        public Enumeration<DeviceMetricOperationalStatus> fromType(PrimitiveType<?> code) throws FHIRException {
          if (code == null)
            return null;
          if (code.isEmpty())
            return new Enumeration<DeviceMetricOperationalStatus>(this, DeviceMetricOperationalStatus.NULL, code);
          String codeString = ((PrimitiveType) code).asStringValue();
          if (codeString == null || "".equals(codeString))
            return new Enumeration<DeviceMetricOperationalStatus>(this, DeviceMetricOperationalStatus.NULL, code);
        if ("on".equals(codeString))
          return new Enumeration<DeviceMetricOperationalStatus>(this, DeviceMetricOperationalStatus.ON, code);
        if ("off".equals(codeString))
          return new Enumeration<DeviceMetricOperationalStatus>(this, DeviceMetricOperationalStatus.OFF, code);
        if ("standby".equals(codeString))
          return new Enumeration<DeviceMetricOperationalStatus>(this, DeviceMetricOperationalStatus.STANDBY, code);
        if ("entered-in-error".equals(codeString))
          return new Enumeration<DeviceMetricOperationalStatus>(this, DeviceMetricOperationalStatus.ENTEREDINERROR, code);
        throw new FHIRException("Unknown DeviceMetricOperationalStatus code '"+codeString+"'");
        }
    public String toCode(DeviceMetricOperationalStatus code) {
       if (code == DeviceMetricOperationalStatus.NULL)
           return null;
       if (code == DeviceMetricOperationalStatus.ON)
        return "on";
      if (code == DeviceMetricOperationalStatus.OFF)
        return "off";
      if (code == DeviceMetricOperationalStatus.STANDBY)
        return "standby";
      if (code == DeviceMetricOperationalStatus.ENTEREDINERROR)
        return "entered-in-error";
      return "?";
   }
    public String toSystem(DeviceMetricOperationalStatus code) {
      return code.getSystem();
      }
    }

    @Block()
    public static class DeviceMetricCalibrationComponent extends BackboneElement implements IBaseBackboneElement {
        /**
         * Describes the type of the calibration method.
         */
        @Child(name = "type", type = {CodeType.class}, order=1, min=0, max=1, modifier=false, summary=false)
        @Description(shortDefinition="unspecified | offset | gain | two-point", formalDefinition="Describes the type of the calibration method." )
        @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/metric-calibration-type")
        protected Enumeration<DeviceMetricCalibrationType> type;

        /**
         * Describes the state of the calibration.
         */
        @Child(name = "state", type = {CodeType.class}, order=2, min=0, max=1, modifier=false, summary=false)
        @Description(shortDefinition="not-calibrated | calibration-required | calibrated | unspecified", formalDefinition="Describes the state of the calibration." )
        @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/metric-calibration-state")
        protected Enumeration<DeviceMetricCalibrationState> state;

        /**
         * Describes the time last calibration has been performed.
         */
        @Child(name = "time", type = {InstantType.class}, order=3, min=0, max=1, modifier=false, summary=false)
        @Description(shortDefinition="Describes the time last calibration has been performed", formalDefinition="Describes the time last calibration has been performed." )
        protected InstantType time;

        private static final long serialVersionUID = 1163986578L;

    /**
     * Constructor
     */
      public DeviceMetricCalibrationComponent() {
        super();
      }

        /**
         * @return {@link #type} (Describes the type of the calibration method.). This is the underlying object with id, value and extensions. The accessor "getType" gives direct access to the value
         */
        public Enumeration<DeviceMetricCalibrationType> getTypeElement() { 
          if (this.type == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create DeviceMetricCalibrationComponent.type");
            else if (Configuration.doAutoCreate())
              this.type = new Enumeration<DeviceMetricCalibrationType>(new DeviceMetricCalibrationTypeEnumFactory()); // bb
          return this.type;
        }

        public boolean hasTypeElement() { 
          return this.type != null && !this.type.isEmpty();
        }

        public boolean hasType() { 
          return this.type != null && !this.type.isEmpty();
        }

        /**
         * @param value {@link #type} (Describes the type of the calibration method.). This is the underlying object with id, value and extensions. The accessor "getType" gives direct access to the value
         */
        public DeviceMetricCalibrationComponent setTypeElement(Enumeration<DeviceMetricCalibrationType> value) { 
          this.type = value;
          return this;
        }

        /**
         * @return Describes the type of the calibration method.
         */
        public DeviceMetricCalibrationType getType() { 
          return this.type == null ? null : this.type.getValue();
        }

        /**
         * @param value Describes the type of the calibration method.
         */
        public DeviceMetricCalibrationComponent setType(DeviceMetricCalibrationType value) { 
          if (value == null)
            this.type = null;
          else {
            if (this.type == null)
              this.type = new Enumeration<DeviceMetricCalibrationType>(new DeviceMetricCalibrationTypeEnumFactory());
            this.type.setValue(value);
          }
          return this;
        }

        /**
         * @return {@link #state} (Describes the state of the calibration.). This is the underlying object with id, value and extensions. The accessor "getState" gives direct access to the value
         */
        public Enumeration<DeviceMetricCalibrationState> getStateElement() { 
          if (this.state == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create DeviceMetricCalibrationComponent.state");
            else if (Configuration.doAutoCreate())
              this.state = new Enumeration<DeviceMetricCalibrationState>(new DeviceMetricCalibrationStateEnumFactory()); // bb
          return this.state;
        }

        public boolean hasStateElement() { 
          return this.state != null && !this.state.isEmpty();
        }

        public boolean hasState() { 
          return this.state != null && !this.state.isEmpty();
        }

        /**
         * @param value {@link #state} (Describes the state of the calibration.). This is the underlying object with id, value and extensions. The accessor "getState" gives direct access to the value
         */
        public DeviceMetricCalibrationComponent setStateElement(Enumeration<DeviceMetricCalibrationState> value) { 
          this.state = value;
          return this;
        }

        /**
         * @return Describes the state of the calibration.
         */
        public DeviceMetricCalibrationState getState() { 
          return this.state == null ? null : this.state.getValue();
        }

        /**
         * @param value Describes the state of the calibration.
         */
        public DeviceMetricCalibrationComponent setState(DeviceMetricCalibrationState value) { 
          if (value == null)
            this.state = null;
          else {
            if (this.state == null)
              this.state = new Enumeration<DeviceMetricCalibrationState>(new DeviceMetricCalibrationStateEnumFactory());
            this.state.setValue(value);
          }
          return this;
        }

        /**
         * @return {@link #time} (Describes the time last calibration has been performed.). This is the underlying object with id, value and extensions. The accessor "getTime" gives direct access to the value
         */
        public InstantType getTimeElement() { 
          if (this.time == null)
            if (Configuration.errorOnAutoCreate())
              throw new Error("Attempt to auto-create DeviceMetricCalibrationComponent.time");
            else if (Configuration.doAutoCreate())
              this.time = new InstantType(); // bb
          return this.time;
        }

        public boolean hasTimeElement() { 
          return this.time != null && !this.time.isEmpty();
        }

        public boolean hasTime() { 
          return this.time != null && !this.time.isEmpty();
        }

        /**
         * @param value {@link #time} (Describes the time last calibration has been performed.). This is the underlying object with id, value and extensions. The accessor "getTime" gives direct access to the value
         */
        public DeviceMetricCalibrationComponent setTimeElement(InstantType value) { 
          this.time = value;
          return this;
        }

        /**
         * @return Describes the time last calibration has been performed.
         */
        public Date getTime() { 
          return this.time == null ? null : this.time.getValue();
        }

        /**
         * @param value Describes the time last calibration has been performed.
         */
        public DeviceMetricCalibrationComponent setTime(Date value) { 
          if (value == null)
            this.time = null;
          else {
            if (this.time == null)
              this.time = new InstantType();
            this.time.setValue(value);
          }
          return this;
        }

        protected void listChildren(List<Property> children) {
          super.listChildren(children);
          children.add(new Property("type", "code", "Describes the type of the calibration method.", 0, 1, type));
          children.add(new Property("state", "code", "Describes the state of the calibration.", 0, 1, state));
          children.add(new Property("time", "instant", "Describes the time last calibration has been performed.", 0, 1, time));
        }

        @Override
        public Property getNamedProperty(int _hash, String _name, boolean _checkValid) throws FHIRException {
          switch (_hash) {
          case 3575610: /*type*/  return new Property("type", "code", "Describes the type of the calibration method.", 0, 1, type);
          case 109757585: /*state*/  return new Property("state", "code", "Describes the state of the calibration.", 0, 1, state);
          case 3560141: /*time*/  return new Property("time", "instant", "Describes the time last calibration has been performed.", 0, 1, time);
          default: return super.getNamedProperty(_hash, _name, _checkValid);
          }

        }

      @Override
      public Base[] getProperty(int hash, String name, boolean checkValid) throws FHIRException {
        switch (hash) {
        case 3575610: /*type*/ return this.type == null ? new Base[0] : new Base[] {this.type}; // Enumeration<DeviceMetricCalibrationType>
        case 109757585: /*state*/ return this.state == null ? new Base[0] : new Base[] {this.state}; // Enumeration<DeviceMetricCalibrationState>
        case 3560141: /*time*/ return this.time == null ? new Base[0] : new Base[] {this.time}; // InstantType
        default: return super.getProperty(hash, name, checkValid);
        }

      }

      @Override
      public Base setProperty(int hash, String name, Base value) throws FHIRException {
        switch (hash) {
        case 3575610: // type
          value = new DeviceMetricCalibrationTypeEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.type = (Enumeration) value; // Enumeration<DeviceMetricCalibrationType>
          return value;
        case 109757585: // state
          value = new DeviceMetricCalibrationStateEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.state = (Enumeration) value; // Enumeration<DeviceMetricCalibrationState>
          return value;
        case 3560141: // time
          this.time = TypeConvertor.castToInstant(value); // InstantType
          return value;
        default: return super.setProperty(hash, name, value);
        }

      }

      @Override
      public Base setProperty(String name, Base value) throws FHIRException {
        if (name.equals("type")) {
          value = new DeviceMetricCalibrationTypeEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.type = (Enumeration) value; // Enumeration<DeviceMetricCalibrationType>
        } else if (name.equals("state")) {
          value = new DeviceMetricCalibrationStateEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.state = (Enumeration) value; // Enumeration<DeviceMetricCalibrationState>
        } else if (name.equals("time")) {
          this.time = TypeConvertor.castToInstant(value); // InstantType
        } else
          return super.setProperty(name, value);
        return value;
      }

  @Override
  public void removeChild(String name, Base value) throws FHIRException {
        if (name.equals("type")) {
          value = new DeviceMetricCalibrationTypeEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.type = (Enumeration) value; // Enumeration<DeviceMetricCalibrationType>
        } else if (name.equals("state")) {
          value = new DeviceMetricCalibrationStateEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.state = (Enumeration) value; // Enumeration<DeviceMetricCalibrationState>
        } else if (name.equals("time")) {
          this.time = null;
        } else
          super.removeChild(name, value);
        
      }

      @Override
      public Base makeProperty(int hash, String name) throws FHIRException {
        switch (hash) {
        case 3575610:  return getTypeElement();
        case 109757585:  return getStateElement();
        case 3560141:  return getTimeElement();
        default: return super.makeProperty(hash, name);
        }

      }

      @Override
      public String[] getTypesForProperty(int hash, String name) throws FHIRException {
        switch (hash) {
        case 3575610: /*type*/ return new String[] {"code"};
        case 109757585: /*state*/ return new String[] {"code"};
        case 3560141: /*time*/ return new String[] {"instant"};
        default: return super.getTypesForProperty(hash, name);
        }

      }

      @Override
      public Base addChild(String name) throws FHIRException {
        if (name.equals("type")) {
          throw new FHIRException("Cannot call addChild on a singleton property DeviceMetric.calibration.type");
        }
        else if (name.equals("state")) {
          throw new FHIRException("Cannot call addChild on a singleton property DeviceMetric.calibration.state");
        }
        else if (name.equals("time")) {
          throw new FHIRException("Cannot call addChild on a singleton property DeviceMetric.calibration.time");
        }
        else
          return super.addChild(name);
      }

      public DeviceMetricCalibrationComponent copy() {
        DeviceMetricCalibrationComponent dst = new DeviceMetricCalibrationComponent();
        copyValues(dst);
        return dst;
      }

      public void copyValues(DeviceMetricCalibrationComponent dst) {
        super.copyValues(dst);
        dst.type = type == null ? null : type.copy();
        dst.state = state == null ? null : state.copy();
        dst.time = time == null ? null : time.copy();
      }

      @Override
      public boolean equalsDeep(Base other_) {
        if (!super.equalsDeep(other_))
          return false;
        if (!(other_ instanceof DeviceMetricCalibrationComponent))
          return false;
        DeviceMetricCalibrationComponent o = (DeviceMetricCalibrationComponent) other_;
        return compareDeep(type, o.type, true) && compareDeep(state, o.state, true) && compareDeep(time, o.time, true)
          ;
      }

      @Override
      public boolean equalsShallow(Base other_) {
        if (!super.equalsShallow(other_))
          return false;
        if (!(other_ instanceof DeviceMetricCalibrationComponent))
          return false;
        DeviceMetricCalibrationComponent o = (DeviceMetricCalibrationComponent) other_;
        return compareValues(type, o.type, true) && compareValues(state, o.state, true) && compareValues(time, o.time, true)
          ;
      }

      public boolean isEmpty() {
        return super.isEmpty() && ca.uhn.fhir.util.ElementUtil.isEmpty(type, state, time);
      }

  public String fhirType() {
    return "DeviceMetric.calibration";

  }

  }

    /**
     * Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or owners. For example, handle ID.
     */
    @Child(name = "identifier", type = {Identifier.class}, order=0, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=true)
    @Description(shortDefinition="Instance identifier", formalDefinition="Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or owners. For example, handle ID." )
    protected List<Identifier> identifier;

    /**
     * Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.
     */
    @Child(name = "type", type = {CodeableConcept.class}, order=1, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Identity of metric, for example Heart Rate or PEEP Setting", formalDefinition="Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/devicemetric-type")
    protected CodeableConcept type;

    /**
     * Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.
     */
    @Child(name = "unit", type = {CodeableConcept.class}, order=2, min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Unit of Measure for the Metric", formalDefinition="Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/ucum-units")
    protected CodeableConcept unit;

    /**
     * Describes the link to the Device.  This is also known as a channel device.
     */
    @Child(name = "device", type = {Device.class}, order=3, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="Describes the link to the Device", formalDefinition="Describes the link to the Device.  This is also known as a channel device." )
    protected Reference device;

    /**
     * Indicates current operational state of the device. For example: On, Off, Standby, etc.
     */
    @Child(name = "operationalStatus", type = {CodeType.class}, order=4, min=0, max=1, modifier=false, summary=true)
    @Description(shortDefinition="on | off | standby | entered-in-error", formalDefinition="Indicates current operational state of the device. For example: On, Off, Standby, etc." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/metric-operational-status")
    protected Enumeration<DeviceMetricOperationalStatus> operationalStatus;

    /**
     * The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.
     */
    @Child(name = "color", type = {CodeType.class}, order=5, min=0, max=1, modifier=false, summary=false)
    @Description(shortDefinition="Color name (from CSS4) or #RRGGBB code", formalDefinition="The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/color-codes")
    protected CodeType color;

    /**
     * Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.
     */
    @Child(name = "category", type = {CodeType.class}, order=6, min=1, max=1, modifier=false, summary=true)
    @Description(shortDefinition="measurement | setting | calculation | unspecified", formalDefinition="Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation." )
    @ca.uhn.fhir.model.api.annotation.Binding(valueSet="http://hl7.org/fhir/ValueSet/metric-category")
    protected Enumeration<DeviceMetricCategory> category;

    /**
     * The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring wheel). The update period may be different than the measurement frequency, if the device does not update the published observed value with the same frequency as it was measured.
     */
    @Child(name = "measurementFrequency", type = {Quantity.class}, order=7, min=0, max=1, modifier=false, summary=false)
    @Description(shortDefinition="Indicates how often the metric is taken or recorded", formalDefinition="The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring wheel). The update period may be different than the measurement frequency, if the device does not update the published observed value with the same frequency as it was measured." )
    protected Quantity measurementFrequency;

    /**
     * Describes the calibrations that have been performed or that are required to be performed.
     */
    @Child(name = "calibration", type = {}, order=8, min=0, max=Child.MAX_UNLIMITED, modifier=false, summary=false)
    @Description(shortDefinition="Describes the calibrations that have been performed or that are required to be performed", formalDefinition="Describes the calibrations that have been performed or that are required to be performed." )
    protected List<DeviceMetricCalibrationComponent> calibration;

    private static final long serialVersionUID = 2109169775L;

  /**
   * Constructor
   */
    public DeviceMetric() {
      super();
    }

  /**
   * Constructor
   */
    public DeviceMetric(CodeableConcept type, Reference device, DeviceMetricCategory category) {
      super();
      this.setType(type);
      this.setDevice(device);
      this.setCategory(category);
    }

    /**
     * @return {@link #identifier} (Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or owners. For example, handle ID.)
     */
    public List<Identifier> getIdentifier() { 
      if (this.identifier == null)
        this.identifier = new ArrayList<Identifier>();
      return this.identifier;
    }

    /**
     * @return Returns a reference to <code>this</code> for easy method chaining
     */
    public DeviceMetric setIdentifier(List<Identifier> theIdentifier) { 
      this.identifier = theIdentifier;
      return this;
    }

    public boolean hasIdentifier() { 
      if (this.identifier == null)
        return false;
      for (Identifier item : this.identifier)
        if (!item.isEmpty())
          return true;
      return false;
    }

    public Identifier addIdentifier() { //3
      Identifier t = new Identifier();
      if (this.identifier == null)
        this.identifier = new ArrayList<Identifier>();
      this.identifier.add(t);
      return t;
    }

    public DeviceMetric addIdentifier(Identifier t) { //3
      if (t == null)
        return this;
      if (this.identifier == null)
        this.identifier = new ArrayList<Identifier>();
      this.identifier.add(t);
      return this;
    }

    /**
     * @return The first repetition of repeating field {@link #identifier}, creating it if it does not already exist {3}
     */
    public Identifier getIdentifierFirstRep() { 
      if (getIdentifier().isEmpty()) {
        addIdentifier();
      }
      return getIdentifier().get(0);
    }

    /**
     * @return {@link #type} (Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.)
     */
    public CodeableConcept getType() { 
      if (this.type == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.type");
        else if (Configuration.doAutoCreate())
          this.type = new CodeableConcept(); // cc
      return this.type;
    }

    public boolean hasType() { 
      return this.type != null && !this.type.isEmpty();
    }

    /**
     * @param value {@link #type} (Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.)
     */
    public DeviceMetric setType(CodeableConcept value) { 
      this.type = value;
      return this;
    }

    /**
     * @return {@link #unit} (Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.)
     */
    public CodeableConcept getUnit() { 
      if (this.unit == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.unit");
        else if (Configuration.doAutoCreate())
          this.unit = new CodeableConcept(); // cc
      return this.unit;
    }

    public boolean hasUnit() { 
      return this.unit != null && !this.unit.isEmpty();
    }

    /**
     * @param value {@link #unit} (Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.)
     */
    public DeviceMetric setUnit(CodeableConcept value) { 
      this.unit = value;
      return this;
    }

    /**
     * @return {@link #device} (Describes the link to the Device.  This is also known as a channel device.)
     */
    public Reference getDevice() { 
      if (this.device == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.device");
        else if (Configuration.doAutoCreate())
          this.device = new Reference(); // cc
      return this.device;
    }

    public boolean hasDevice() { 
      return this.device != null && !this.device.isEmpty();
    }

    /**
     * @param value {@link #device} (Describes the link to the Device.  This is also known as a channel device.)
     */
    public DeviceMetric setDevice(Reference value) { 
      this.device = value;
      return this;
    }

    /**
     * @return {@link #operationalStatus} (Indicates current operational state of the device. For example: On, Off, Standby, etc.). This is the underlying object with id, value and extensions. The accessor "getOperationalStatus" gives direct access to the value
     */
    public Enumeration<DeviceMetricOperationalStatus> getOperationalStatusElement() { 
      if (this.operationalStatus == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.operationalStatus");
        else if (Configuration.doAutoCreate())
          this.operationalStatus = new Enumeration<DeviceMetricOperationalStatus>(new DeviceMetricOperationalStatusEnumFactory()); // bb
      return this.operationalStatus;
    }

    public boolean hasOperationalStatusElement() { 
      return this.operationalStatus != null && !this.operationalStatus.isEmpty();
    }

    public boolean hasOperationalStatus() { 
      return this.operationalStatus != null && !this.operationalStatus.isEmpty();
    }

    /**
     * @param value {@link #operationalStatus} (Indicates current operational state of the device. For example: On, Off, Standby, etc.). This is the underlying object with id, value and extensions. The accessor "getOperationalStatus" gives direct access to the value
     */
    public DeviceMetric setOperationalStatusElement(Enumeration<DeviceMetricOperationalStatus> value) { 
      this.operationalStatus = value;
      return this;
    }

    /**
     * @return Indicates current operational state of the device. For example: On, Off, Standby, etc.
     */
    public DeviceMetricOperationalStatus getOperationalStatus() { 
      return this.operationalStatus == null ? null : this.operationalStatus.getValue();
    }

    /**
     * @param value Indicates current operational state of the device. For example: On, Off, Standby, etc.
     */
    public DeviceMetric setOperationalStatus(DeviceMetricOperationalStatus value) { 
      if (value == null)
        this.operationalStatus = null;
      else {
        if (this.operationalStatus == null)
          this.operationalStatus = new Enumeration<DeviceMetricOperationalStatus>(new DeviceMetricOperationalStatusEnumFactory());
        this.operationalStatus.setValue(value);
      }
      return this;
    }

    /**
     * @return {@link #color} (The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.). This is the underlying object with id, value and extensions. The accessor "getColor" gives direct access to the value
     */
    public CodeType getColorElement() { 
      if (this.color == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.color");
        else if (Configuration.doAutoCreate())
          this.color = new CodeType(); // bb
      return this.color;
    }

    public boolean hasColorElement() { 
      return this.color != null && !this.color.isEmpty();
    }

    public boolean hasColor() { 
      return this.color != null && !this.color.isEmpty();
    }

    /**
     * @param value {@link #color} (The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.). This is the underlying object with id, value and extensions. The accessor "getColor" gives direct access to the value
     */
    public DeviceMetric setColorElement(CodeType value) { 
      this.color = value;
      return this;
    }

    /**
     * @return The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.
     */
    public String getColor() { 
      return this.color == null ? null : this.color.getValue();
    }

    /**
     * @param value The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.
     */
    public DeviceMetric setColor(String value) { 
      if (Utilities.noString(value))
        this.color = null;
      else {
        if (this.color == null)
          this.color = new CodeType();
        this.color.setValue(value);
      }
      return this;
    }

    /**
     * @return {@link #category} (Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.). This is the underlying object with id, value and extensions. The accessor "getCategory" gives direct access to the value
     */
    public Enumeration<DeviceMetricCategory> getCategoryElement() { 
      if (this.category == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.category");
        else if (Configuration.doAutoCreate())
          this.category = new Enumeration<DeviceMetricCategory>(new DeviceMetricCategoryEnumFactory()); // bb
      return this.category;
    }

    public boolean hasCategoryElement() { 
      return this.category != null && !this.category.isEmpty();
    }

    public boolean hasCategory() { 
      return this.category != null && !this.category.isEmpty();
    }

    /**
     * @param value {@link #category} (Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.). This is the underlying object with id, value and extensions. The accessor "getCategory" gives direct access to the value
     */
    public DeviceMetric setCategoryElement(Enumeration<DeviceMetricCategory> value) { 
      this.category = value;
      return this;
    }

    /**
     * @return Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.
     */
    public DeviceMetricCategory getCategory() { 
      return this.category == null ? null : this.category.getValue();
    }

    /**
     * @param value Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.
     */
    public DeviceMetric setCategory(DeviceMetricCategory value) { 
        if (this.category == null)
          this.category = new Enumeration<DeviceMetricCategory>(new DeviceMetricCategoryEnumFactory());
        this.category.setValue(value);
      return this;
    }

    /**
     * @return {@link #measurementFrequency} (The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring wheel). The update period may be different than the measurement frequency, if the device does not update the published observed value with the same frequency as it was measured.)
     */
    public Quantity getMeasurementFrequency() { 
      if (this.measurementFrequency == null)
        if (Configuration.errorOnAutoCreate())
          throw new Error("Attempt to auto-create DeviceMetric.measurementFrequency");
        else if (Configuration.doAutoCreate())
          this.measurementFrequency = new Quantity(); // cc
      return this.measurementFrequency;
    }

    public boolean hasMeasurementFrequency() { 
      return this.measurementFrequency != null && !this.measurementFrequency.isEmpty();
    }

    /**
     * @param value {@link #measurementFrequency} (The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring wheel). The update period may be different than the measurement frequency, if the device does not update the published observed value with the same frequency as it was measured.)
     */
    public DeviceMetric setMeasurementFrequency(Quantity value) { 
      this.measurementFrequency = value;
      return this;
    }

    /**
     * @return {@link #calibration} (Describes the calibrations that have been performed or that are required to be performed.)
     */
    public List<DeviceMetricCalibrationComponent> getCalibration() { 
      if (this.calibration == null)
        this.calibration = new ArrayList<DeviceMetricCalibrationComponent>();
      return this.calibration;
    }

    /**
     * @return Returns a reference to <code>this</code> for easy method chaining
     */
    public DeviceMetric setCalibration(List<DeviceMetricCalibrationComponent> theCalibration) { 
      this.calibration = theCalibration;
      return this;
    }

    public boolean hasCalibration() { 
      if (this.calibration == null)
        return false;
      for (DeviceMetricCalibrationComponent item : this.calibration)
        if (!item.isEmpty())
          return true;
      return false;
    }

    public DeviceMetricCalibrationComponent addCalibration() { //3
      DeviceMetricCalibrationComponent t = new DeviceMetricCalibrationComponent();
      if (this.calibration == null)
        this.calibration = new ArrayList<DeviceMetricCalibrationComponent>();
      this.calibration.add(t);
      return t;
    }

    public DeviceMetric addCalibration(DeviceMetricCalibrationComponent t) { //3
      if (t == null)
        return this;
      if (this.calibration == null)
        this.calibration = new ArrayList<DeviceMetricCalibrationComponent>();
      this.calibration.add(t);
      return this;
    }

    /**
     * @return The first repetition of repeating field {@link #calibration}, creating it if it does not already exist {3}
     */
    public DeviceMetricCalibrationComponent getCalibrationFirstRep() { 
      if (getCalibration().isEmpty()) {
        addCalibration();
      }
      return getCalibration().get(0);
    }

      protected void listChildren(List<Property> children) {
        super.listChildren(children);
        children.add(new Property("identifier", "Identifier", "Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or owners. For example, handle ID.", 0, java.lang.Integer.MAX_VALUE, identifier));
        children.add(new Property("type", "CodeableConcept", "Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.", 0, 1, type));
        children.add(new Property("unit", "CodeableConcept", "Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.", 0, 1, unit));
        children.add(new Property("device", "Reference(Device)", "Describes the link to the Device.  This is also known as a channel device.", 0, 1, device));
        children.add(new Property("operationalStatus", "code", "Indicates current operational state of the device. For example: On, Off, Standby, etc.", 0, 1, operationalStatus));
        children.add(new Property("color", "code", "The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.", 0, 1, color));
        children.add(new Property("category", "code", "Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.", 0, 1, category));
        children.add(new Property("measurementFrequency", "Quantity", "The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring wheel). The update period may be different than the measurement frequency, if the device does not update the published observed value with the same frequency as it was measured.", 0, 1, measurementFrequency));
        children.add(new Property("calibration", "", "Describes the calibrations that have been performed or that are required to be performed.", 0, java.lang.Integer.MAX_VALUE, calibration));
      }

      @Override
      public Property getNamedProperty(int _hash, String _name, boolean _checkValid) throws FHIRException {
        switch (_hash) {
        case -1618432855: /*identifier*/  return new Property("identifier", "Identifier", "Instance identifiers assigned to a device, by the device or gateway software, manufacturers, other organizations or owners. For example, handle ID.", 0, java.lang.Integer.MAX_VALUE, identifier);
        case 3575610: /*type*/  return new Property("type", "CodeableConcept", "Describes the type of the metric. For example: Heart Rate, PEEP Setting, etc.", 0, 1, type);
        case 3594628: /*unit*/  return new Property("unit", "CodeableConcept", "Describes the unit that an observed value determined for this metric will have. For example: Percent, Seconds, etc.", 0, 1, unit);
        case -1335157162: /*device*/  return new Property("device", "Reference(Device)", "Describes the link to the Device.  This is also known as a channel device.", 0, 1, device);
        case -2103166364: /*operationalStatus*/  return new Property("operationalStatus", "code", "Indicates current operational state of the device. For example: On, Off, Standby, etc.", 0, 1, operationalStatus);
        case 94842723: /*color*/  return new Property("color", "code", "The preferred color associated with the metric (e.g., display color). This is often used to aid clinicians to track and identify parameter types by color. In practice, consider a Patient Monitor that has ECG/HR and Pleth; the metrics are displayed in different characteristic colors, such as HR in blue, BP in green, and PR and SpO2 in magenta.", 0, 1, color);
        case 50511102: /*category*/  return new Property("category", "code", "Indicates the category of the observation generation process. A DeviceMetric can be for example a setting, measurement, or calculation.", 0, 1, category);
        case 1766341888: /*measurementFrequency*/  return new Property("measurementFrequency", "Quantity", "The frequency at which the metric is taken or recorded. Devices measure metrics at a wide range of frequencies; for example, an ECG might sample measurements in the millisecond range, while an NIBP might trigger only once an hour. Less often, the measurementFrequency may be based on a unit other than time, such as distance (e.g. for a measuring wheel). The update period may be different than the measurement frequency, if the device does not update the published observed value with the same frequency as it was measured.", 0, 1, measurementFrequency);
        case 1421318634: /*calibration*/  return new Property("calibration", "", "Describes the calibrations that have been performed or that are required to be performed.", 0, java.lang.Integer.MAX_VALUE, calibration);
        default: return super.getNamedProperty(_hash, _name, _checkValid);
        }

      }

      @Override
      public Base[] getProperty(int hash, String name, boolean checkValid) throws FHIRException {
        switch (hash) {
        case -1618432855: /*identifier*/ return this.identifier == null ? new Base[0] : this.identifier.toArray(new Base[this.identifier.size()]); // Identifier
        case 3575610: /*type*/ return this.type == null ? new Base[0] : new Base[] {this.type}; // CodeableConcept
        case 3594628: /*unit*/ return this.unit == null ? new Base[0] : new Base[] {this.unit}; // CodeableConcept
        case -1335157162: /*device*/ return this.device == null ? new Base[0] : new Base[] {this.device}; // Reference
        case -2103166364: /*operationalStatus*/ return this.operationalStatus == null ? new Base[0] : new Base[] {this.operationalStatus}; // Enumeration<DeviceMetricOperationalStatus>
        case 94842723: /*color*/ return this.color == null ? new Base[0] : new Base[] {this.color}; // CodeType
        case 50511102: /*category*/ return this.category == null ? new Base[0] : new Base[] {this.category}; // Enumeration<DeviceMetricCategory>
        case 1766341888: /*measurementFrequency*/ return this.measurementFrequency == null ? new Base[0] : new Base[] {this.measurementFrequency}; // Quantity
        case 1421318634: /*calibration*/ return this.calibration == null ? new Base[0] : this.calibration.toArray(new Base[this.calibration.size()]); // DeviceMetricCalibrationComponent
        default: return super.getProperty(hash, name, checkValid);
        }

      }

      @Override
      public Base setProperty(int hash, String name, Base value) throws FHIRException {
        switch (hash) {
        case -1618432855: // identifier
          this.getIdentifier().add(TypeConvertor.castToIdentifier(value)); // Identifier
          return value;
        case 3575610: // type
          this.type = TypeConvertor.castToCodeableConcept(value); // CodeableConcept
          return value;
        case 3594628: // unit
          this.unit = TypeConvertor.castToCodeableConcept(value); // CodeableConcept
          return value;
        case -1335157162: // device
          this.device = TypeConvertor.castToReference(value); // Reference
          return value;
        case -2103166364: // operationalStatus
          value = new DeviceMetricOperationalStatusEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.operationalStatus = (Enumeration) value; // Enumeration<DeviceMetricOperationalStatus>
          return value;
        case 94842723: // color
          this.color = TypeConvertor.castToCode(value); // CodeType
          return value;
        case 50511102: // category
          value = new DeviceMetricCategoryEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.category = (Enumeration) value; // Enumeration<DeviceMetricCategory>
          return value;
        case 1766341888: // measurementFrequency
          this.measurementFrequency = TypeConvertor.castToQuantity(value); // Quantity
          return value;
        case 1421318634: // calibration
          this.getCalibration().add((DeviceMetricCalibrationComponent) value); // DeviceMetricCalibrationComponent
          return value;
        default: return super.setProperty(hash, name, value);
        }

      }

      @Override
      public Base setProperty(String name, Base value) throws FHIRException {
        if (name.equals("identifier")) {
          this.getIdentifier().add(TypeConvertor.castToIdentifier(value));
        } else if (name.equals("type")) {
          this.type = TypeConvertor.castToCodeableConcept(value); // CodeableConcept
        } else if (name.equals("unit")) {
          this.unit = TypeConvertor.castToCodeableConcept(value); // CodeableConcept
        } else if (name.equals("device")) {
          this.device = TypeConvertor.castToReference(value); // Reference
        } else if (name.equals("operationalStatus")) {
          value = new DeviceMetricOperationalStatusEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.operationalStatus = (Enumeration) value; // Enumeration<DeviceMetricOperationalStatus>
        } else if (name.equals("color")) {
          this.color = TypeConvertor.castToCode(value); // CodeType
        } else if (name.equals("category")) {
          value = new DeviceMetricCategoryEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.category = (Enumeration) value; // Enumeration<DeviceMetricCategory>
        } else if (name.equals("measurementFrequency")) {
          this.measurementFrequency = TypeConvertor.castToQuantity(value); // Quantity
        } else if (name.equals("calibration")) {
          this.getCalibration().add((DeviceMetricCalibrationComponent) value);
        } else
          return super.setProperty(name, value);
        return value;
      }

  @Override
  public void removeChild(String name, Base value) throws FHIRException {
        if (name.equals("identifier")) {
          this.getIdentifier().remove(value);
        } else if (name.equals("type")) {
          this.type = null;
        } else if (name.equals("unit")) {
          this.unit = null;
        } else if (name.equals("device")) {
          this.device = null;
        } else if (name.equals("operationalStatus")) {
          value = new DeviceMetricOperationalStatusEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.operationalStatus = (Enumeration) value; // Enumeration<DeviceMetricOperationalStatus>
        } else if (name.equals("color")) {
          this.color = null;
        } else if (name.equals("category")) {
          value = new DeviceMetricCategoryEnumFactory().fromType(TypeConvertor.castToCode(value));
          this.category = (Enumeration) value; // Enumeration<DeviceMetricCategory>
        } else if (name.equals("measurementFrequency")) {
          this.measurementFrequency = null;
        } else if (name.equals("calibration")) {
          this.getCalibration().remove((DeviceMetricCalibrationComponent) value);
        } else
          super.removeChild(name, value);
        
      }

      @Override
      public Base makeProperty(int hash, String name) throws FHIRException {
        switch (hash) {
        case -1618432855:  return addIdentifier(); 
        case 3575610:  return getType();
        case 3594628:  return getUnit();
        case -1335157162:  return getDevice();
        case -2103166364:  return getOperationalStatusElement();
        case 94842723:  return getColorElement();
        case 50511102:  return getCategoryElement();
        case 1766341888:  return getMeasurementFrequency();
        case 1421318634:  return addCalibration(); 
        default: return super.makeProperty(hash, name);
        }

      }

      @Override
      public String[] getTypesForProperty(int hash, String name) throws FHIRException {
        switch (hash) {
        case -1618432855: /*identifier*/ return new String[] {"Identifier"};
        case 3575610: /*type*/ return new String[] {"CodeableConcept"};
        case 3594628: /*unit*/ return new String[] {"CodeableConcept"};
        case -1335157162: /*device*/ return new String[] {"Reference"};
        case -2103166364: /*operationalStatus*/ return new String[] {"code"};
        case 94842723: /*color*/ return new String[] {"code"};
        case 50511102: /*category*/ return new String[] {"code"};
        case 1766341888: /*measurementFrequency*/ return new String[] {"Quantity"};
        case 1421318634: /*calibration*/ return new String[] {};
        default: return super.getTypesForProperty(hash, name);
        }

      }

      @Override
      public Base addChild(String name) throws FHIRException {
        if (name.equals("identifier")) {
          return addIdentifier();
        }
        else if (name.equals("type")) {
          this.type = new CodeableConcept();
          return this.type;
        }
        else if (name.equals("unit")) {
          this.unit = new CodeableConcept();
          return this.unit;
        }
        else if (name.equals("device")) {
          this.device = new Reference();
          return this.device;
        }
        else if (name.equals("operationalStatus")) {
          throw new FHIRException("Cannot call addChild on a singleton property DeviceMetric.operationalStatus");
        }
        else if (name.equals("color")) {
          throw new FHIRException("Cannot call addChild on a singleton property DeviceMetric.color");
        }
        else if (name.equals("category")) {
          throw new FHIRException("Cannot call addChild on a singleton property DeviceMetric.category");
        }
        else if (name.equals("measurementFrequency")) {
          this.measurementFrequency = new Quantity();
          return this.measurementFrequency;
        }
        else if (name.equals("calibration")) {
          return addCalibration();
        }
        else
          return super.addChild(name);
      }

  public String fhirType() {
    return "DeviceMetric";

  }

      public DeviceMetric copy() {
        DeviceMetric dst = new DeviceMetric();
        copyValues(dst);
        return dst;
      }

      public void copyValues(DeviceMetric dst) {
        super.copyValues(dst);
        if (identifier != null) {
          dst.identifier = new ArrayList<Identifier>();
          for (Identifier i : identifier)
            dst.identifier.add(i.copy());
        };
        dst.type = type == null ? null : type.copy();
        dst.unit = unit == null ? null : unit.copy();
        dst.device = device == null ? null : device.copy();
        dst.operationalStatus = operationalStatus == null ? null : operationalStatus.copy();
        dst.color = color == null ? null : color.copy();
        dst.category = category == null ? null : category.copy();
        dst.measurementFrequency = measurementFrequency == null ? null : measurementFrequency.copy();
        if (calibration != null) {
          dst.calibration = new ArrayList<DeviceMetricCalibrationComponent>();
          for (DeviceMetricCalibrationComponent i : calibration)
            dst.calibration.add(i.copy());
        };
      }

      protected DeviceMetric typedCopy() {
        return copy();
      }

      @Override
      public boolean equalsDeep(Base other_) {
        if (!super.equalsDeep(other_))
          return false;
        if (!(other_ instanceof DeviceMetric))
          return false;
        DeviceMetric o = (DeviceMetric) other_;
        return compareDeep(identifier, o.identifier, true) && compareDeep(type, o.type, true) && compareDeep(unit, o.unit, true)
           && compareDeep(device, o.device, true) && compareDeep(operationalStatus, o.operationalStatus, true)
           && compareDeep(color, o.color, true) && compareDeep(category, o.category, true) && compareDeep(measurementFrequency, o.measurementFrequency, true)
           && compareDeep(calibration, o.calibration, true);
      }

      @Override
      public boolean equalsShallow(Base other_) {
        if (!super.equalsShallow(other_))
          return false;
        if (!(other_ instanceof DeviceMetric))
          return false;
        DeviceMetric o = (DeviceMetric) other_;
        return compareValues(operationalStatus, o.operationalStatus, true) && compareValues(color, o.color, true)
           && compareValues(category, o.category, true);
      }

      public boolean isEmpty() {
        return super.isEmpty() && ca.uhn.fhir.util.ElementUtil.isEmpty(identifier, type, unit, device
          , operationalStatus, color, category, measurementFrequency, calibration);
      }

  @Override
  public ResourceType getResourceType() {
    return ResourceType.DeviceMetric;
   }

 /**
   * Search parameter: <b>category</b>
   * <p>
   * Description: <b>The category of the metric</b><br>
   * Type: <b>token</b><br>
   * Path: <b>DeviceMetric.category</b><br>
   * </p>
   */
  @SearchParamDefinition(name="category", path="DeviceMetric.category", description="The category of the metric", type="token" )
  public static final String SP_CATEGORY = "category";
 /**
   * <b>Fluent Client</b> search parameter constant for <b>category</b>
   * <p>
   * Description: <b>The category of the metric</b><br>
   * Type: <b>token</b><br>
   * Path: <b>DeviceMetric.category</b><br>
   * </p>
   */
  public static final ca.uhn.fhir.rest.gclient.TokenClientParam CATEGORY = new ca.uhn.fhir.rest.gclient.TokenClientParam(SP_CATEGORY);

 /**
   * Search parameter: <b>device</b>
   * <p>
   * Description: <b>The device resource</b><br>
   * Type: <b>reference</b><br>
   * Path: <b>DeviceMetric.device</b><br>
   * </p>
   */
  @SearchParamDefinition(name="device", path="DeviceMetric.device", description="The device resource", type="reference", target={Device.class } )
  public static final String SP_DEVICE = "device";
 /**
   * <b>Fluent Client</b> search parameter constant for <b>device</b>
   * <p>
   * Description: <b>The device resource</b><br>
   * Type: <b>reference</b><br>
   * Path: <b>DeviceMetric.device</b><br>
   * </p>
   */
  public static final ca.uhn.fhir.rest.gclient.ReferenceClientParam DEVICE = new ca.uhn.fhir.rest.gclient.ReferenceClientParam(SP_DEVICE);

/**
   * Constant for fluent queries to be used to add include statements. Specifies
   * the path value of "<b>DeviceMetric:device</b>".
   */
  public static final ca.uhn.fhir.model.api.Include INCLUDE_DEVICE = new ca.uhn.fhir.model.api.Include("DeviceMetric:device").toLocked();

 /**
   * Search parameter: <b>identifier</b>
   * <p>
   * Description: <b>The identifier of the metric</b><br>
   * Type: <b>token</b><br>
   * Path: <b>DeviceMetric.identifier</b><br>
   * </p>
   */
  @SearchParamDefinition(name="identifier", path="DeviceMetric.identifier", description="The identifier of the metric", type="token" )
  public static final String SP_IDENTIFIER = "identifier";
 /**
   * <b>Fluent Client</b> search parameter constant for <b>identifier</b>
   * <p>
   * Description: <b>The identifier of the metric</b><br>
   * Type: <b>token</b><br>
   * Path: <b>DeviceMetric.identifier</b><br>
   * </p>
   */
  public static final ca.uhn.fhir.rest.gclient.TokenClientParam IDENTIFIER = new ca.uhn.fhir.rest.gclient.TokenClientParam(SP_IDENTIFIER);

 /**
   * Search parameter: <b>type</b>
   * <p>
   * Description: <b>The type of metric</b><br>
   * Type: <b>token</b><br>
   * Path: <b>DeviceMetric.type</b><br>
   * </p>
   */
  @SearchParamDefinition(name="type", path="DeviceMetric.type", description="The type of metric", type="token" )
  public static final String SP_TYPE = "type";
 /**
   * <b>Fluent Client</b> search parameter constant for <b>type</b>
   * <p>
   * Description: <b>The type of metric</b><br>
   * Type: <b>token</b><br>
   * Path: <b>DeviceMetric.type</b><br>
   * </p>
   */
  public static final ca.uhn.fhir.rest.gclient.TokenClientParam TYPE = new ca.uhn.fhir.rest.gclient.TokenClientParam(SP_TYPE);


}

