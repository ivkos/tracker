package com.ivkos.tracker.core.models;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker used to indicate the package that contains subpackages of models. Used for Spring entity scanning.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface ModelsPackageMarker { }
