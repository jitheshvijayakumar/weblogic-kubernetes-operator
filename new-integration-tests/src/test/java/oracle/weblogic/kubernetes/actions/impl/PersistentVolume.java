// Copyright (c) 2020, Oracle Corporation and/or its affiliates.
// Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.

package oracle.weblogic.kubernetes.actions.impl;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1ObjectMetaBuilder;
import io.kubernetes.client.openapi.models.V1PersistentVolume;
import io.kubernetes.client.openapi.models.V1PersistentVolumeSpec;
import oracle.weblogic.kubernetes.actions.impl.primitive.Kubernetes;

public class PersistentVolume {

  /**
   * Create a Kubernetes Persistent Volume.
   *
   * @param persistentVolume V1PersistentVolume object containing persistent volume
   *     configuration data
   * @return true if successful, false otherwise
   * @throws ApiException if Kubernetes client API call fails
   */
  public static boolean create(oracle.weblogic.domain.PersistentVolumeClaim persistentVolume) throws ApiException {
    // PersistentVolumeClaim TestAction create()
    V1PersistentVolume v1pv = new V1PersistentVolume();

    // build metadata object
    V1ObjectMeta metadata = new V1ObjectMetaBuilder()
        .withName(persistentVolume.name()) // set PVC name
        .withNamespace(persistentVolume.namespace()) // set PVC namespace
        .build();
    // set PVC labels
    metadata.setLabels(persistentVolume.labels());

    // build spec object
    V1PersistentVolumeSpec spec = new V1PersistentVolumeSpec();
    // set spec storageclassname and accessModes
    spec.setStorageClassName(persistentVolume.storageClassName());
    spec.setAccessModes(persistentVolume.accessMode());

    v1pv.setMetadata(metadata);
    v1pv.setSpec(spec);
    return Kubernetes.createPv(v1pv);
  }

  /**
   * Delete the Kubernetes Persistent Volume.
   *
   * @param name name of the Persistent Volume
   * @return true if successful
   * @throws ApiException if Kubernetes client API call fails
   */
  public static boolean delete(String name) throws ApiException {
    return Kubernetes.deletePv(name);
  }
}
