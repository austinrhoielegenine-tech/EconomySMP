package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PotentialCreators {
   public PotentialCreator propertiesBased;
   private List<PotentialCreator> explicitDelegating;
   private List<PotentialCreator> implicitDelegatingConstructors;
   private List<PotentialCreator> implicitDelegatingFactories;

   public void setPropertiesBased(MapperConfig<?> config, PotentialCreator ctor, String mode) {
      if (this.propertiesBased != null) {
         throw new IllegalArgumentException(String.format("Conflicting property-based creators: already had %s creator %s, encountered another: %s", mode, this.propertiesBased.creator(), ctor.creator()));
      } else {
         this.propertiesBased = ctor.introspectParamNames(config);
      }
   }

   public void addExplicitDelegating(PotentialCreator ctor) {
      if (this.explicitDelegating == null) {
         this.explicitDelegating = new ArrayList();
      }

      this.explicitDelegating.add(ctor);
   }

   public void setImplicitDelegating(List<PotentialCreator> implicitConstructors, List<PotentialCreator> implicitFactories) {
      this.implicitDelegatingConstructors = implicitConstructors;
      this.implicitDelegatingFactories = implicitFactories;
   }

   public boolean hasDelegating() {
      return this.explicitDelegating != null && !this.explicitDelegating.isEmpty();
   }

   public boolean hasPropertiesBased() {
      return this.propertiesBased != null;
   }

   public boolean hasPropertiesBasedOrDelegating() {
      return this.propertiesBased != null || this.explicitDelegating != null && !this.explicitDelegating.isEmpty();
   }

   public List<PotentialCreator> getExplicitDelegating() {
      return this.explicitDelegating == null ? Collections.emptyList() : this.explicitDelegating;
   }

   public List<PotentialCreator> getImplicitDelegatingFactories() {
      return this.implicitDelegatingFactories == null ? Collections.emptyList() : this.implicitDelegatingFactories;
   }

   public List<PotentialCreator> getImplicitDelegatingConstructors() {
      return this.implicitDelegatingConstructors == null ? Collections.emptyList() : this.implicitDelegatingConstructors;
   }
}
